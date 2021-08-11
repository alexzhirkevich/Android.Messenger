package com.community.messenger.app.ui.viewmodels

import android.Manifest
import android.content.ContentResolver
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.community.messenger.common.entities.interfaces.IGroup
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.core.providers.components.DaggerChatsProviderComponent
import com.community.messenger.core.providers.components.DaggerContactsProviderComponent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.ChatsProvider
import com.community.messenger.core.providers.interfaces.ContactsProvider
import com.community.messenger.core.providers.interfaces.SettingsProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await

class ContactsViewModel(
    private val contentResolver: ContentResolver,
    private val settingsManager : SettingsProvider
) : DataViewModel<Collection<IUser>?>() {

    suspend fun createGroup(group : IGroup, usersToInvite : Collection<IUser>) {
        chatsProvider.create(group).await()
        viewModelScope.launch {
            usersToInvite.map {
                async { chatsProvider.invite(group.id, it.id) }
            }.forEach {
                try{
                    it.await()
                }catch (t : Throwable){
                    Log.e(javaClass.simpleName,"Failed to invite user. Group id: ${group.id}, User id : $",t)
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        contactsProvider.dispose()
    }

    private val contactsProvider: ContactsProvider by lazy {
        DaggerContactsProviderComponent.builder()
            .setContentResolver(contentResolver)
            .build().getProvider()
    }

    private val userProvider: UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }

    private val chatsProvider : ChatsProvider by lazy {
        DaggerChatsProviderComponent.create().getProvider()
    }


    private var syncAction : Runnable?= null

    init {
        collect(contactsProvider.getAll(limit = -1)
            .flatMap {
                try {
                    syncAction?.let { action ->
                        settingsManager.removeOnSynchronizationCompleteListener(
                            action
                        )
                    }

                    syncAction = Runnable {
                        contactsProvider.isSynchronizationEnabled =
                            settingsManager.confidentiality.isContactsSynchronizationEnabled
                    }

                    if (settingsManager.isSynchronized)
                        syncAction!!.run()
                    else settingsManager.addOnSynchronizationCompleteListener(syncAction!!)
                }catch (t : Throwable) {
                    Log.e(javaClass.simpleName, "Failed to sync contacts", t)
                }

                userProvider.findByPhone(*it.map { c -> c.phone }.toTypedArray())
            }.asFlow())
    }
}


class ContactsViewModelFactory(
    private val contentResolver: ContentResolver,
    private val settingsManager: SettingsProvider
) : ViewModelProvider.Factory{

    @RequiresPermission(value = Manifest.permission.READ_CONTACTS)
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ContactsViewModel(contentResolver,settingsManager) as T
}