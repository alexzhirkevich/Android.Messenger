package com.community.messenger.core.providers.interfaces

import com.community.messenger.common.entities.interfaces.IChat
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.core.providers.base.DependentRangeEntityProvider
import com.community.messenger.core.providers.base.DependentRemovable
import com.community.messenger.core.providers.base.SingleEntityProvider
import io.reactivex.Maybe
import io.reactivex.Observable

interface ChatsProvider :
        SingleEntityProvider<IChat>,
        DependentRangeEntityProvider<IChat,IUser>,
        DependentRemovable<IUser>{

    fun getUsers(chatId : String,limit : Int = 30) : Observable<Collection<IUser>>

    fun invite(chatId: String,userId : String): Maybe<out IChat>
}