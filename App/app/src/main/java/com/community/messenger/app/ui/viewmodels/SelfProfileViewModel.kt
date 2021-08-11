package com.community.messenger.app.ui.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.community.messenger.app.data.ChatApplication.Companion.AppContext
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.core.providers.components.DaggerStorageProviderComponent
import com.community.messenger.core.providers.components.DaggerUserProfileProviderComponent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.StorageProvider
import com.community.messenger.core.providers.interfaces.UserProfileProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow

open class SelfProfileViewModel : DataViewModel<IUser>(){


    val uploadingStatus : LiveData<Double>
    get() = mutableUploadingLiveData


    @SuppressLint("CheckResult")
    fun updateProfilePic(uri : Uri){
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            try {
                storageProvider.uploadImage(uri).asFlow().collect {
                    if (it.second != null) {
                        try {
                            userProfileProvider.setImageUri(it.second.toString()).blockingAwait()
                        }catch (err : Throwable){
                            Log.e(TAG, "FAILED to update profile image\n$err")
                        }
                    }
                    mutableUploadingLiveData.postValue(it.first!!)
                }
            } catch (err: Throwable) {
                Log.e(TAG, "FAILED to update profile image\n$err")
            }
        }
    }

    private val mutableUploadingLiveData = MutableLiveData<Double>(0.0)
    private var updateJob : Job? = null

    private val TAG = SelfProfileViewModel::class.java.simpleName

    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }

    private val storageProvider : StorageProvider by lazy {
        DaggerStorageProviderComponent.builder()
            .setContext(AppContext)
            .build()
            .getProvider()
    }

    private val userProfileProvider : UserProfileProvider by lazy {
        DaggerUserProfileProviderComponent.create().getProvider()
    }

    init {
        collect(usersProvider.get(usersProvider.currentUserId).asFlow())

    }

}