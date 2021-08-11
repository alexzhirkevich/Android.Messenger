package com.community.messenger.core.providers.components

import com.community.messenger.core.providers.imp.AppInitProviderImp
import com.community.messenger.core.providers.modules.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AuthProviderModule::class,
    UsersProviderModule::class,
    FirebaseProviderModule::class,
    SettingsProviderModule::class,
    ContactsProviderModule::class
])
interface AppInitProviderComponent : ProviderComponent<AppInitProviderImp>