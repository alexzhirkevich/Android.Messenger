package com.alexz.messenger.app.data.repo

import android.graphics.Bitmap
import com.alexz.messenger.app.data.entities.dao.AppStorageDao
import com.alexz.messenger.app.data.entities.dao.StorageDao
import com.alexz.messenger.app.data.providers.imp.FirebaseStorageProvider
import com.alexz.messenger.app.data.providers.interfaces.StorageProvider
import io.reactivex.rxjava3.core.Observable

class StorageRepository(
        private val provider: StorageProvider = FirebaseStorageProvider(),
        private val dao : StorageDao = AppStorageDao()) : StorageProvider by provider {

    override fun loadImage(uri: String): Observable<Bitmap> =
            Observable.concatArray(dao.loadImage(uri).toObservable(),provider.loadImage(uri))


    override fun loadVoice(url: String): Observable<ByteArray> =
            Observable.concatArray(dao.loadVoice(url).toObservable(),provider.loadVoice(url))
}