package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import com.alexz.messenger.app.util.FirebaseUtil.CHANNELS
import com.alexz.messenger.app.util.FirebaseUtil.CHATS
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import com.alexz.messenger.app.util.firestoreObservable
import com.alexz.messenger.app.util.taskCompletable
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class FirestoreUserListProvider  : UserListProvider {

    override fun getUser(userID: String): Observable<User> = firestoreObservable(
        usersCollection.document(userID),User::class.java)

    override fun onChannelJoined(channelId: String): Completable = taskCompletable(
            usersCollection.document(User().id).collection(CHANNELS).document(channelId)
                .set(mapOf(Pair("", ""))))

    override fun onChannelLeft(channelId: String): Completable = taskCompletable(
        usersCollection.document(User().id).collection(CHANNELS).document(channelId)
                .delete())

    override fun onChatJoined(chatId: String): Completable = taskCompletable(
        usersCollection.document(User().id).collection(CHATS).document(chatId)
                .set(mapOf(Pair("", ""))))

    override fun onChatLeft(chatId: String): Completable  = taskCompletable(
        usersCollection.document(User().id).collection(CHATS).document(chatId)
                .delete())
}