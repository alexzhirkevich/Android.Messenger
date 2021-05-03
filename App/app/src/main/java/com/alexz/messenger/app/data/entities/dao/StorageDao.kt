package com.alexz.messenger.app.data.entities.dao

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

interface StorageDao {

    fun saveImage(img: Bitmap,name:String): Completable

    fun loadImage(uri: String): Maybe<Bitmap>

    fun saveVoice(bytes: ByteArray, name: String): Completable

    fun loadVoice(uri: String): Maybe<ByteArray>
}