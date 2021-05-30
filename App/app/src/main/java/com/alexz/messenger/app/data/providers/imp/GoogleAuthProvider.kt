package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.interfaces.AuthProvider
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.Single

class GoogleAuthProvider(
        private val userListProvider: UserListProvider = FirestoreUserListProvider()) : AuthProvider {

    override fun login(account: GoogleSignInAccount): Single<FirebaseUser?> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return Single.create {
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    task.result?.user?.let { u ->
                        it.onSuccess(u)
                        userListProvider.create(User())
                    }
                } else {
                    it.onError(task.exception ?: Exception("Login failed"))
                }
            }
        }
    }
}