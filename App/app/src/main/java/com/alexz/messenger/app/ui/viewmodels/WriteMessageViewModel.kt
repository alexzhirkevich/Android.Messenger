package com.alexz.messenger.app.ui.viewmodels

import android.Manifest
import android.content.ContentResolver
import androidx.annotation.RequiresPermission
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.imp.DaggerContactsProviderModule
import com.alexz.messenger.app.data.providers.imp.DaggerUsersProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.ContactsProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import io.reactivex.schedulers.Schedulers

class WriteMessageViewModel : DataViewModel<List<IUser>?>() {

    private val contactsProvider : ContactsProvider by lazy {
        DaggerContactsProviderModule.create().getContactsProvider()
    }

    private val userProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getUsersProvider()
    }

    @RequiresPermission(value = Manifest.permission.READ_CONTACTS)
    fun init(contentResolver : ContentResolver) {
        observe(contactsProvider.getPhoneNumbers(contentResolver)
                .flatMapObservable {
                    userProvider.findByPhone(*it.toTypedArray())
                }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()))
    }
}