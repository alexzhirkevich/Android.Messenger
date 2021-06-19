package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.Chat
import com.alexz.messenger.app.data.entities.imp.Dialog
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.interfaces.IMessageable
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import com.alexz.messenger.app.util.FirebaseUtil
import com.alexz.messenger.app.util.FirebaseUtil.CHATS
import com.alexz.messenger.app.util.FirebaseUtil.REFERENCE
import com.alexz.messenger.app.util.FirebaseUtil.USERS
import com.alexz.messenger.app.util.FirebaseUtil.chatsCollection
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import com.alexz.messenger.app.util.toCompletable
import com.alexz.messenger.app.util.toObservable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

class FirestoreChatsProvider(
        private val userListProvider: UsersProvider = FirestoreUsersProvider()) : ChatsProvider {

    override fun getAll(collection: IUser, limit: Int): Observable<List<IMessageable>> {
        val col = usersCollection.document(collection.id).collection(CHATS)
        val task = if (limit > -1) col.limitToLast(limit.toLong()) else col
        return task.toObservable { parse(it) }
    }

    override fun getUsers(chatId: String, limit: Int): Observable<List<IUser>> =
            userListProvider.getAll(Chat(id = chatId), limit)

    override fun get(id: String): Observable<IMessageable> =
            chatsCollection.document(id).toObservable { parse(it) }


    override fun create(entity: IMessageable): Completable {
        val doc = chatsCollection.document()
        entity.id = doc.id
        return doc.set(entity).toCompletable()
    }

    override fun delete(entity: IMessageable) =
            chatsCollection.document(entity.id).delete().toCompletable()

    override fun remove(id: String, collection: IUser): Completable =
            Completable.complete()

    override fun join(chatId: String): Maybe<IMessageable> {

        val uid = User().id
        val addUserCompletable = chatsCollection.document(chatId).collection(USERS).document(uid)
                .set(mapOf(Pair(REFERENCE, usersCollection.document(uid))), SetOptions.merge())
                .toCompletable()


        return userListProvider.joinChat(chatId)
                .andThen(addUserCompletable)
                .andThen(get(chatId).singleElement())
    }

    private fun parse(ds : DocumentSnapshot) =
            if (ds.contains(FirebaseUtil.CREATOR_ID))
                ds.toObject(Chat::class.java) as IMessageable
            else
                ds.toObject(Dialog::class.java) as IMessageable


}