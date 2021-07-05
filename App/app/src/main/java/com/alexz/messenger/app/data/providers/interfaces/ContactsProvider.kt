package com.alexz.messenger.app.data.providers.interfaces

import android.content.ContentResolver
import io.reactivex.Single

interface ContactsProvider {

    fun getPhoneNumbers(contentResolver: ContentResolver) : Single<List<String>>

}