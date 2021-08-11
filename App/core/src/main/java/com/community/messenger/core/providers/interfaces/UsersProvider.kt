package com.community.messenger.core.providers.interfaces

import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.core.providers.base.SingleEntityProvider
import io.reactivex.Observable

interface UsersProvider
    : SingleEntityProvider<IUser>, CurrentUserProvider {

    fun getNotificationToken(userId : String) : Observable<String>

    fun findByPhone(vararg phones : String) : Observable<Collection<IUser>>

    fun findByUsername(username : String) : Observable<IUser>

    fun findByUsernameNearly(username: String, limit:Int = 30) : Observable<Collection<IUser>>

}


