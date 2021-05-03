package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.imp.fromFire
import com.alexz.messenger.app.data.providers.interfaces.AuthProvider
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class GoogleAuthProvider(
        private val userListProvider: UserListProvider = FirestoreUserListProvider()) : AuthProvider {

    override fun login(account: GoogleSignInAccount): Single<FirebaseUser?> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return Single.create {
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    it.onSuccess(task.result?.user)
                    task.result?.user?.let { u ->
                        addUserToDatabase(User.fromFire(u))
                    }
                } else {
                    it.onError(task.exception)
                }
            }
        }
    }

    private fun addUserToDatabase(user: User): Completable {
        val doc = FirebaseUtil.usersCollection.document(user.id)

        return Completable.create {
            try {
                doc.get().addOnSuccessListener { snap ->
                    if (!snap.exists() || snap.toObject(User::class.java) != user) {
                        doc.set(user).addOnSuccessListener { _ ->
                            it.onComplete()
                        }.addOnFailureListener { ex ->
                            it.onError(ex)
                        }
                    }
                }
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }
}