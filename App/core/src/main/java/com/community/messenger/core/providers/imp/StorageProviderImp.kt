package com.community.messenger.core.providers.imp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.MimeTypeMap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.IMAGES
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.USER_DATA
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.VOICES
import com.community.messenger.core.providers.interfaces.StorageProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageProviderImp @Inject constructor(
        private val context:Context,
        private val usersProvider : UsersProvider
): StorageProvider {

    override fun uploadImage(path: Uri): Observable<Pair<Double, Uri?>> {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(path.toString())
        val storageReference = FirebaseStorage.getInstance().reference
                .child(USER_DATA)
                .child(usersProvider.currentUserId)
                .child(IMAGES)
                .child("${System.currentTimeMillis()}.$fileExtension")

        return upload(storageReference, path)
    }

    override fun loadImage(uri: String): Observable<Bitmap> = Observable.create{
        Glide.with(context).asBitmap().load(uri).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {

            }


            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                it.tryOnError(Exception("Download failed"))
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                it.onNext(resource)
                it.onComplete()
            }
        })
    }

    override fun uploadVoice(path: Uri): Observable<Pair<Double, Uri?>> {
        val storageReference = FirebaseStorage.getInstance().reference
                .child(USER_DATA)
                .child(usersProvider.currentUserId)
                .child(VOICES)
                .child("${System.currentTimeMillis()}.mp3")

        return upload(storageReference, path)
    }

    override fun loadVoice(url: String): Observable<ByteArray> = Observable.create<ByteArray> {
        try {
            val url = URL(url)
            val c: HttpURLConnection = url.openConnection() as HttpURLConnection
            c.requestMethod = "GET"
            c.connect()

            ByteArrayOutputStream().use { fos ->

                val buffer = ByteArray(1024)
                var len: Int

                while (c.inputStream.read(buffer).also {l-> len = l } != -1) {
                    fos.write(buffer, 0, len) //Write new file
                }

                it.onNext(fos.toByteArray())
                it.onComplete()
            }
        } catch (t: Throwable) {
            it.tryOnError(t)
        }
    }

    private fun upload(storageReference: StorageReference, path: Uri): Observable<Pair<Double, Uri?>> =
            Observable.create {
                storageReference.putFile(path)
                        .addOnSuccessListener { task ->
                            task.metadata?.reference?.downloadUrl
                                    ?.addOnSuccessListener { uri ->
                                        it.onNext(Pair(1.0, uri))
                                        it.onComplete()
                                    }?.addOnFailureListener { t -> it.onError(t) }
                        }
                        .addOnFailureListener { t -> it.onError(t) }
                        .addOnProgressListener { ts ->
                            val progress = 1.0 * ts.bytesTransferred /
                                    ts.totalByteCount
                            it.onNext(Pair(progress, null))
                        }
            }

}

