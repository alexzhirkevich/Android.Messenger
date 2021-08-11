package com.community.messenger.core.providers.imp

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import com.community.messenger.common.entities.imp.Contact
import com.community.messenger.common.entities.interfaces.IContact
import com.community.messenger.common.util.invoke
import com.community.messenger.common.util.toCompletable
import com.community.messenger.common.util.toSingle
import com.community.messenger.core.providers.interfaces.ContactsProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.COLLECTION_CONTACTS
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.google.firebase.firestore.CollectionReference
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsProviderImp @Inject constructor(
    private val contentResolver: ContentResolver,
    private val firebaseProvider: FirebaseProvider,
    private val usersProvider: UsersProvider,
) : ContactsProvider {

    override var isSynchronizationEnabled: Boolean = false
        set(value) {
            if  (field != value) {
                field = value
                if (value) {
                    syncDisposable?.dispose()
                    syncDisposable = getAll(limit = -1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe({ synchronize(it) }, {})
                } else{
                    clearDisposable?.dispose()
                    clearDisposable = clear().invoke()
                }
            }
        }

    override fun getAll(limit: Int): Observable<Collection<IContact>> {

        return ContactsObservable(contentResolver, limit, Handler(Looper.getMainLooper())).apply {
            doOnDispose(onDispose)
        }

    }

    override fun dispose() {
        syncDisposable?.dispose()
        clearDisposable?.dispose()
    }

    override fun isDisposed(): Boolean =
        syncDisposable?.isDisposed == true && clearDisposable?.isDisposed == true


    override fun get(id: String): Observable<IContact> {
        TODO("Not yet implemented")
    }

    override fun create(entity: IContact): Completable {
        TODO("Not yet implemented")
    }

    override fun delete(entity: IContact): Completable {
        TODO("Not yet implemented")
    }

    private val contactsCollection : CollectionReference by lazy {
        firebaseProvider.usersCollection.document(usersProvider.currentUserId)
            .collection(COLLECTION_CONTACTS)
    }

    private var syncDisposable : Disposable? =null
    private var clearDisposable : Disposable? =null

    private fun synchronize(contacts: Collection<IContact>): Completable =
        Completable.merge(contacts.map {
            contactsCollection.document(it.phone).set(it).toCompletable()
        })

    private fun clear() : Completable =
        contactsCollection.get().toSingle().flatMapCompletable {
            Completable.merge(
                it.documents.map { doc ->
                    contactsCollection.document(doc.id).delete().toCompletable()
                }
            )
        }



    private class ContactsObservable(
        private val contentResolver: ContentResolver,
        private val limit : Int,
        private val handler : Handler)
        : Observable<Collection<IContact>>() {

        var observer: Observer<in Collection<IContact>>? = null

        val contentObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                update()
            }
        }

        val onDispose = Action {
            contentResolver.unregisterContentObserver(contentObserver)
        }

        override fun subscribeActual(observer: Observer<in Collection<IContact>>?) {
            this.observer = observer
            update()
            contentResolver.registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,true,contentObserver)
        }

        private fun update() {
            val contacts = mutableSetOf<IContact>()

            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null
            )
            while (cursor != null && cursor.moveToNext() && (contacts.size < limit || limit == -1)) {
                val id =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val hasPhone: String =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhone.equals("1", ignoreCase = true)) {
                    val phCur = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null,
                        null
                    )
                    while (phCur != null && phCur.moveToNext() && (contacts.size < limit || limit == -1)) {
                        val number =
                            phCur.getString(phCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val name =
                            phCur.getString(phCur.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME))
                        contacts.add(Contact(name = name, phone = number))
                    }
                }
            }

            observer?.onNext(
                contacts
                    .filter { contact -> contact.phone.startsWith("+") }
                    .onEach { contact ->
                        contact.phone = contact.phone.filter { phone -> phone.isDigit() || phone == '+' }
                    }
            )
        }
    }
}
