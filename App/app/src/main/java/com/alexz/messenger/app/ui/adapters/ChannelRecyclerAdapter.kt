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
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import com.alexz.messenger.app.data.providers.test.TestPostProvider
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.FirebaseUtil
import com.alexz.messenger.app.util.timeVisualizer
import com.google.firebase.firestore.DocumentSnapshot
import com.messenger.app.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChannelRecyclerAdapter : FirestoreMapRecyclerAdapter<IChannel, ChannelRecyclerAdapter.ChannelViewHolder>(
        Channel::class.java as Class<IChannel>,
        FirebaseUtil.usersCollection.document(User().id).collection(FirebaseUtil.CHANNELS)) {

    override fun onCreateEntityReference(id: String) =
            FirebaseUtil.channelsCollection.document(id)

    override fun onEntityNotFound(id: String) {
        FirebaseUtil.usersCollection.document(User().id).collection(FirebaseUtil.CHANNELS)
                .document(id).delete()
    }

//    override fun onSelect(key: String, model: IChannel) = model.name.contains(key,true)

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat, parent, false)
        return ChannelViewHolder(root)
    }

    class ChannelViewHolder(itemView: View) : FirebaseViewHolder<IChannel>(itemView) {

        private var lastPostDisposable : Disposable? = null

        private val imgView: AvatarImageView = itemView.findViewById(R.id.channel_avatar)
        private val nameView: TextView = itemView.findViewById(R.id.channel_name)
        private val lastPostView: TextView = itemView.findViewById(R.id.channel_last_post_text)
        private val dateView: TextView = itemView.findViewById(R.id.channel_last_post_date)
        private val unreadView: TextView = itemView.findViewById(R.id.channel_unread_count)
        
        override fun bind(entity: IChannel) {
            super.bind(entity)
            lastPostDisposable?.dispose()

            nameView.text = entity.name

            if (entity.imageUri.isNotEmpty()) {
                imgView.setImageURI(Uri.parse(entity.imageUri))
            } else {
                imgView.setImageResource(R.drawable.logo)
            }

            if (entity.lastPostId.isNotEmpty()) {
                lastPostDisposable = postsProvider.last(entity.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    lastPostView.text = it.text
                                    dateView.text = it.time.timeVisualizer().time
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

        val postsProvider: PostsProvider by lazy {
            TestPostProvider()
        }
        val TAG = ChannelRecyclerAdapter::class.java.simpleName
    }

    override fun parse(snapshot: DocumentSnapshot): IChannel? {
        TODO("Not yet implemented")
    }
}