//package com.alexz.messenger.app.data.providers.imp
//
//import android.app.Activity
//import com.alexz.messenger.app.data.entities.imp.User
//import com.alexz.messenger.app.data.providers.interfaces.AuthProvider
//import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.tasks.Task
//import com.google.firebase.FirebaseException
//import com.google.firebase.auth.*
//import io.reactivex.Single
//import java.util.concurrent.TimeUnit
//
//class FirebaseAuthProvider(private val userListProvider: UsersProvider = FirestoreUsersProvider())
//    : AuthProvider {
//
//    private var verifId : String?= null
//    private var resendToken : PhoneAuthProvider.ForceResendingToken?= null
//
//    override fun login(account: GoogleSignInAccount): Single<FirebaseUser?> {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        return Single.create {
//            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult> ->
//                if (task.isSuccessful) {
//                    task.result?.user?.let { u ->
//                        it.onSuccess(u)
//                        userListProvider.create(User())
//                    }
//                } else {
//                    it.onError(task.exception ?: Exception("Login failed"))
//                }
//            }
//        }
//    }
//
//    override fun login(callbackActivity : Activity, phone: String,
//                       onCodeSend: (verificationId: String, token: PhoneAuthProvider.ForceResendingToken)-> Unit): Single<FirebaseUser>
//            = Single.create{
//
//        val auth = FirebaseAuth.getInstance()
//        val options = PhoneAuthOptions.newBuilder(auth)
//                .setPhoneNumber(phone)
//                .setTimeout(60L, TimeUnit.SECONDS)
//                .setActivity(callbackActivity)
//                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
//                    override fun onVerificationCompleted(pac: PhoneAuthCredential) {
//                        auth.signInWithCredential(pac).addOnSuccessListener { res->
//                            res.user?.let { it1 -> it.onSuccess(it1) }
//                        }.addOnFailureListener { t ->
//                            it.onError(t)
//                        }
//                    }
//
//                    override fun onVerificationFailed(error: FirebaseException) {
//                        it.onError(error)
//                    }
//
//                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
//                        super.onCodeSent(verificationId, token)
//                        verifId = verificationId
//                        resendToken = token
//                        onCodeSend(verificationId,token)
//                    }
//
//                })
//                .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }
//}