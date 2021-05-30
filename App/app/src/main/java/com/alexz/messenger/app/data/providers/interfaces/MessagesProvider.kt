package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.Message
import io.reactivex.Observable

interface MessagesProvider : EntityProvider<Message> {

    fun last(chatId: String) : Observable<Message>
}