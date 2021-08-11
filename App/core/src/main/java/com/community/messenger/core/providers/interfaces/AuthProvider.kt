package com.community.messenger.core.providers.interfaces

import com.community.messenger.core.providers.base.Provider
import io.reactivex.Completable

interface AuthProvider : Provider {

    fun setOnline(onlineNow: Boolean, onlineOnExit : Boolean) : Boolean
    fun signOut()
    val isAuthenticated :Boolean
    fun doOnAuthenticated(action : Runnable)
    fun setNotificationToken(token : String) : Completable
}