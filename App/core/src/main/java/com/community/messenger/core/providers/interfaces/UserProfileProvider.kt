package com.community.messenger.core.providers.interfaces

import com.community.messenger.core.providers.base.Provider
import io.reactivex.Completable

interface UserProfileProvider : Provider{

    fun setName(name : String) : Completable

    fun setUsername(username : String) : Completable

    fun setDescription(text : String) : Completable

    fun setImageUri(uri : String) : Completable

//    fun getContacts() : Observable<List<IUser>>
//
//    fun setContacts(contacts : Map<String, String>) : Completable

}