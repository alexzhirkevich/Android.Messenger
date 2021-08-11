package com.community.messenger.core.providers.imp

import android.content.Context
import android.util.Log
import com.community.messenger.common.util.toSingle
import com.community.messenger.core.providers.interfaces.AppInitProvider
import com.community.messenger.core.providers.interfaces.AuthProvider
import com.community.messenger.core.providers.interfaces.SettingsProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitProviderImp @Inject constructor(
    private val authProvider : AuthProvider,
    private val settingsProvider: SettingsProvider,

) : AppInitProvider {

    override fun init(context: Context) {

        initApp(context)
        initDatabase()
        initFirestore()
        initAppCheck()
        initAuth()
    }

    private fun initApp(context: Context){
        FirebaseApp.initializeApp(context)
    }

    private fun initDatabase(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }

    private fun initFirestore(){
        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
    }

    private fun initAppCheck(){
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance())
    }

    private fun initAuth(){
        FirebaseAuth.getInstance().addAuthStateListener { it ->
            if (it.currentUser!=null) {
                authProvider.setOnline(onlineNow = true, onlineOnExit = false)

                FirebaseMessaging.getInstance().token.toSingle().flatMapCompletable {
                    authProvider.setNotificationToken(it)
                }.subscribeOn(Schedulers.io()).subscribe(
                    {},
                    { Log.wtf("Failed to update notification token",it)  }
                )

            }
        }
    }
}