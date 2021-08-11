package com.community.messenger.core.providers.components

import android.content.ContentResolver
import com.community.messenger.core.providers.imp.ContactsProviderImp
import com.community.messenger.core.providers.modules.FirebaseProviderModule
import com.community.messenger.core.providers.modules.UsersProviderModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FirebaseProviderModule::class,UsersProviderModule::class])
interface ContactsProviderComponent : ProviderComponent<ContactsProviderImp>{

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun setContentResolver(contentResolver: ContentResolver) : Builder

        fun build() : ContactsProviderComponent
    }
}
