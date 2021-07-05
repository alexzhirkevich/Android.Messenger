package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.Dialog
import com.alexz.messenger.app.data.entities.imp.Group
import com.alexz.messenger.app.data.entities.interfaces.IChat
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
import dagger.Component
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatsProviderImp @Inject constructor(
        private val userListProvider: UsersProvider
) : ChatsProvider {

    override fun getAll(collection: IUser, limit: Int): Observable<List<IChat>> {
        val col = usersCollection.document(collection.id).collection(CHATS)
        val task = if (limit > -1) col.limitToLast(limit.toLong()) else col
        return task.toObservable { parse(it) }
    }

    override fun getUsers(chatId: String, limit: Int): Observable<List<IUser>> = Observable.create {

    }
    override fun get(id: String): Observable<IChat> =
            chatsCollection.document(id).toObservable { parse(it) }


    override fun create(entity: IChat): Completable {
        val doc = chatsCollection.document()
        entity.id = doc.id
        return doc.set(entity).toCompletable()
    }

    override fun delete(entity: IChat) =
            chatsCollection.document(entity.id).delete().toCompletable()

    override fun remove(id: String, collection: IUser): Completable =
            Completable.complete()

    override fun join(chatId: String): Maybe<IChat> {

        val uid = userListProvider.currentUserId
        val addUserCompletable = chatsCollection.document(chatId).collection(USERS).document(uid)
                .set(mapOf(Pair(REFERENCE, usersCollection.document(uid))), SetOptions.merge())
                .toCompletable()


//        return userListProvider.joinChat(chatId)
//                .andThen(addUserCompletable)
//                .andThen(get(chatId).singleElement())
        return Maybe.error(NotImplementedError())
    }

    private fun parse(ds : DocumentSnapshot) =
            if (ds.contains(FirebaseUtil.CREATOR_ID))
                ds.toObject(Group::class.java) as IChat
            else
                ds.toObject(Dialog::class.java) as IChat


}

@Singleton
@Component(modules = [UsersProviderModule::class])
interface ChatsProviderComponent{
    fun getChatsProvider() : ChatsProviderImp
}