package com.community.messenger.core.providers.modules

import com.community.messenger.core.providers.imp.ContactsProviderImp
import com.community.messenger.core.providers.interfaces.ContactsProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ContactsProviderModule : ProviderModule<ContactsProvider,ContactsProviderImp>{

    @Binds
    abstract override fun provide(provider: ContactsProviderImp): ContactsProvider
}