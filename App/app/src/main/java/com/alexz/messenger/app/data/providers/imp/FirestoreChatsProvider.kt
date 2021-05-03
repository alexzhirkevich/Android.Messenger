package com.alexz.messenger.app.data.providers.imp

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
import com.alexz.messenger.app.util.InterruptingThrowable
import com.alexz.messenger.app.util.complete
import com.alexz.messenger.app.util.firestoreObservable
import com.alexz.messenger.app.util.taskCompletable
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class FirestoreChatsProvider(
        private val userListProvider: UserListProvider = FirestoreUserListProvider()) : ChatsProvider {

    override fun getChats(userId: String): Observable<List<String>> = Observable.create {
        usersCollection.document(userId).collection(CHATS).get()
                .addOnSuccessListener { qs ->
                    it.onNext(qs.documents.mapNotNull { doc -> doc.id })
                }.addOnFailureListener { ex -> it.onError(ex) }
    }

    override fun getUsers(channelId: String): Observable<List<String>> = Observable.create {
        channelsCollection.document(channelId).collection(USERS).get()
                .addOnSuccessListener { qs ->
                    it.onNext(qs.documents.mapNotNull { doc -> doc.id })
                }.addOnFailureListener { ex -> it.onError(ex) }
    }

    override fun getChat(chatId: String): Observable<Chat> =
            firestoreObservable(chatsCollection.document(chatId), Chat::class.java)

    override fun createChat(chat: Chat): Completable {

        val doc = chatsCollection.document()
        val uId = User().id

        chat.id = doc.id

        val creationCompletable = Completable.create { it.complete(doc.set(chat)) }

        val userCompletable = taskCompletable(doc.collection(USERS).document(uId)
                .set(mapOf(Pair(REFERENCE, usersCollection.document(uId)))))

        val profileCompletable = taskCompletable(usersCollection.document(uId).collection(CHANNELS).document(chat.id)
                .set(mapOf(Pair(REFERENCE, channelsCollection.document(chat.id)))))

        return Completable.concatArray(creationCompletable, userCompletable, profileCompletable)
    }

    override fun removeChat(chatId: String): Completable {

        val completable = Completable.create {
            getChat(chatId).subscribe({ c ->
                if (c.creatorId == User().id) {
                    it.complete(chatsCollection.document(c.id).delete())
                } else it.onComplete()
            },
                    { t -> it.tryOnError(t) })
        }

        return Completable.concatArray(completable, userListProvider.onChatLeft(chatId))
    }

    override fun joinChat(chatId: String): Single<Chat> {

        val uid = User().id
        val doc = chatsCollection.document(chatId).collection(USERS).document(uid)

        val check1 = Completable.create {
            getChats(uid).singleElement().doOnSuccess { list ->
                list.find { id -> id == chatId }?.let { _ -> it.onComplete() }
                        ?: it.tryOnError(InterruptingThrowable)
            }.subscribe()
        }

        var chat: Chat? = null

        val check2 = Completable.create {
            doc.get().addOnSuccessListener { ds ->
                if (ds.exists()) {
                    chat = ds.toObject(Chat::class.java)
                    it.onComplete()
                } else {
                    it.tryOnError(InterruptingThrowable)
                }
            }.addOnFailureListener { _ ->
                it.tryOnError(InterruptingThrowable)
            }
        }

        val joinCompletable = Completable.concatArray(
                taskCompletable(doc.set(mapOf(Pair(REFERENCE, usersCollection.document(uid))))),
                userListProvider.onChannelJoined(chatId))

        return Single.create<Chat> {
            Completable.concatArray(check1, check2)
                    .doOnError { ex ->
                        if (ex is InterruptingThrowable) {
                            joinCompletable.subscribe(
                                    {
                                        chat?.let { c -> it.onSuccess(c) }
                                                ?: it.tryOnError(NullPointerException())
                                    },
                                    { t -> it.tryOnError(t) })
                        } else {
                            it.tryOnError(ex)
                        }
                    }.subscribe()
        }
    }
}