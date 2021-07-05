package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.base.SingleEntityProvider
import io.reactivex.Observable

interface UsersProvider
    : SingleEntityProvider<IUser> {

    fun getNotificationToken(userId : String) : Observable<String>

    fun findByPhone(vararg phones : String) : Observable<List<IUser>>

    fun findByUsername(username : String) : Observable<IUser>

    fun isUsernameAvailable(username: String): Observable<Boolean>

    val currentUserId : String
}