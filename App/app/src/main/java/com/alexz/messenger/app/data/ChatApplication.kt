package com.alexz.messenger.app.data

import android.app.Application
import android.content.Context
import android.util.Log
import com.alexz.messenger.app.data.providers.imp.DaggerPhoneAuthProviderComponent
import com.alexz.messenger.app.data.providers.imp.DaggerProfileProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.AuthProvider
import com.alexz.messenger.app.data.providers.interfaces.ProfileProvider
import com.alexz.messenger.app.util.toSingle
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.messaging.FirebaseMessaging

class ChatApplication : Application() {

    private val authProvider : AuthProvider by lazy {
        DaggerPhoneAuthProviderComponent.builder().setCallback(null).build().getPhoneAuthProvider()
    }
    private val profileProvider : ProfileProvider by lazy {
        DaggerProfileProviderComponent.create().getProfileProvider()
    }


    var isListeningForContacts = false
        private set

    override fun onCreate() {
        super.onCreate()
        AppContext = applicationContext
        setupFirebase()

//        FirebaseFirestore.getInstance().collection(USERS)
//                .document(currentUser.value!!.id)
//                .set(mapOf(ONLINE to true), SetOptions.merge())

    }

    private fun setupFirebase(){
        FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true)
        }

        FirebaseApp.initializeApp(applicationContext)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance())

        FirebaseFirestore.getInstance().firestoreSettings =
                FirebaseFirestoreSettings.Builder()
                        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build()


        FirebaseAuth.getInstance().addAuthStateListener { it ->
            if (it.currentUser!=null) {
                authProvider.setOnline(onlineNow = true, onlineOnExit = false)

                FirebaseMessaging.getInstance().token.toSingle().subscribe(
                        { token ->
                            profileProvider.setNotificationToken(token).subscribe({}, {})
                        },
                        {
                            Log.e("TOKEN",it.toString())
                        }
                )
            }
        }
    }



    override fun onTerminate() {

        authProvider.setOnline(false,false)
        super.onTerminate()
    }


    companion object {
        lateinit var AppContext : Context
            private set
//

    }
}