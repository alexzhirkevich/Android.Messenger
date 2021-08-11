package com.community.messenger.app.ui.viewmodels

import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.UsersProvider
import kotlinx.coroutines.rx2.asFlow

class UserProfileViewModel : DataViewModel<IUser>(), Initable{
    private val provider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }

    override fun init(id : String) {
        collect(provider.get(id).asFlow())
    }
}