package com.alexz.messenger.app.data.providers.imp

import android.app.Activity
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.interfaces.PhoneAuthCallback
import com.alexz.messenger.app.data.providers.interfaces.PhoneAuthProvider
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.PhoneAuthProvider as FirebasePhoneAuthProvider

class PhoneAuthProviderImpl(val callback : PhoneAuthCallback) : PhoneAuthProvider {

    var forceResendingToken: FirebasePhoneAuthProvider.ForceResendingToken? = null
    var verificationId: String? = null

    override val isAuthenticated : Boolean
        get() = FirebaseAuth.getInstance().currentUser != null

    override val isCodeSend: Boolean
        get() = forceResendingToken != null

    private var mCallback = object : FirebasePhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(pac: PhoneAuthCredential) {
            signIn(pac)
        }

        override fun onVerificationFailed(ex: FirebaseException) {
            this@PhoneAuthProviderImpl.callback.onError(ex)
        }

        override fun onCodeSent(verificationId: String, forceResendingToken: FirebasePhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken)
            this@PhoneAuthProviderImpl.verificationId = verificationId
            this@PhoneAuthProviderImpl.forceResendingToken = forceResendingToken
            this@PhoneAuthProviderImpl.callback.onCodeSend()
        }
    }

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
            callback.onError(Exception("Could not find verificationId"))
        }
        val creds =  FirebasePhoneAuthProvider.getCredential(verificationId!!, code)
        signIn(creds)
    }

    private fun signIn(creds : AuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(creds)
                .addOnSuccessListener {
                    this.callback.onSuccess(User())
                }
                .addOnFailureListener { err -> this.callback.onError(err) }
    }
}