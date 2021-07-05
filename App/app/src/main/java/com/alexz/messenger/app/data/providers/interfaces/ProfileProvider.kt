package com.alexz.messenger.app.data.providers.interfaces

import io.reactivex.Completable

interface ProfileProvider {

    fun setName(name : String) : Completable

    fun setUsername(username : String) : Completable

    fun setDescription(text : String) : Completable

    fun setImageUri(uri : String) : Completable

//    fun getContacts() : Observable<List<IUser>>
//
//    fun setContacts(contacts : Map<String, String>) : Completable

    fun setNotificationToken(token : String) : Completable
}