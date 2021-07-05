package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.providers.imp.DaggerProfileProviderComponent
import com.alexz.messenger.app.data.providers.imp.DaggerUsersProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.ProfileProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditProfileViewModel(
        private val profileProvider: ProfileProvider =
                DaggerProfileProviderComponent.create().getProfileProvider())
    : ViewModel(),ProfileProvider by profileProvider {

    val userNameAvailable : LiveData<Boolean>
        get() = mutableUsernameAvailable

    fun checkUsername(minLen : Int = 0,username : () -> String){
        updateTimer = true
        if (!timerStarted ){
            timerStarted = true
            Thread{
                while (updateTimer) {
                    updateTimer = false
                    Thread.sleep(500)
                }
                usersDisposable?.dispose()
                usersDisposable = usersProvider.isUsernameAvailable(username())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(
                                {
                                    mutableUsernameAvailable.postValue(it)
                                },
                                {
                                    mutableUsernameAvailable.postValue(it is NoSuchElementException)
                                }
                        )

                timerStarted = false
            }.start()
        }
    }

    override fun onCleared() {
        super.onCleared()
        usersDisposable?.dispose()
    }

    private val mutableUsernameAvailable = MutableLiveData<Boolean>(true)
    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getUsersProvider()
    }
    private var usersDisposable : Disposable?= null
    private var updateTimer = false
    private var timerStarted = false

}