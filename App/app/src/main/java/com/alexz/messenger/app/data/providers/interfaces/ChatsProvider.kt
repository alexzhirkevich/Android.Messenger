package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.base.DependentRangeEntityProvider
import com.alexz.messenger.app.data.providers.base.DependentRemovable
import com.alexz.messenger.app.data.providers.base.SingleEntityProvider
import io.reactivex.Maybe
import io.reactivex.Observable

interface ChatsProvider :
        SingleEntityProvider<IChat>,
        DependentRangeEntityProvider<IChat,IUser>,
        DependentRemovable<IUser>{

    fun getUsers(chatId : String,limit : Int = 30) : Observable<List<IUser>>

    fun join(chatId: String): Maybe<IChat>
}