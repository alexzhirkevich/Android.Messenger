package com.community.messenger.core.providers.modules

import com.community.messenger.core.providers.base.Provider
import dagger.Binds
import dagger.Module

interface ProviderModule<IProvider : Provider, ProviderImp : Provider> {

    fun provide(provider : ProviderImp) : IProvider
}