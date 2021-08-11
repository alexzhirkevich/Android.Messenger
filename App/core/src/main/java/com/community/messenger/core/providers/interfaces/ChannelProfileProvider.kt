package com.community.messenger.core.providers.interfaces

import com.community.messenger.core.providers.base.Provider
import io.reactivex.Completable

interface ChannelProfileProvider : Provider {

    fun setName(name : String) : Completable

    fun setTag(tag : String) : Completable

    fun setDescription(text : String) : Completable

    fun setImageUri(uri : String) : Completable
}