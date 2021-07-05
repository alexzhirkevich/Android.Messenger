package com.alexz.messenger.app.data.providers.interfaces

interface AuthProvider {

    fun setOnline(onlineNow: Boolean, onlineOnExit : Boolean) : Boolean
    fun signOut()
    val isAuthenticated :Boolean
    fun doOnAuthenticated(action : Runnable)
}