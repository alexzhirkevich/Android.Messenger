package com.alexz.messenger.app.data.repo

import com.alexz.messenger.app.data.LocalDatabase
import com.alexz.messenger.app.data.entities.dao.MessagesDao
import com.alexz.messenger.app.data.entities.imp.Message
import com.alexz.messenger.app.data.providers.imp.FirestoreMessagesProvider
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.util.with
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class MessagesRepository(
        private val provider: MessagesProvider = FirestoreMessagesProvider(),
        private val dao : MessagesDao = LocalDatabase.INSTANCE.messagesDao()) : MessagesProvider {

    override fun getMessage(chatId: String, msgId: String): Observable<Message> = Observable.concatArray(
            dao.get(msgId).toObservable(),provider.getMessage(chatId,msgId)
    )

    override fun sendMessage(message: Message): Completable =
            provider.sendMessage(message).andThen { dao.add(message) }

    override fun lastMessage(chatId: String): Observable<Message> = Observable.create {
        dao.lastMessageId(chatId).subscribe(
                { id ->
                    dao.get(id).subscribe(
                            { msg ->
                                it.onNext(msg)
                                it.with(provider.lastMessage(chatId))
                            },
                            { _ -> it.with(provider.lastMessage(chatId)) }
                    )
                },
                { _ -> it.with(provider.lastMessage(chatId)) })
    }

    override fun deleteMessage(chatId: String, msgId: String): Completable =
            provider.deleteMessage(chatId,msgId).andThen { dao.delete(msgId) }
}