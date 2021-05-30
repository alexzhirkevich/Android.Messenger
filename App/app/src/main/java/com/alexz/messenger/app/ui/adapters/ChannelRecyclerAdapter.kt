package com.alexz.messenger.app.ui.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alexz.firerecadapter.firestore.FirestoreMapRecyclerAdapter
import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
import com.alexz.messenger.app.data.entities.imp.Channel
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.imp.FirestorePostsProvider
import com.alexz.messenger.app.data.providers.imp.FirestoreUserListProvider
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.FirebaseUtil
import com.alexz.messenger.app.util.getTime
import com.messenger.app.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChannelRecyclerAdapter : FirestoreMapRecyclerAdapter<Channel, ChannelRecyclerAdapter.ChannelViewHolder>(
        Channel::class.java,
        FirebaseUtil.usersCollection.document(User().id).collection(FirebaseUtil.CHANNELS)) {

    override fun onCreateEntityReference(id: String) =
            FirebaseUtil.channelsCollection.document(id)

    override fun onEntityNotFound(id: String) {
        FirebaseUtil.usersCollection.document(User().id).collection(FirebaseUtil.CHANNELS)
                .document(id).delete()
    }

    override fun onSelect(key: String, model: Channel) = model.name.contains(key,true)

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(root)
    }

    class ChannelViewHolder(itemView: View) : FirebaseViewHolder<Channel>(itemView) {

        var dispose : Disposable? = null

        val imgView: AvatarImageView = itemView.findViewById(R.id.channel_avatar)
        val nameView: TextView = itemView.findViewById(R.id.channel_name)
        val lastPostView: TextView = itemView.findViewById(R.id.chat_last_post)
        val dateView: TextView = itemView.findViewById(R.id.channel_last_post_date)
        val unreadView: TextView = itemView.findViewById(R.id.channel_unread_count)
        init {
            unreadView.visibility = View.INVISIBLE
        }

        override fun bind(entity: Channel) {
            super.bind(entity)
            dispose?.dispose()

            nameView.text = entity.name

            if (entity.imageUri.isNotEmpty()) {
                imgView.setImageURI(Uri.parse(entity.imageUri))
            } else {
                imgView.setImageResource(R.drawable.logo256)
            }

            if (entity.lastPostId.isNotEmpty()) {
                dispose = postsProvider.last(entity.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    lastPostView.text = it.text
                                    dateView.text = getTime(it.time )
                                },
                                {
                                    Log.w(TAG, "Failed to observe last post")
                                }
                        )
            } else {
                lastPostView.text = ""
                dateView.text = ""
            }
        }
    }

    companion object {

        val userListProvider by lazy {
           FirestoreUserListProvider()
        }
        val postsProvider: PostsProvider by lazy {
            FirestorePostsProvider()
        }
        val TAG = ChannelRecyclerAdapter::class.java.simpleName
    }
}