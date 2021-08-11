package com.community.messenger.core.providers.imp

import com.community.messenger.common.entities.imp.Dialog
import com.community.messenger.common.entities.imp.Group
import com.community.messenger.common.entities.interfaces.IChat
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.common.util.toCompletable
import com.community.messenger.common.util.toObservable
import com.community.messenger.core.providers.interfaces.ChatsProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.COLLECTION_CHATS
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_CREATOR_ID
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.google.firebase.firestore.DocumentSnapshot
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class ChatsProviderImp @Inject constructor(
        private val userListProvider: UsersProvider,
        private val firebaseProvider: FirebaseProvider

) : ChatsProvider {

    override fun getAll(collection: IUser, limit: Int): Observable<Collection<IChat>> {
        val col = firebaseProvider.usersCollection.document(collection.id).collection(COLLECTION_CHATS)
        val task = if (limit > -1) col.limitToLast(limit.toLong()) else col
        return task.toObservable { parse(it) }
    }

    override fun getUsers(chatId: String, limit: Int): Observable<Collection<IUser>> = Observable.create {

    }

    override fun invite(chatId: String, userId: String): Maybe<out IChat> {
        TODO("Not yet implemented")
    }

    override fun get(id: String): Observable<IChat> =
            firebaseProvider.chatsCollection.document(id).toObservable { parse(it) }


    override fun create(entity: IChat): Completable {
        val doc = firebaseProvider.chatsCollection.document()
        entity.id = doc.id
        return doc.set(entity).toCompletable()
    }

    override fun delete(entity: IChat) =
            firebaseProvider.chatsCollection.document(entity.id).delete().toCompletable()

    override fun remove(id: String, collection: IUser): Completable =
            Completable.complete()

//    override fun invite(chatId: String): Maybe<out IChat> {
//
//        val uid = userListProvider.currentUserId
//        val addUserCompletable = firebaseProvider.chatsCollection.document(chatId).collection(COLLECTION_USERS).document(uid)
//                .set(mapOf(Pair(FIELD_REFERENCE, firebaseProvider.usersCollection.document(uid))), SetOptions.merge())
//                .toCompletable()
//
//
////        return userListProvider.joinChat(chatId)
////                .andThen(addUserCompletable)
////                .andThen(get(chatId).singleElement())
//        return Maybe.error(NotImplementedError())
//    }

    private fun parse(ds : DocumentSnapshot) =
            if (ds.contains(FIELD_CREATOR_ID))
                ds.toObject(Group::class.java) as IChat
            else
                ds.toObject(Dialog::class.java) as IChat


}

