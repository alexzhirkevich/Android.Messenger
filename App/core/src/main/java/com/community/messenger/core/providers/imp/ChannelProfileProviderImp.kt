package com.community.messenger.core.providers.imp

import android.net.Uri
import com.community.messenger.common.util.toCompletable
import com.community.messenger.core.providers.interfaces.ChannelProfileProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_DESCRIPTION
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_IMAGE_URI
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_NAME
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import io.reactivex.Completable
import javax.inject.Inject

class ChannelProfileProviderImp @Inject constructor(
        private val id : String,
        private val firebaseProvider: FirebaseProvider
) : ChannelProfileProvider {

    private val doc = firebaseProvider.channelsCollection.document(id)

    override fun setName(name: String): Completable =
            doc.set(mapOf(FIELD_NAME to name), SetOptions.merge()).toCompletable().doOnComplete {
                FirebaseAuth.getInstance().currentUser?.updateProfile(UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build())
                    }

    override fun setTag(tag: String): Completable =
            doc.set(mapOf(FIELD_TAG to tag), SetOptions.merge()).toCompletable()

    override fun setDescription(text: String): Completable =
            doc.set(mapOf(FIELD_DESCRIPTION to text), SetOptions.merge()).toCompletable()


    override fun setImageUri(uri: String): Completable =
            doc.set(mapOf(FIELD_IMAGE_URI to uri), SetOptions.merge())
                    .toCompletable().doOnComplete {
                        FirebaseAuth.getInstance().currentUser
                                ?.updateProfile(UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(uri)).build())
                    }

}
