package com.community.messenger.core.providers.modules

import com.community.messenger.core.providers.imp.UsersProviderImp
import com.community.messenger.core.providers.interfaces.UsersProvider
import dagger.Binds
import dagger.Module

@Module
abstract class UsersProviderModule : ProviderModule<UsersProvider,UsersProviderImp> {

    @Binds
    abstract override fun provide(provider: UsersProviderImp): UsersProvider
}