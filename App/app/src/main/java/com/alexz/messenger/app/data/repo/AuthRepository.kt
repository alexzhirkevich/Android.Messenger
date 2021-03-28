package com.alexz.messenger.app.data.repo

import android.util.Log
import com.alexz.messenger.app.data.model.imp.User
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.messenger.app.BuildConfig

object AuthRepository {

    private val TAG = AuthRepository::class.java.canonicalName

    @JvmStatic
    fun googleLogin(account: GoogleSignInAccount): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                addUserToDatabase(task.result!!.user)
            }
        }
    }

    @JvmStatic
    private fun addUserToDatabase(user: FirebaseUser?) {
        val ref = FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.USERS)
                .child(user!!.uid)
                .child(FirebaseUtil.INFO)
                .child(FirebaseUtil.ID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    val u = User()
                    u.id = user.uid
                    u.isOnline = true
                    ref.parent!!.setValue(u)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Failed to load user")
                }
            }
        })
    }
}