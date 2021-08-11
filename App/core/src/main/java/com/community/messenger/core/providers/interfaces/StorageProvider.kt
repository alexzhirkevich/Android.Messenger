package com.community.messenger.core.providers.interfaces

import android.graphics.Bitmap
import android.net.Uri
import com.community.messenger.core.providers.base.Provider
import io.reactivex.Observable

interface StorageProvider : Provider{

    /**
     * @return observable pair of progress (from 0 to 1) and uploaded uri (only when progress is 1)
     * */
    fun uploadImage(path: Uri) : Observable<Pair<Double, Uri?>>

    fun loadImage(uri : String) : Observable<Bitmap>

    /**
     * @return observable pair of progress (from 0 to 1) and uploaded uri (only when progress is 1)
     * */
    fun uploadVoice(path: Uri) : Observable<Pair<Double, Uri?>>

    fun loadVoice(url: String): Observable<ByteArray>
}