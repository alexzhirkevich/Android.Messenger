package com.alexz.messenger.app.ui.viewmodels

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.model.result.MutableFuture
import com.alexz.messenger.app.data.repo.AuthRepository
import com.alexz.messenger.app.util.FirebaseUtil
import com.alexz.result.Error
import com.alexz.result.Future
import com.alexz.result.Success
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.messenger.app.R

class LoginActivityViewModel : ViewModel() {

    fun googleLogin(data:Intent): Future<FirebaseUser> {

        val res = MutableFuture<FirebaseUser>()

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                AuthRepository.googleLogin(account).addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        res.post(Success(FirebaseUtil.getCurrentFireUser()))
                    } else {
                        res.post(Error(R.string.error_google_login))
                    }
                }
            }

        } catch (ignored: ApiException) {
           res.post(Error(R.string.error_google_login))
        }

        return res
    }
}