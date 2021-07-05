package com.alexz.messenger.app.ui.viewmodels

import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.providers.imp.DaggerChannelsProviderComponent
import com.alexz.messenger.app.data.providers.imp.DaggerPostsProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import com.alexz.messenger.app.data.providers.test.TestChannelsProvider
import com.alexz.messenger.app.data.repo.LinkProvider
import io.reactivex.schedulers.Schedulers

class ChannelsViewModel: DataViewModel<List<IChannel>>(), LinkProvider,Updatable {


    private val channelRepository: ChannelsProvider by lazy { TestChannelsProvider() }
    private val linkProvider: LinkProvider by lazy {
        DaggerChannelsProviderComponent.create().getChannelsProvider()
    }
    private val postsRepository: PostsProvider by lazy {
        DaggerPostsProviderComponent.create().getPostsProvider()
    }

    override fun update() {
        observe(channelRepository.getAll(User(id = "test"))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map { it.sorted() }
        )
    }

    fun getChannel(id: String) = channelRepository.get(id)

    override fun createInviteLink(id: String) = linkProvider.createInviteLink(id)

    fun deletePost (post: Post) = postsRepository.delete(post)
    
    fun joinChannel(id: String) = channelRepository.join(id)

    fun createPost(p: Post) = postsRepository.create(p)

    fun getAdmins(id : String) = channelRepository.getAdmins(id)
}
