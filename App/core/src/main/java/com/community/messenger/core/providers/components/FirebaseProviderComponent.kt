package com.community.messenger.core.providers.components;

import com.community.messenger.core.providers.imp.FirebaseProviderImp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface FirebaseProviderComponent : ProviderComponent<FirebaseProviderImp>