package com.community.messenger.app.ui.viewmodels

import androidx.lifecycle.*
import com.community.messenger.app.data.Data
import com.community.messenger.common.entities.imp.Channel
import com.community.messenger.common.entities.imp.ChannelAdmin
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.entities.interfaces.IPost
import com.community.messenger.core.providers.components.DaggerChannelsProviderComponent
import com.community.messenger.core.providers.components.DaggerPostsProviderComponent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.ChannelsProvider
import com.community.messenger.core.providers.interfaces.CurrentUserProvider
import com.community.messenger.core.providers.interfaces.PostsProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await

class ChannelDataViewModel(val id : String) : DataViewModel<IChannel>(),
    Parameterized<String>,
    CurrentUserProvider {

    val subscribers : LiveData<Data<Long>>
        get() = mutableSubscribers

    val admins : LiveData<Data<Collection<ChannelAdmin>>>
        get() = mutableAdmins

    val posts : LiveData<Data<Collection<IPost>>>
        get() = mutablePosts


    override var parameter: String = id
        set(value) {
            field = value
            update()
        }

    override val currentUserId: String
        get() = usersProvider.currentUserId


    suspend fun post(post : IPost) {
        post.channelId = parameter
        postsProvider.create(post).await()
    }

    private companion object{
        private const val MAX_SUBSCRIBERS = 1000
    }

    private val mutableSubscribers = MutableLiveData<Data<Long>>(Data(0))
    private val mutableAdmins = MutableLiveData<Data<Collection<ChannelAdmin>>>(Data(emptyList()))
    private val mutablePosts = MutableLiveData<Data<Collection<IPost>>>(Data(emptyList()))

    private var subscribersJob : Job?= null
    private var adminsJob : Job?= null
    private var postsJob : Job?= null

    private val channelsProvider : ChannelsProvider by lazy {
        DaggerChannelsProviderComponent.create().getProvider()
    }

    private val postsProvider : PostsProvider by lazy {
        DaggerPostsProviderComponent.create().getProvider()
    }

    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }

    init {
        update()
    }


    private fun update(){

        collect(channelsProvider.get(parameter).asFlow())

        subscribersJob?.cancel()
        subscribersJob = viewModelScope.launch {
            try {
                channelsProvider.getSubscribersCount(parameter)
                    .takeWhile {
                        it < MAX_SUBSCRIBERS
                    }.asFlow().collect {
                        mutableSubscribers.postValue(Data(it))
                    }
            }catch (t : Throwable){
                mutableSubscribers.postValue(Data(error = t))
            }
        }
        adminsJob?.cancel()
        adminsJob = viewModelScope.launch {
            try {
                channelsProvider.getAdmins(parameter).asFlow().collect {
                    mutableAdmins.postValue(Data(it))
                }
            }catch (t : Throwable){
                mutableAdmins.postValue(Data(error = t))
            }
        }

        postsJob?.cancel()
        postsJob = viewModelScope.launch {
            try {
                postsProvider.getAll(Channel(id = parameter))
                    .asFlow().collect {
                        mutablePosts.postValue(Data(it))
                    }
            }catch (t : Throwable){
                mutablePosts.postValue(Data(error = t))
            }
        }
    }
}

class ChannelDataViewModelFactory(val channelId: String) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ChannelDataViewModel(channelId) as T
}