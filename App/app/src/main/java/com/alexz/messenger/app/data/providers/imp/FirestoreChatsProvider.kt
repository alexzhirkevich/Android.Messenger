package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.IEntityCollection
import com.alexz.messenger.app.data.entities.imp.Chat
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import com.alexz.messenger.app.util.FirebaseUtil.CHANNELS
import com.alexz.messenger.app.util.FirebaseUtil.CHATS
import com.alexz.messenger.app.util.FirebaseUtil.REFERENCE
import com.alexz.messenger.app.util.FirebaseUtil.USERS
import com.alexz.messenger.app.util.FirebaseUtil.channelsCollection
import com.alexz.messenger.app.util.FirebaseUtil.chatsCollection
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import com.alexz.messenger.app.util.toCompletable
import com.alexz.messenger.app.util.toObservable
import com.google.firebase.firestore.SetOptions
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

class FirestoreChatsProvider(
        private val userListProvider: UserListProvider = FirestoreUserListProvider()) : ChatsProvider {

    override fun getAll(collection: IEntityCollection, limit: Int): Observable<List<Chat>> = Observable.create {
        val col = usersCollection.document(collection.id).collection(CHATS)
        val task = if (limit > -1) col.limitToLast(limit.toLong()).get() else col.get()

        task.addOnSuccessListener { qs ->
            it.onNext(qs.documents.mapNotNull { doc -> doc.toObject(Chat::class.java) })
        }.addOnFailureListener { ex -> it.onError(ex) }
    }

    override fun getUsers(chatId: String, limit: Int): Observable<List<User>> =
            userListProvider.getAll(Chat(id = chatId), limit)

    override fun get(id: String, collectionID: String?): Observable<Chat> =
            chatsCollection.document(id).toObservable(Chat::class.java)

    override fun create(entity: Chat): Completable {

        val doc = chatsCollection.document()
        val uId = User().id

        entity.id = doc.id

        val creationCompletable = doc.set(entity).toCompletable()

        val userCompletable = doc.collection(USERS).document(uId)
                .set(mapOf(Pair(REFERENCE, usersCollection.document(uId)))).toCompletable()

        val profileCompletable = usersCollection.document(uId).collection(CHANNELS).document(entity.id)
                .set(mapOf(Pair(REFERENCE, channelsCollection.document(entity.id)))).toCompletable()

        return Completable.concatArray(creationCompletable, userCompletable, profileCompletable)
    }

    override fun delete(entity: Chat): Completable {

        val deleteCompletable = chatsCollection.document(entity.id).delete().toCompletable()

        return if (entity.creatorId == User().id)
            Completable.concatArray(deleteCompletable, remove(entity.id))
        else remove(entity.id)
    }

    override fun remove(id: String, collection: IEntityCollection?): Completable = userListProvider.onChatLeft(id)

    override fun join(chatId: String): Maybe<Chat> {

        val uid = User().id
        val addUserCompletable = chatsCollection.document(chatId).collection(USERS).document(uid)
                .set(mapOf(Pair(REFERENCE, usersCollection.document(uid))), SetOptions.merge())
                .toCompletable()


        return userListProvider.onChatJoin(chatId)
                .andThen(addUserCompletable)
                .andThen(get(chatId).singleElement())
    }
}