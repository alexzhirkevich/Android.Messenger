package com.alexz.messenger.app.ui.viewmodels

import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.providers.imp.FirestoreChannelsProvider
import com.alexz.messenger.app.data.providers.imp.FirestorePostsProvider
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import com.alexz.messenger.app.data.providers.test.TestChannelsProvider
import com.alexz.messenger.app.data.repo.LinkProvider
import io.reactivex.schedulers.Schedulers

class ChannelViewModel: DataViewModel<List<IChannel>>(), LinkProvider {


    private val channelRepository: ChannelsProvider by lazy { TestChannelsProvider() }
    private val linkProvider: LinkProvider by lazy { FirestoreChannelsProvider() }
    private val postsRepository: PostsProvider by lazy { FirestorePostsProvider() }

    init {
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
