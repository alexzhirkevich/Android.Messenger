package com.alexz.messenger.app.data.providers.imp

import android.app.Activity
import com.alexz.messenger.app.data.providers.interfaces.PhoneAuthCallback
import com.alexz.messenger.app.data.providers.interfaces.PhoneAuthProvider
import com.alexz.messenger.app.data.providers.interfaces.SignedInUserImp
import com.alexz.messenger.app.util.FirebaseUtil.ONLINE
import com.alexz.messenger.app.util.FirebaseUtil.USERS
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.BindsInstance
import dagger.Component
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.google.firebase.auth.PhoneAuthProvider as FirebasePhoneAuthProvider


class PhoneAuthProviderImp @Inject constructor(val callback : PhoneAuthCallback?) : PhoneAuthProvider {

    override val isAuthenticated : Boolean
        get() = FirebaseAuth.getInstance().currentUser != null

    override fun signOut() = FirebaseAuth.getInstance().signOut()

    override fun sendCode(activity: Activity, phone: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(mCallback)
                .apply {
                    forceResendingToken?.let { setForceResendingToken(it) }
                }
                .build()
        FirebasePhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyCode(code: String) {
        if (verificationId == null){
            callback?.onError(Exception("Could not find verificationId"))
        }
        val creds =  FirebasePhoneAuthProvider.getCredential(verificationId!!, code)
        signIn(creds)
    }

    override fun setOnline(onlineNow: Boolean, onlineOnExit: Boolean): Boolean {

        if (!isAuthenticated)
            return  false

        userOnlineReference.setValue(onlineNow)

        if (!onlineOnExit)
            userOnlineReference.onDisconnect().setValue(false)
        else
            userOnlineReference.onDisconnect().cancel()
        return true
    }

    private val userOnlineReference : DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
                .child(USERS).child(FirebaseAuth.getInstance().currentUser!!.uid).child(ONLINE)
    }

    private var forceResendingToken: FirebasePhoneAuthProvider.ForceResendingToken? = null
    private var verificationId: String? = null
    private var mCallback = object : FirebasePhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(pac: PhoneAuthCredential) {
            signIn(pac)
        }

        override fun onVerificationFailed(ex: FirebaseException) {
            this@PhoneAuthProviderImp.callback?.onError(ex)
        }

        override fun onCodeSent(verificationId: String, forceResendingToken: FirebasePhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken)
            this@PhoneAuthProviderImp.verificationId = verificationId
            this@PhoneAuthProviderImp.forceResendingToken = forceResendingToken
            this@PhoneAuthProviderImp.callback?.onCodeSend()
        }
    }

    private fun signIn(creds : AuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(creds)
                .addOnSuccessListener {
                    this.callback?.onSuccess(SignedInUserImp(it.user!!.uid, it.additionalUserInfo?.isNewUser ?: false))
                }
                .addOnFailureListener { err -> this.callback?.onError(err) }
    }

    override fun doOnAuthenticated(action: Runnable) =
            FirebaseAuth.getInstance().addAuthStateListener {
                if (it.currentUser!=null){
                    action.run()
                }
            }

}

@Component
interface PhoneAuthProviderComponent {

    fun getPhoneAuthProvider() : PhoneAuthProviderImp

    @Component.Builder
    interface Builder {
        @BindsInstance fun setCallback(callback : PhoneAuthCallback?) : Builder

        fun build() : PhoneAuthProviderComponent
    }
}