package com.community.messenger.core.providers.interfaces

import com.community.messenger.common.entities.interfaces.IChat
import com.community.messenger.common.entities.interfaces.IMessage
import com.community.messenger.core.providers.base.DependentEntityProvider
import com.community.messenger.core.providers.base.DependentRangeEntityProvider
import com.community.messenger.core.providers.base.DependentRemovable
import io.reactivex.Observable

interface MessagesProvider :
        DependentEntityProvider<IMessage,IChat>,
        DependentRemovable<IChat>,
        DependentRangeEntityProvider<IMessage,IChat>{

    fun last(chatId: String) : Observable<out IMessage>
}