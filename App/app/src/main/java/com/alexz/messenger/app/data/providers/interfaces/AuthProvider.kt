package com.alexz.messenger.app.data.providers.interfaces

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Single

interface AuthProvider {
    fun login(account: GoogleSignInAccount) : Single<FirebaseUser?>
}