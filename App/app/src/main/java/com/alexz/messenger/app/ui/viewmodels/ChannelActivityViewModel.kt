package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.providers.imp.FirestoreChannelsProvider
import com.alexz.messenger.app.data.providers.imp.FirestorePostsProvider
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import com.alexz.messenger.app.data.repo.LinkProvider

class ChannelActivityViewModel: ViewModel(), LinkProvider {

    private val channelRepository: ChannelsProvider by lazy { FirestoreChannelsProvider() }
    private val linkProvider: LinkProvider by lazy { FirestoreChannelsProvider() }
    private val postsRepository: PostsProvider by lazy { FirestorePostsProvider() }

    fun getChannel(id: String) = channelRepository.get(id)

    override fun createInviteLink(id: String) = linkProvider.createInviteLink(id)

    fun deletePost (post: Post) = postsRepository.delete(post)
    
    fun joinChannel(id: String) = channelRepository.join(id)

    fun createPost(p: Post) = postsRepository.create(p)

    fun getAdmins(id : String) = channelRepository.getAdmins(id)
}
