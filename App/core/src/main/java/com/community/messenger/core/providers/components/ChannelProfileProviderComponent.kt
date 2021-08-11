package com.community.messenger.core.providers.components

import com.community.messenger.core.providers.imp.ChannelProfileProviderImp
import com.community.messenger.core.providers.modules.FirebaseProviderModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FirebaseProviderModule::class])
interface ChannelProfileProviderComponent : ProviderComponent<ChannelProfileProviderImp>{

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun setId(id : String) : Builder

        fun build() : ChannelProfileProviderComponent
    }
}