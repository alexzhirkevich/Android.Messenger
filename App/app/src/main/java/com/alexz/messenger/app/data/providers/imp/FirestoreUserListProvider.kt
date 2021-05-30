package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.IEntityCollection
import com.alexz.messenger.app.data.entities.imp.*
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import com.alexz.messenger.app.util.FirebaseUtil.CHANNELS
import com.alexz.messenger.app.util.FirebaseUtil.CHATS
import com.alexz.messenger.app.util.FirebaseUtil.NOTIFY_TOKEN
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import com.alexz.messenger.app.util.toCompletable
import com.alexz.messenger.app.util.toObservable
import com.alexz.messenger.app.util.toSingle
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.getField
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class FirestoreUserListProvider(
        private val chatsProvider: ChatsProvider = FirestoreChatsProvider(),
        private val channelsProvider: ChannelsProvider = FirestoreChannelsProvider()
) : UserListProvider {

    override fun getNotificationToken(userId: String): Single<String> =
            usersCollection.document(userId).get().toSingle()
                    .map { ds -> ds.getField<String>(NOTIFY_TOKEN) }

    override fun onChannelJoin(channelId: String): Completable =
            usersCollection.document(User().id).collection(CHANNELS).document(channelId)
                .set(mapOf(Pair("", ""))).toCompletable()

    override fun onChannelLeft(channelId: String): Completable =
        usersCollection.document(User().id).collection(CHANNELS).document(channelId)
                .delete().toCompletable()

    override fun onChatJoin(chatId: String): Completable =
        usersCollection.document(User().id).collection(CHATS).document(chatId)
                .set(mapOf(Pair("", ""))).toCompletable()

    override fun onChatLeft(chatId: String): Completable  =
        usersCollection.document(User().id).collection(CHATS).document(chatId)
                .delete().toCompletable()

    override fun get(id: String, collectionID: String?): Observable<User> =
            usersCollection.document(id).toObservable(User::class.java)

    override fun getAll(collection: IEntityCollection, limit: Int): Observable<List<User>>{

        if (collection.containsAll(listOf(
                        Message::class.java,    // Chat
                        Post::class.java        // Channel
                ))){
           return Observable.error(IllegalArgumentException("Cannot get users from multiple sources"))
        }

        return when {
            Chat::class.java in collection ->
                chatsProvider.getUsers(collection.id,limit)

            Channel::class.java in collection ->
                channelsProvider.getUsers(collection.id,limit)

            else ->
                return Observable.error(IllegalArgumentException("Cannot get users: Incompatible sources"))
        }
    }

    override fun create(entity: User): Completable {
        return if (entity.id.isNotEmpty()) {
            usersCollection.document(entity.id).set(entity, SetOptions.merge()).toCompletable()
        } else {
            val doc = usersCollection.document()
            entity.id = doc.id
            doc.set(entity).toCompletable()
        }
    }

    override fun delete(entity: User): Completable =
            usersCollection.document(entity.id).delete().toCompletable()

    override fun remove(id: String, collection: IEntityCollection?) : Completable {
        return when {
            collection == null -> delete(User(id = id))

            collection.containsAll(listOf(Post::class.java, Message::class.java)) ->
                Completable.error(IllegalArgumentException("Cannot remove user: too many sources in collection"))

            Post::class.java in collection -> {
                channelsProvider.remove(id,collection)
            }
            Message::class.java in collection -> {
                chatsProvider.remove(id,collection)
            }
            else -> Completable.error(IllegalArgumentException("Cannot remove user: Invalid collection"))
        }
    }
}