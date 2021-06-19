package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.interfaces.IMessage
import com.alexz.messenger.app.data.entities.interfaces.IMessageable
import com.alexz.messenger.app.data.providers.base.DependentEntityProvider
import com.alexz.messenger.app.data.providers.base.DependentRangeEntityProvider
import com.alexz.messenger.app.data.providers.base.DependentRemovable
import io.reactivex.Observable

interface MessagesProvider :
        DependentEntityProvider<IMessage,IMessageable>,
        DependentRemovable<IMessageable>,
        DependentRangeEntityProvider<IMessage,IMessageable>{

    fun last(chatId: String) : Observable<IMessage>
}