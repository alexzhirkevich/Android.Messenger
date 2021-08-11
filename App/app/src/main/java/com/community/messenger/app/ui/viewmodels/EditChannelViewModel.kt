package com.community.messenger.app.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.community.messenger.app.data.ChatApplication.Companion.AppContext
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.util.FlowableDelayedTask
import com.community.messenger.core.providers.components.DaggerChannelProfileProviderComponent
import com.community.messenger.core.providers.components.DaggerChannelsProviderComponent
import com.community.messenger.core.providers.components.DaggerStorageProviderComponent
import com.community.messenger.core.providers.interfaces.ChannelProfileProvider
import com.community.messenger.core.providers.interfaces.ChannelsProvider
import com.community.messenger.core.providers.interfaces.StorageProvider
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.rx2.awaitLast

class EditChannelViewModel : ViewModel(),Parameterized<String>  {

    val tagAvailable : LiveData<Boolean>
        get() = mutableUsernameAvailable


    override var parameter: String = ""
        set(value) {
            field = value
            if (parameter.isNotEmpty())
                channelProfileProvider = DaggerChannelProfileProviderComponent.builder()
                    .setId(parameter).build().getProvider()
        }


    suspend fun uploadImage(uri : Uri) : String{
        return storageProvider.uploadImage(uri).awaitLast().second.toString()
    }

    suspend fun setName(name: String) =
        channelProfileProvider.setName(name).await()


    suspend fun setTag(tag: String) =
        channelProfileProvider.setTag(tag).await()


    suspend fun setDescription(text: String) =
        channelProfileProvider.setDescription(text).await()


    suspend fun setImageUri(uri: String)  =
        channelProfileProvider.setImageUri(uri).await()

    fun checkTag(minLen : Int = 0, tagSupplier : () -> String){
        this.tagSupplier = tagSupplier
        this.minLen = minLen
        task.updateCountdown()
    }

    suspend fun createChannel(channel  : IChannel) = channelsProvider.create(channel)
        .await()

    override fun onCleared() {
        super.onCleared()
        channelTagDisposable?.dispose()
    }

    private val mutableUsernameAvailable = MutableLiveData(true)

    private lateinit var channelProfileProvider : ChannelProfileProvider

    private val storageProvider : StorageProvider by lazy {
        DaggerStorageProviderComponent.builder().setContext(AppContext).build().getProvider()
    }

    private val channelsProvider : ChannelsProvider by lazy {
        DaggerChannelsProviderComponent.create().getProvider()
    }

    private var channelTagDisposable : Disposable?= null

    private var tagSupplier : () -> String = {""}
    private var minLen : Int = 0

    private val tagObservable : Observable<IChannel>
        get() = channelsProvider.findByTag(tagSupplier())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .onErrorResumeNext(Function {
                if (it is NoSuchElementException){
                    Thread.sleep(SEARCH_DELAY)
                    mutableUsernameAvailable.postValue(false)
                    tagObservable
                } else Observable.error(it)
            })

    private val task = FlowableDelayedTask(SEARCH_DELAY){
        channelTagDisposable?.dispose()
        val name = tagSupplier()
        if (name.length >=minLen) {
            val currentTag = tagSupplier()
            channelTagDisposable = tagObservable
                    .subscribe(
                            {
                                mutableUsernameAvailable.postValue(false)
                            },
                            {
                                mutableUsernameAvailable.postValue(false)
                            }
                    )
        }
    }
    private companion object{
        const val SEARCH_DELAY = 500L
    }
}