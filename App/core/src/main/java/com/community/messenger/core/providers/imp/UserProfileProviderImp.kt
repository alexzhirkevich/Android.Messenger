package com.community.messenger.core.providers.imp

import android.net.Uri
import com.community.messenger.common.util.toCompletable
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_DESCRIPTION
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_IMAGE_URI
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_NAME
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_USERNAME
import com.community.messenger.core.providers.interfaces.UserProfileProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import io.reactivex.Completable
import javax.inject.Inject

class UserProfileProviderImp @Inject constructor(
        private var usersProvider: UsersProvider,
        private val firebaseProvider: FirebaseProvider
) : UserProfileProvider {

    override fun setName(name: String): Completable =
            firebaseProvider.usersCollection.document(usersProvider.currentUserId)
                    .set(mapOf(FIELD_NAME to name), SetOptions.merge())
                    .toCompletable().doOnComplete {
                FirebaseAuth.getInstance().currentUser?.updateProfile(UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build())
                    }

    override fun setUsername(username: String): Completable =
            firebaseProvider.usersCollection.document(usersProvider.currentUserId)
                    .set(mapOf(FIELD_USERNAME to username), SetOptions.merge()).toCompletable()

    override fun setDescription(text: String): Completable =
            firebaseProvider.usersCollection.document(usersProvider.currentUserId)
                    .set(mapOf(FIELD_DESCRIPTION to text), SetOptions.merge()).toCompletable()


    override fun setImageUri(uri: String): Completable =
            firebaseProvider.usersCollection.document(usersProvider.currentUserId)
                    .set(mapOf(FIELD_IMAGE_URI to uri), SetOptions.merge())
                    .toCompletable().doOnComplete {
                        FirebaseAuth.getInstance().currentUser
                                ?.updateProfile(UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(uri)).build())
                    }

//    override fun getContacts(): Observable<List<IUser>> =
//            FirebaseDatabase.getInstance().reference.child(USERS).child(usersProvider.currentUserId)
//                    .child(FirebaseUtil.CONTACTS).toObservable {
//                        it.value as Map<String, String>
//                    }.map { usersProvider.findByPhone(*it.keys.toTypedArray()) }.concatAll()
//
//
//    override fun setContacts(contacts: Map<String, String>): Completable =
//            FirebaseDatabase.getInstance().reference.child(USERS).child(usersProvider.currentUserId)
//                    .child(FirebaseUtil.CONTACTS).setValue(contacts).toCompletable()




}
