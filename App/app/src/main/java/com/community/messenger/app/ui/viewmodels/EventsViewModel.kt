package com.community.messenger.app.ui.viewmodels

import com.community.messenger.common.entities.interfaces.IEvent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.EventsProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.community.messenger.core.providers.test.TestEventsProvider
import kotlinx.coroutines.rx2.asFlow

class EventsViewModel : DataViewModel<Collection<IEvent>>() {

    val currentUserId : String
        get() = usersProvider.currentUserId

    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }

    private val eventsProvider : EventsProvider by lazy { TestEventsProvider() }

    init {
        collect(eventsProvider.getAll(limit = 15).asFlow())
    }
}