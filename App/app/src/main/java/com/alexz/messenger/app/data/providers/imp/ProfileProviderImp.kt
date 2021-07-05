package com.alexz.messenger.app.data.providers.imp

import android.net.Uri
import com.alexz.messenger.app.data.providers.interfaces.ProfileProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import com.alexz.messenger.app.util.FirebaseUtil
import com.alexz.messenger.app.util.FirebaseUtil.DESCRIPTION
import com.alexz.messenger.app.util.FirebaseUtil.IMAGE_URI
import com.alexz.messenger.app.util.FirebaseUtil.NAME
import com.alexz.messenger.app.util.FirebaseUtil.USERNAME
import com.alexz.messenger.app.util.FirebaseUtil.USERS
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import com.alexz.messenger.app.util.toCompletable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.SetOptions
import dagger.Component
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

class ProfileProviderImp @Inject constructor(
        private var usersProvider: UsersProvider
) : ProfileProvider {

    override fun setName(name: String): Completable =
            usersCollection.document(usersProvider.currentUserId)
                    .set(mapOf(NAME to name), SetOptions.merge())
                    .toCompletable().doOnComplete {
                FirebaseAuth.getInstance().currentUser?.updateProfile(UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build())
                    }

    override fun setUsername(username: String): Completable =
            usersCollection.document(usersProvider.currentUserId)
                    .set(mapOf(USERNAME to username), SetOptions.merge()).toCompletable()

    override fun setDescription(text: String): Completable =
            usersCollection.document(usersProvider.currentUserId)
                    .set(mapOf(DESCRIPTION to text), SetOptions.merge()).toCompletable()


    override fun setImageUri(uri: String): Completable =
            usersCollection.document(usersProvider.currentUserId)
                    .set(mapOf(IMAGE_URI to uri), SetOptions.merge())
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

    override fun setNotificationToken(token: String): Completable =
            FirebaseDatabase.getInstance().reference.child(USERS).child(usersProvider.currentUserId)
                    .child(FirebaseUtil.NOTIFY_TOKEN).setValue(token).toCompletable()

}

@Singleton
@Component(modules = [UsersProviderModule::class])
interface ProfileProviderComponent {

    fun getProfileProvider() : ProfileProviderImp
}