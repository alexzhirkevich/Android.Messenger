package com.alexz.messenger.app.data.repo

import com.alexz.messenger.app.data.LocalDatabase
import com.alexz.messenger.app.data.entities.dao.ChatsDao
import com.alexz.messenger.app.data.entities.imp.Chat
import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class ChatsRepository(
        private val chatsProvider: ChatsProvider = FirestoreChatsProvider(),
        private val chatsDao: ChatsDao = LocalDatabase.INSTANCE.chatsDao()
) : ChatsProvider by chatsProvider{
    override fun getChats(userId: String): Observable<List<String>> = Observable.concatArray(
            chatsDao.getAll()
                    .toObservable()
                    .flatMapIterable { l -> l }
                    .map { c -> c.id}
                    .toList()
                    .toObservable(),
            chatsProvider.getChats(userId)
    )

    override fun getChat(chatId: String): Observable<Chat> =
            chatsDao.get(chatId).toObservable().concatWith(chatsProvider.getChat(chatId))

    override fun createChat(chat: Chat): Completable =
            chatsProvider.createChat(chat).andThen(chatsDao.add(chat))

    override fun removeChat(chatId: String): Completable =
            chatsProvider.removeChat(chatId).andThen(chatsDao.delete(chatId))

    override fun joinChat(chatId: String): Single<Chat> =
            chatsProvider.joinChat(chatId)
}