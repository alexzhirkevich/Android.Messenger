package com.community.messenger.core.providers.imp

import android.annotation.SuppressLint
import com.community.messenger.common.util.toObservable
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.SETTINGS
import com.community.messenger.core.providers.interfaces.SettingsChangeListener
import com.community.messenger.core.providers.interfaces.SettingsProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.google.firebase.firestore.SetOptions
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsProviderImp @Inject constructor(
    private val firebaseProvider : FirebaseProvider,
    private val usersProvider: UsersProvider,
//    private val contactsProvider: ContactsProvider
) : SettingsProvider {


    override val confidentiality: SettingsProvider.Confidentiality by lazy {
        ConfidentialityImpl()
    }


    override var isSynchronized: Boolean = false
        private set

    override fun addOnSynchronizationCompleteListener(action: Runnable) {
        if (isSynchronized)
            action.run()
        else
            completeListeners.add(action)
    }

    override fun removeOnSynchronizationCompleteListener(action: Runnable) {
        completeListeners.remove(action)
    }

    override fun addOnConfidentialitySettingsChangedListener(
        listener: SettingsChangeListener<SettingsProvider.Confidentiality>
    ) {
        confidentialityChangeListeners.add(listener)
    }

    override fun removeOnConfidentialitySettingsChangedListener(
        listener: SettingsChangeListener<SettingsProvider.Confidentiality>
    ) {
        confidentialityChangeListeners.remove(listener)
    }


    private val completeListeners = mutableListOf<Runnable>()
    private val confidentialityChangeListeners =
        mutableListOf<SettingsChangeListener<SettingsProvider.Confidentiality>>()


    @SuppressLint("CheckResult")
    inner class ConfidentialityImpl : SettingsProvider.Confidentiality {

        private val settingCollection = firebaseProvider.usersCollection
            .document(usersProvider.currentUserId).collection(SETTINGS)

        private var mBlackList: List<String> = emptyList()
        override var blackList: List<String>
            get() = mBlackList
            set(value) {
                if (mBlackList != value) {
                    settingCollection.document(S_CONFIDENTIALITY)
                        .set(S_BLACKLIST to value, SetOptions.merge())
                    mBlackList = mutableListOf<String>().apply { addAll(value) }
                }
            }

        private var mPhoneAccess: Byte = SettingsProvider.Confidentiality.ACCESS_CONTACTS
        override var phoneAccess: Byte
            get() = mPhoneAccess
            set(value) {
                if (mPhoneAccess != value) {
                    settingCollection.document(S_CONFIDENTIALITY)
                        .set(S_PHONEACCESS to value, SetOptions.merge())
                    mPhoneAccess = value
                }
            }

        private var mActivityAccess: Byte = SettingsProvider.Confidentiality.ACCESS_CONTACTS
        override var activityAccess: Byte
            get() = mActivityAccess
            set(value) {
                if (mActivityAccess != value) {
                    settingCollection.document(S_CONFIDENTIALITY)
                        .set(S_ACTIVITYACCESS to value, SetOptions.merge())
                    mActivityAccess = value
                }
            }

        private var mCallsAccess: Byte = SettingsProvider.Confidentiality.ACCESS_CONTACTS
        override var callsAccess: Byte
            get() = mCallsAccess
            set(value) {
                if (mCallsAccess != value)
                    settingCollection.document(S_CONFIDENTIALITY)
                        .set(S_CALLSACCESS to value, SetOptions.merge())
                mCallsAccess = value
            }

        private var mGroupInviteAccess: Byte = SettingsProvider.Confidentiality.ACCESS_CONTACTS
        override var groupInviteAccess: Byte
            get() = mGroupInviteAccess
            set(value) {
                if (mGroupInviteAccess != value)
                    settingCollection.document(S_CONFIDENTIALITY)
                        .set(S_GROUPINVITEACCESS to value, SetOptions.merge())
                mGroupInviteAccess = value
            }

        private var mIsContactsSynchronizationEnabled: Boolean = true
        override var isContactsSynchronizationEnabled: Boolean
            get() = mIsContactsSynchronizationEnabled
            set(value) {
                if (mIsContactsSynchronizationEnabled != value)
                    settingCollection.document(S_CONFIDENTIALITY)
                        .set(S_SYNCCONTACTS to value, SetOptions.merge())
                mIsContactsSynchronizationEnabled = value
            }

        private val S_CONFIDENTIALITY = "confidentiality"
        private val S_BLACKLIST = "blackList"
        private val S_PHONEACCESS = "phoneAccess"
        private val S_ACTIVITYACCESS = "activityAccess"
        private val S_CALLSACCESS = "callsAccess"
        private val S_GROUPINVITEACCESS = "groupInviteAccess"
        private val S_SYNCCONTACTS = "isContactsSyncEnabled"

        private var remoteConfidentialityDisposable: Disposable? = null

        init {
            remoteConfidentialityDisposable = settingCollection.document(S_CONFIDENTIALITY)
                .toObservable {
                    kotlin.runCatching {
                        mBlackList = it[S_BLACKLIST] as List<String>
                    }
                    kotlin.runCatching {
                        mPhoneAccess = (it[S_PHONEACCESS] as Long).toByte()
                    }
                    kotlin.runCatching {
                        mActivityAccess = (it[S_ACTIVITYACCESS] as Long).toByte()
                    }
                    kotlin.runCatching {
                        mCallsAccess = (it[S_CALLSACCESS] as Long).toByte()
                    }

                    kotlin.runCatching {
                        groupInviteAccess = (it[S_GROUPINVITEACCESS] as Long).toByte()
                    }
                    kotlin.runCatching {
                        mIsContactsSynchronizationEnabled = it[S_SYNCCONTACTS] as Boolean
                    }

                    confidentialityChangeListeners.forEach {
                        it.invoke(this)
                    }

                    if (!isSynchronized) {
                        isSynchronized = true
                        completeListeners.forEach {
                            it.run()
                        }
                        completeListeners.clear()
                    }
                    this
                }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .repeatUntil { isSynchronized }
                .subscribe({}, {})
        }
    }
}