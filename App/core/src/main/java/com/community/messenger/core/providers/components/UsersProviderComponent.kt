package com.community.messenger.core.providers.components

import com.community.messenger.core.providers.imp.UsersProviderImp
import com.community.messenger.core.providers.modules.FirebaseProviderModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FirebaseProviderModule::class])
interface UsersProviderComponent : ProviderComponent<UsersProviderImp>