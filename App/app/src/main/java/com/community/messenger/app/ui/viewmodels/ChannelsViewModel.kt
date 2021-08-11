package com.community.messenger.app.ui.viewmodels

import com.community.messenger.common.entities.imp.Post
import com.community.messenger.common.entities.imp.User
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.util.SnapshotNotFoundException
import com.community.messenger.core.providers.base.LinkProvider
import com.community.messenger.core.providers.components.DaggerChannelsProviderComponent
import com.community.messenger.core.providers.components.DaggerPostsProviderComponent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.ChannelsProvider
import com.community.messenger.core.providers.interfaces.PostsProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import io.reactivex.Observable
import io.reactivex.functions.Function
import kotlinx.coroutines.rx2.asFlow

class ChannelsViewModel: DataViewModel<Collection<IChannel>>(), LinkProvider {

    private val channelsProvider: ChannelsProvider by lazy {
        DaggerChannelsProviderComponent.create().getProvider()
    }
    private val postsRepository: PostsProvider by lazy {
        DaggerPostsProviderComponent.create().getProvider()
    }

    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }

    private val observable : Observable<Collection<IChannel>>
        get() = channelsProvider.getAll(User(id = usersProvider.currentUserId))
            .onErrorResumeNext(Function { t ->
                if (t is SnapshotNotFoundException) {
                    channelsProvider.remove(t.id, User(id = usersProvider.currentUserId))
                    observable
                } else Observable.error(t)
            })

    init {
        collect(observable.asFlow())
    }



    fun getChannel(id: String) = channelsProvider.get(id)

    override fun createInviteLink(id: String) = channelsProvider.createInviteLink(id)

    fun deletePost (post: Post) = postsRepository.delete(post)
    
    fun joinChannel(id: String) = channelsProvider.join(id)

    fun createPost(p: Post) = postsRepository.create(p)

    fun getAdmins(id : String) = channelsProvider.getAdmins(id)
}
