package com.community.messenger.app.ui.adapters.recycler

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.community.features.avatarimageview.AvatarImageView
import com.community.messenger.app.R
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.util.dateTime
import com.community.messenger.core.providers.components.DaggerPostsProviderComponent
import com.community.messenger.core.providers.interfaces.PostsProvider
import com.community.recadapter.BaseRecyclerAdapter
import com.community.recadapter.BaseViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChannelRecyclerAdapter : BaseRecyclerAdapter<IChannel, ChannelRecyclerAdapter.ChannelViewHolder>(){

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_channel_constraint, parent, false)
        return ChannelViewHolder(root)
    }

    inner class ChannelViewHolder(itemView: View) : BaseViewHolder<IChannel>(itemView) {

        private var lastPostDisposable : Disposable? = null

        private val imgView: AvatarImageView = itemView.findViewById(R.id.channel_avatar)
        private val nameView: TextView = itemView.findViewById(R.id.channel_name)
        private val lastPostView: TextView = itemView.findViewById(R.id.channel_last_post_text)
        private val dateView: TextView = itemView.findViewById(R.id.channel_last_post_date)
        private val unreadView: TextView = itemView.findViewById(R.id.channel_unread_count)
        private val selector: CheckBox = itemView.findViewById(R.id.channel_selector)

        override fun onBind(entity: IChannel) {

            lastPostDisposable?.dispose()

            val isSelected = isSelected(entity.id)

            selector.isVisible = inSelectingMode
            selector.isChecked = isSelected
            itemView.setBackgroundColor(ContextCompat.getColor(imgView.context,
                    if (isSelected) R.color.selected else R.color.app_back
            ))

            nameView.text = entity.name

            if (entity.imageUri.isNotEmpty()) {
                imgView.setImageURI(Uri.parse(entity.imageUri))
            } else {
                imgView.setupWithText(entity.name,R.dimen.font_size_large,R.color.channels)
            }

            if (entity.lastPostId.isNotEmpty()) {
                lastPostDisposable = postsProvider.get(entity.lastPostId,entity.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    lastPostView.text = it.text
                                    dateView.text = it.time.dateTime(itemView.context).timeNearly
                                },
                                {
                                    Log.w(TAG, "Failed to observe last post")
                                }
                        )
            } else {
                lastPostView.text = itemView.context.getString(R.string.channel_created)
                dateView.text = entity.creationTime.dateTime(itemView.context).timeNearly
            }
        }
    }

    companion object {

        val postsProvider: PostsProvider by lazy {
            DaggerPostsProviderComponent.create().getProvider()
        }
        val TAG = ChannelRecyclerAdapter::class.java.simpleName
    }
}