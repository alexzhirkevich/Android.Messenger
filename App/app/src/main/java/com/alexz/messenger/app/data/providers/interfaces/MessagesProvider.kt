package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.Message
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface MessagesProvider {

    fun getMessage(chatId: String, msgId: String) : Observable<Message>

    fun sendMessage(message: Message): Completable

    fun lastMessage(chatId: String) : Observable<Message>

    fun deleteMessage(chatId : String, msgId : String) : Completable
}