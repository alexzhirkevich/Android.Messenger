package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import com.alexz.messenger.app.data.repo.ChannelsRepository
import com.alexz.messenger.app.data.repo.PostsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class ChannelActivityViewModel(
        private val channelRepository: ChannelsProvider = ChannelsRepository(),
        private val postsRepository: PostsProvider = PostsRepository()
): ViewModel(), ChannelsProvider by channelRepository,PostsProvider by postsRepository{

    override fun addPost(post: Post): Completable = postsRepository.addPost(post)

    override fun lastPost(channelId: String): Observable<Post> = postsRepository.lastPost(channelId)
}
