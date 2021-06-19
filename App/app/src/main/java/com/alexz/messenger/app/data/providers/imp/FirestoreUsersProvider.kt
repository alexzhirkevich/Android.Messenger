package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.Channel
import com.alexz.messenger.app.data.entities.imp.Chat
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.entities.interfaces.IUserContainer
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
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

class FirestoreUsersProvider(
        private val chatsProvider: ChatsProvider = FirestoreChatsProvider(),
        private val channelsProvider: ChannelsProvider = FirestoreChannelsProvider()
) : UsersProvider {

    override fun getNotificationToken(userId: String): Single<String> =
            usersCollection.document(userId).get().toSingle()
                    .map { ds -> ds.getField<String>(NOTIFY_TOKEN) }

//    override fun onChannelJoin(channelId: String): Completable =
//            usersCollection.document(User().id).collection(CHANNELS).document(channelId)
//                .set(mapOf(Pair("", ""))).toCompletable()
//
//    override fun onChannelLeft(channelId: String): Completable =
//        usersCollection.document(User().id).collection(CHANNELS).document(channelId)
//                .delete().toCompletable()
//
//    override fun onChatJoin(chatId: String): Completable =
//        usersCollection.document(User().id).collection(CHATS).document(chatId)
//                .set(mapOf(Pair("", ""))).toCompletable()
//
//    override fun onChatLeft(chatId: String): Completable  =
//        usersCollection.document(User().id).collection(CHATS).document(chatId)
//                .delete().toCompletable()

    override fun get(id: String): Observable<IUser> =
            usersCollection.document(id).toObservable(User::class.java).map { it as IUser }

    override fun getAll(collection: IUserContainer, limit: Int): Observable<List<IUser>> {


        return when(collection) {
            is Chat ->
                chatsProvider.getUsers(collection.id,limit)

            is Channel ->
                channelsProvider.getUsers(collection.id,limit)

            else ->
                return Observable.error(IllegalArgumentException("Cannot get users: Incompatible sources"))
        }
    }

    override fun create(entity: IUser): Completable {
        return if (entity.id.isNotEmpty()) {
            usersCollection.document(entity.id).set(entity, SetOptions.merge()).toCompletable()
        } else {
            val doc = usersCollection.document()
            entity.id = doc.id
            doc.set(entity).toCompletable()
        }
    }

    override fun delete(entity: IUser): Completable =
            usersCollection.document(entity.id).delete().toCompletable()

    override fun leaveChannel(userId: String, channelId: String): Completable =
            Completable.complete()


    override fun leaveChat(userId: String, chatId: String): Completable =
            Completable.complete()

}