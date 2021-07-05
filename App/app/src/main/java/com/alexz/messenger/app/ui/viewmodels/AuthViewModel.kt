package com.alexz.messenger.app.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.Data
import com.alexz.messenger.app.data.providers.imp.DaggerPhoneAuthProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.PhoneAuthCallback
import com.alexz.messenger.app.data.providers.interfaces.PhoneAuthProvider
import com.alexz.messenger.app.data.providers.interfaces.SignedInUser
import com.alexz.messenger.app.data.providers.interfaces.SignedInUserImp
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel(),PhoneAuthProvider {

    val loginStatus: LiveData<Data<SignedInUser>>
        get() = mutableLoginStatus

    val codeSendStatus: LiveData<Boolean>
        get() = mutableCodeSendStatus

    override val isAuthenticated
        get() = provider.isAuthenticated

    override fun doOnAuthenticated(action: Runnable) =provider.doOnAuthenticated(action)

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
            }).build().getPhoneAuthProvider()


    init {
        if (isAuthenticated) {
            mutableLoginStatus.postValue(Data(value = SignedInUserImp(
                    FirebaseAuth.getInstance().currentUser!!.uid,false)))
        }
        mutableCodeSendStatus.postValue(false)

    }
}