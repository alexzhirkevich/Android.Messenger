package com.community.messenger.app.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.community.messenger.app.data.Data
import com.community.messenger.core.providers.components.DaggerPhoneAuthProviderComponent
import com.community.messenger.core.providers.interfaces.PhoneAuthCallback
import com.community.messenger.core.providers.interfaces.PhoneAuthProvider
import com.community.messenger.core.providers.interfaces.SignedInUser
import com.community.messenger.core.providers.interfaces.SignedInUserImp
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel(),PhoneAuthProvider {

    val loginStatus: LiveData<Data<SignedInUser>>
        get() = mutableLoginStatus

    val codeSendStatus: LiveData<Boolean>
        get() = mutableCodeSendStatus

    override val isAuthenticated
        get() = provider.isAuthenticated

    override fun doOnAuthenticated(action: Runnable) =provider.doOnAuthenticated(action)

    override fun setNotificationToken(token: String) = provider.setNotificationToken(token)

    override fun setOnline(onlineNow: Boolean, onlineOnExit: Boolean) = provider.setOnline(onlineNow,onlineOnExit)

    override fun signOut() {
        provider.signOut()
        mutableLoginStatus.postValue((Data(value = null,error = null)))
        mutableCodeSendStatus.postValue(false)
    }

    override fun sendCode(activity: Activity, phone: String) = provider.sendCode(activity,phone)

    override fun verifyCode(code: String) =
            provider.verifyCode(code)



    private val mutableLoginStatus =  MutableLiveData<Data<SignedInUser>>()
    private val mutableCodeSendStatus  =  MutableLiveData<Boolean>()

    private val provider : PhoneAuthProvider =
            DaggerPhoneAuthProviderComponent.builder().setCallback(object : PhoneAuthCallback {
                override fun onCodeSend() {
                    mutableCodeSendStatus.postValue(true)
                }

                override fun onSuccess(signedInUser: SignedInUser) {
                    mutableLoginStatus.postValue(Data(value = signedInUser))
                }

                override fun onError(t: Throwable) {
                    mutableLoginStatus.postValue(Data(error = t))
                }
            }).build().getProvider()


    init {
        if (isAuthenticated) {
            mutableLoginStatus.postValue(Data(value = SignedInUserImp(
                    FirebaseAuth.getInstance().currentUser!!.uid,false)))
        }
        mutableCodeSendStatus.postValue(false)

    }
}