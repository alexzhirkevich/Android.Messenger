package com.alexz.messenger.app.ui.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.imp.DaggerProfileProviderComponent
import com.alexz.messenger.app.data.providers.imp.DaggerStorageProviderComponent
import com.alexz.messenger.app.data.providers.imp.DaggerUsersProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.ProfileProvider
import com.alexz.messenger.app.data.providers.interfaces.StorageProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import io.reactivex.schedulers.Schedulers

class SelfProfileViewModel : DataViewModel<IUser>(),Updatable{


    val uploadingStatus : LiveData<Double>
    get() = mutableUploadingLiveData

    @SuppressLint("CheckResult")
    fun updateProfilePic(uri : Uri){
        storageProvider.uploadImage(uri)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            if (it.second != null) {

                                profileProvider.setImageUri(it.second.toString()).subscribe(
                                        { Log.d(TAG, "Profile image seccessfully updated") },
                                        { err -> Log.wtf(TAG, "FAILED to update profile image", err) }
                                )
                            }
                            mutableUploadingLiveData.postValue(it.first)
                        },
                        {
                            err -> Log.e(TAG, "FAILED to update profile image\n$err")
                        }
                )
    }

    override fun update() {
        observe(usersProvider.get(usersProvider.currentUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()))

    }

    private val mutableUploadingLiveData = MutableLiveData<Double>(0.0)


    private val TAG = SelfProfileViewModel::class.java.simpleName

    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getUsersProvider()
    }

    private val storageProvider : StorageProvider by lazy {
        DaggerStorageProviderComponent.create().getStorageProvider()
    }

    private val profileProvider : ProfileProvider by lazy {
        DaggerProfileProviderComponent.create().getProfileProvider()
    }

}