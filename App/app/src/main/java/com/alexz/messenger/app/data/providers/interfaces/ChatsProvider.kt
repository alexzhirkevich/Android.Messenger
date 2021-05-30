package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.Chat
import com.alexz.messenger.app.data.entities.imp.User
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

interface ChatsProvider : EntityProvider<Chat> {

    fun getUsers(chatId : String,limit : Int = 30) : Observable<List<User>>

    fun join(chatId: String): Maybe<Chat>
}