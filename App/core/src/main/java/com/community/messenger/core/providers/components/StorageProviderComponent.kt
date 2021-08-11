package com.community.messenger.core.providers.components

import android.content.Context
import com.community.messenger.core.providers.imp.StorageProviderImp
import com.community.messenger.core.providers.modules.FirebaseProviderModule
import com.community.messenger.core.providers.modules.UsersProviderModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UsersProviderModule::class,FirebaseProviderModule::class])
interface StorageProviderComponent : ProviderComponent<StorageProviderImp>{
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun setContext(context : Context) : Builder

        fun build() : StorageProviderComponent
    }
}