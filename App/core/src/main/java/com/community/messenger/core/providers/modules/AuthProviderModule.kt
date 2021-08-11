package com.community.messenger.core.providers.modules

import com.community.messenger.core.providers.imp.AuthProviderImp
import com.community.messenger.core.providers.interfaces.AuthProvider
import dagger.Binds
import dagger.Module

@Module
abstract class AuthProviderModule : ProviderModule<AuthProvider, AuthProviderImp> {

    @Binds
    abstract override fun provide(provider: AuthProviderImp): AuthProvider
}