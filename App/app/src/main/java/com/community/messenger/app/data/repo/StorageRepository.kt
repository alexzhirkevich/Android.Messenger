//package com.community.messenger.app.data.repo
//
//import android.graphics.Bitmap
//import com.community.messenger.common.entities.dao.AppStorageDao
//import com.community.messenger.common.entities.dao.StorageDao
//import com.community.messenger.core.providers.imp.FirebaseStorageProvider
//import com.community.messenger.core.providers.interfaces.StorageProvider
//import io.reactivex.Observable
//
//class StorageRepository(
//        private val provider: StorageProvider = FirebaseStorageProvider(),
//        private val dao : StorageDao = AppStorageDao()) : StorageProvider by provider {
//
//    override fun loadImage(uri: String): Observable<Bitmap> =
//            Observable.mergeArray(dao.loadImage(uri).toObservable(),provider.loadImage(uri))
//
//
//    override fun loadVoice(url: String): Observable<ByteArray> =
//            Observable.mergeArray(dao.loadVoice(url).toObservable(),provider.loadVoice(url))
//}