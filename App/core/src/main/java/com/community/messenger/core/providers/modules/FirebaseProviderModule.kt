package com.community.messenger.core.providers.modules

import com.community.messenger.core.providers.imp.FirebaseProviderImp
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import dagger.Binds
import dagger.Module

@Module
abstract class FirebaseProviderModule : ProviderModule<FirebaseProvider,FirebaseProviderImp>{

    @Binds
    abstract override fun provide(provider: FirebaseProviderImp): FirebaseProvider
}