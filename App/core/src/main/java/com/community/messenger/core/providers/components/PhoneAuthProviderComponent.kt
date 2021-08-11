package com.community.messenger.core.providers.components

import com.community.messenger.core.providers.imp.PhoneAuthProviderImp
import com.community.messenger.core.providers.interfaces.PhoneAuthCallback
import com.community.messenger.core.providers.modules.FirebaseProviderModule
import com.community.messenger.core.providers.modules.UsersProviderModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UsersProviderModule::class,FirebaseProviderModule::class])
interface PhoneAuthProviderComponent : ProviderComponent<PhoneAuthProviderImp>{

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun setCallback(callback : PhoneAuthCallback?) : Builder

        fun build() : PhoneAuthProviderComponent
    }
}

