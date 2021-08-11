package com.community.messenger.core.providers.components

import com.community.messenger.core.providers.imp.ChannelsProviderImp
import com.community.messenger.core.providers.modules.FirebaseProviderModule
import com.community.messenger.core.providers.modules.UsersProviderModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UsersProviderModule::class, FirebaseProviderModule::class])
interface ChannelsProviderComponent : ProviderComponent<ChannelsProviderImp>
