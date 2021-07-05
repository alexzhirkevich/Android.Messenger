package com.alexz.messenger.app.ui.viewmodels

import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.imp.UsersProviderImp
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider

class UserProfileViewModel : DataViewModel<IUser>(){
    private val provider : UsersProvider by lazy { UsersProviderImp() }

    fun init(id : String) : UserProfileViewModel {
        observe(provider.get(id))
        return this
    }
}