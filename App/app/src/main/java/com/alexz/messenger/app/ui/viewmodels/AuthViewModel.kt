package com.alexz.messenger.app.ui.viewmodels

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.providers.imp.GoogleAuthProvider
import com.alexz.messenger.app.data.providers.interfaces.AuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.messenger.app.R
import io.reactivex.rxjava3.core.Single

class AuthViewModel : ViewModel(){

    val provider : AuthProvider = GoogleAuthProvider()

    fun getGoogleSignInClient(activity: Activity): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build()

        return GoogleSignIn.getClient(activity, options)
    }

    fun getGoogleSignInAccount(intent: Intent) : Single<GoogleSignInAccount> =
            Single.create {
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                task.addOnSuccessListener { acc ->
                    it.onSuccess(acc)
                }.addOnFailureListener { ex ->
                    it.onError(ex)
                }
            }

    fun login(account: GoogleSignInAccount) = provider.login(account)

    companion object {
        const val G_PLUS_SCOPE =
                "oauth2:https://www.googleapis.com/auth/plus.me";
        const val USERINFO_SCOPE =
                "https://www.googleapis.com/auth/userinfo.profile";
        const val EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email"
        const val SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;
        const val REQ_SIGN_IN = 123;
    }
}