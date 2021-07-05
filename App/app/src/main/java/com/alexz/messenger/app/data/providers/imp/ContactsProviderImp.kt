package com.alexz.messenger.app.data.providers.imp

import android.content.ContentResolver
import android.provider.ContactsContract
import com.alexz.messenger.app.data.providers.interfaces.ContactsProvider
import dagger.Component
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsProviderImp @Inject constructor() : ContactsProvider {

    override fun getPhoneNumbers(contentResolver: ContentResolver): Single<List<String>> = Single.create {
        val phones = mutableSetOf<String>()
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null)
        while (cursor != null && cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val hasPhone: String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

            if (hasPhone.equals("1", ignoreCase = true)) {
                val phCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                while (phCur != null && phCur.moveToNext()) {
                    val number = phCur.getString(phCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    phones.add(number)
                }
            }
        }


        it.onSuccess(phones.filter { n -> n.startsWith("+") }.map { n ->
            n.filter { c -> c.isDigit() || c == '+' }
        })

    }
}

@Singleton
@Component
interface ContactsProviderModule{
    fun getContactsProvider() : ContactsProviderImp
}