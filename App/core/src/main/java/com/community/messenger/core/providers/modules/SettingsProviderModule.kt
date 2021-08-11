package com.community.messenger.core.providers.modules

import com.community.messenger.core.providers.imp.SettingsProviderImp
import com.community.messenger.core.providers.interfaces.SettingsProvider
import dagger.Binds
import dagger.Module

@Module
abstract class SettingsProviderModule : ProviderModule<SettingsProvider,SettingsProviderImp>{
    @Binds
    abstract override fun provide(provider: SettingsProviderImp): SettingsProvider
}
