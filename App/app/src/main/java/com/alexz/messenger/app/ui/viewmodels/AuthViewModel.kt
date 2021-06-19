package com.alexz.messenger.app.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.Data
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.imp.PhoneAuthProviderImpl
import com.alexz.messenger.app.data.providers.interfaces.PhoneAuthCallback
import com.alexz.messenger.app.data.providers.interfaces.PhoneAuthProvider

class AuthViewModel : ViewModel(),PhoneAuthProvider{

    val loginStatus : LiveData<Data<User>>
    get() = mutableLoginStatus

    val codeSendStatus : LiveData<Boolean>
    get() = mutableCodeSendStatus

    override val isAuthenticated
        get() = provider.isAuthenticated

    override val isCodeSend: Boolean
        get() = provider.isCodeSend

    override fun sendCode(activity: Activity, phone: String) = provider.sendCode(activity,phone)


    override fun verifyCode(code: String) =
            provider.verifyCode(code)


    private val mutableLoginStatus =  MutableLiveData<Data<User>>()
    private val mutableCodeSendStatus  =  MutableLiveData<Boolean>()

    private val provider : PhoneAuthProvider = PhoneAuthProviderImpl(object : PhoneAuthCallback {
        override fun onCodeSend() {
            mutableCodeSendStatus.postValue(true)
        }

        override fun onSuccess(u: User) {
            mutableLoginStatus.postValue(Data(value = u))
        }

        override fun onError(t: Throwable) {
            mutableLoginStatus.postValue(Data(error = t))
        }
    })

    init {
        if (isAuthenticated) {
            mutableLoginStatus.postValue(Data(value = User()))
        }
        mutableCodeSendStatus.postValue(false)
    }
}