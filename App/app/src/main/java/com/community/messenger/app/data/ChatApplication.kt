package com.community.messenger.app.data

import android.app.Application
import android.content.Context
import com.community.messenger.core.providers.components.DaggerAppInitProviderComponent
import com.community.messenger.core.providers.components.DaggerPhoneAuthProviderComponent
import com.community.messenger.core.providers.interfaces.AppInitProvider
import com.community.messenger.core.providers.interfaces.AuthProvider
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider

class ChatApplication : Application() {

    private val authProvider : AuthProvider by lazy {
        DaggerPhoneAuthProviderComponent.builder().setCallback(null).build().getProvider()
    }

    private val appInitProvider : AppInitProvider by lazy {
        DaggerAppInitProviderComponent.create().getProvider()
    }


    var isListeningForContacts = false
        private set

    override fun onCreate() {
        super.onCreate()
        AppContext = applicationContext
        appInitProvider.init(applicationContext)
        EmojiManager.install(GoogleEmojiProvider())
//        FirebaseFirestore.getInstance().collection(USERS)
//                .document(currentUser.value!!.id)
//                .set(mapOf(ONLINE to true), SetOptions.merge())

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