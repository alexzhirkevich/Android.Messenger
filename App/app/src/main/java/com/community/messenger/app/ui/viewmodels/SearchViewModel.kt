package com.community.messenger.app.ui.viewmodels

import com.community.messenger.app.data.ChatApplication.Companion.AppContext
import com.community.messenger.common.entities.imp.User
import com.community.messenger.common.entities.interfaces.IListable
import com.community.messenger.common.util.FlowableDelayedTask
import com.community.messenger.common.util.merge
import com.community.messenger.core.providers.components.DaggerChannelsProviderComponent
import com.community.messenger.core.providers.components.DaggerContactsProviderComponent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.ChannelsProvider
import com.community.messenger.core.providers.interfaces.ContactsProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import kotlinx.coroutines.rx2.asFlow

class SearchViewModel: DataViewModel<Collection<IListable>>() {


    private lateinit var query : String

    fun search(query : String){
        this.query = query
        searchTask.updateCountdown()
    }

    private val searchTask = FlowableDelayedTask(SEARCH_DELAY){

        val contacts =  contactsProvider.getAll(limit = -1)
            .flatMap { usersProvider.findByPhone(*it.map { it.phone}.toTypedArray()) }
            .map { users ->
                users.filter { user ->
                    user.name.contains(query,true) || user.phone.contains(query,true)
                }.map { it }
            }

        val selfChannels = channelsProvider.getAll(User(id = usersProvider.currentUserId),limit = -1)
            .map {channels ->
                channels.filter { channel ->
                    channel.name.contains(query,true) || channel.tag.contains(query,true)
                }.map { it }
            }

        val otherUsers = usersProvider.findByUsernameNearly(query).map { it.map { it } }

        val otherChannels = channelsProvider.find(query).map { it.map { it } }


        collect(listOf(
            contacts,selfChannels,otherUsers,otherChannels
        ).merge().map { it.flatten() }
            .map { it }.asFlow())
    }

    private val usersProvider: UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }

    private val channelsProvider : ChannelsProvider by lazy {
        DaggerChannelsProviderComponent.create().getProvider()
    }

    private val contactsProvider : ContactsProvider by lazy {
        DaggerContactsProviderComponent.builder()
            .setContentResolver(AppContext.contentResolver)
            .build().getProvider()
    }

    private companion object{
        private const val SEARCH_DELAY = 500L
    }
}