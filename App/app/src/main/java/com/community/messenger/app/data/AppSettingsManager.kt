package com.community.messenger.app.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.community.messenger.app.BuildConfig
import com.community.messenger.core.providers.components.DaggerSettingsProviderComponent
import com.community.messenger.core.providers.interfaces.SettingsChangeListener
import com.community.messenger.core.providers.interfaces.SettingsProvider


val Context.settings : SettingsManager
    get() = AppSettingsManager(this)


val Fragment.settings : SettingsManager
    get() = requireContext().settings

@SuppressLint("StaticFieldLeak")
class AppSettingsManager(private val context: Context) : SettingsManager {

    private companion object{
        private val remoteSettingsProvider: SettingsProvider by lazy {
            DaggerSettingsProviderComponent.create().getProvider()
        }
    }


    override val  activity: SettingsManager.Activity by lazy {
        ActivityImpl(preferences)
    }
    override val  messenger: SettingsManager.Messenger by lazy {
        MessengerImpl(preferences)
    }

    override val confidentiality: SettingsProvider.Confidentiality
        get() = remoteSettingsProvider.confidentiality

    override fun addOnConfidentialitySettingsChangedListener(listener: SettingsChangeListener<SettingsProvider.Confidentiality>) {
        remoteSettingsProvider.addOnConfidentialitySettingsChangedListener(listener)
    }

    override fun removeOnConfidentialitySettingsChangedListener(listener: SettingsChangeListener<SettingsProvider.Confidentiality>) {
        remoteSettingsProvider.removeOnConfidentialitySettingsChangedListener(listener)
    }


    override val isSynchronized: Boolean
        get() = remoteSettingsProvider.isSynchronized


    override fun addOnSynchronizationCompleteListener(action: Runnable) {
        remoteSettingsProvider.addOnSynchronizationCompleteListener(action)
    }

    override fun removeOnSynchronizationCompleteListener(action: Runnable){
        remoteSettingsProvider.removeOnSynchronizationCompleteListener(action)
    }

    private val preferences: SharedPreferences =  context.getSharedPreferences(
        "${BuildConfig.APPLICATION_ID}.SHARED_PREFERENCES",
        Context.MODE_PRIVATE)





    private class ActivityImpl(private val preferences: SharedPreferences) : SettingsManager.Activity {


        override var online: Int =
            preferences.getInt(S_ONLINE, SettingsManager.Activity.ONLINE_DEFAULT)
            set(value) {
                field = value
                preferences.edit().putInt(S_ONLINE, value).apply()
            }

        companion object {
            private const val S_ONLINE = "online"
        }
    }

    private class MessengerImpl(private val preferences: SharedPreferences) : SettingsManager.Messenger {
        override var read: Int = preferences.getInt(S_READ, SettingsManager.Messenger.READ_ALL)
            set(value) {
                field = value
                preferences.edit().putInt(S_READ, value).apply()
            }

        companion object {
            private const val S_READ = "read"
        }
    }
}
