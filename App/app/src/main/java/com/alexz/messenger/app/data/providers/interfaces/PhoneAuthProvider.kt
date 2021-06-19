package com.alexz.messenger.app.data.providers.interfaces

import android.app.Activity
import com.alexz.messenger.app.data.entities.imp.User

interface PhoneAuthProvider {
    fun sendCode(activity: Activity, phone: String)
    fun verifyCode(code: String)
    val isAuthenticated :Boolean
    val isCodeSend : Boolean
}

interface PhoneAuthCallback{
    fun onCodeSend()
    fun onSuccess(u : User)
    fun onError(t : Throwable)
}