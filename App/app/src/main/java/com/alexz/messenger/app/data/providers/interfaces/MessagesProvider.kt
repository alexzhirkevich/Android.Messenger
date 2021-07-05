package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.interfaces.IMessage
import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.data.providers.base.DependentEntityProvider
import com.alexz.messenger.app.data.providers.base.DependentRangeEntityProvider
import com.alexz.messenger.app.data.providers.base.DependentRemovable
import io.reactivex.Observable

interface MessagesProvider :
        DependentEntityProvider<IMessage,IChat>,
        DependentRemovable<IChat>,
        DependentRangeEntityProvider<IMessage,IChat>{

    fun last(chatId: String) : Observable<IMessage>
}