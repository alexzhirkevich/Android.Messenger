package com.community.messenger.core.providers.interfaces

import com.community.messenger.common.entities.interfaces.IEvent
import com.community.messenger.core.providers.base.RangeEntityProvider
import com.community.messenger.core.providers.base.SingleEntityProvider
import com.community.messenger.core.providers.base.SingleRemovable
import com.community.messenger.core.providers.imp.FirebaseProviderImp
import dagger.Component
import javax.inject.Singleton

interface EventsProvider
    : SingleEntityProvider<IEvent>,
    RangeEntityProvider<IEvent>,
    SingleRemovable

@Singleton
@Component
interface FirebaseProviderComponent{
    fun getUsersProvider() : FirebaseProviderImp
}