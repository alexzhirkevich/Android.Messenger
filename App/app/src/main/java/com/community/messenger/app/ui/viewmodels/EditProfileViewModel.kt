package com.community.messenger.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.common.util.FlowableDelayedTask
import com.community.messenger.core.providers.components.DaggerUserProfileProviderComponent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.UserProfileProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

class EditProfileViewModel(
        private val userProfileProvider: UserProfileProvider =
                DaggerUserProfileProviderComponent.create().getProvider())
    : SelfProfileViewModel(),UserProfileProvider by userProfileProvider {

    val userNameAvailable : LiveData<Boolean>
        get() = mutableUsernameAvailable

    fun checkUsername(minLen : Int = 0, usernameSupplier : () -> String){
        this.usernameSupplier = usernameSupplier
        this.minLen = minLen
        task.updateCountdown()
    }


    override fun onCleared() {
        super.onCleared()
        usersDisposable?.dispose()
    }


    private val mutableUsernameAvailable = MutableLiveData(true)

    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }
    private var usersDisposable : Disposable?= null

    private var usernameSupplier : () -> String = {""}
    private var minLen : Int = 0

    private val usernameObservable : Observable<IUser>
        get() = usersProvider.findByUsername(usernameSupplier())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .onErrorResumeNext(Function {

                if (it is NoSuchElementException) {
                    Thread.sleep(SEARCH_DELAY)
                    mutableUsernameAvailable.postValue(true)
                    usernameObservable
                }
                else {
                    mutableUsernameAvailable.postValue(false)
                    Observable.error(it)
                }

            })

    private val task = FlowableDelayedTask(SEARCH_DELAY){
        usersDisposable?.dispose()
        val name = usernameSupplier()
        if (name.length >=minLen) {
            val currentUsername = usernameSupplier()
            usersDisposable =  usernameObservable
                .subscribe(
                    {
                        mutableUsernameAvailable.postValue(currentUsername == data.value?.value?.username)
                    },
                    {
                        mutableUsernameAvailable.postValue(currentUsername == data.value?.value?.username)
                    })
        }
    }

    private companion object{
        private const val SEARCH_DELAY = 500L
    }
}