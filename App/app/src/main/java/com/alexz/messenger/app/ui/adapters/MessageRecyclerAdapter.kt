package com.alexz.messenger.app.ui.adapters

import android.view.ViewGroup
import com.alexz.messenger.app.data.model.imp.MediaMessage
import com.alexz.messenger.app.data.model.imp.Message
import com.alexz.messenger.app.data.model.interfaces.IMediaMessage
import com.alexz.messenger.app.ui.adapters.MessageRecyclerAdapter.MessageViewHolder
import com.alexz.messenger.app.ui.common.ItemClickListener
import com.alexz.messenger.app.ui.common.firerecyclerview.FirebaseListRecyclerAdapter
import com.alexz.messenger.app.ui.common.firerecyclerview.FirebaseViewHolder
import com.alexz.messenger.app.ui.views.MessageView
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class MessageRecyclerAdapter(private val chatID: String) :
        FirebaseListRecyclerAdapter<Message, MessageViewHolder>(Message::class.java) {

    private val imageClickListener: ItemClickListener<Message>? = null
    private val avatarClickListener: ItemClickListener<Message>? = null
    private val nameClickListener: ItemClickListener<Message>? = null

    override fun onCreateModelsQuery(): Query {
        return FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.CHATS)
                .child(chatID)
                .child(FirebaseUtil.MESSAGES)
    }

    override fun parse(snapshot: DataSnapshot): Message? {
        return if (snapshot.hasChild(IMediaMessage.MEDIA_CONTENT)) {
            snapshot.getValue(MediaMessage::class.java)
        } else {
            super.parse(snapshot)
        }
    }

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(MessageView(parent.context))
    }

    inner class MessageViewHolder(itemView: MessageView) : FirebaseViewHolder<Message>(itemView) {

        private val messageView = itemView

        override fun bind(model: Message) {
            super.bind(model)
            messageView.bind(model)
        }

        init {
            messageView.avatarView.setOnClickListener {
                avatarClickListener?.onItemClick(it, model)
            }
            messageView.avatarView.setOnLongClickListener {
                if (avatarClickListener != null) {
                    avatarClickListener.onLongItemClick(it, model)
                    return@setOnLongClickListener true
                }
                false
            }
            messageView.nameView.setOnClickListener {
                nameClickListener?.onItemClick(it, model)
            }
            messageView.nameView.setOnLongClickListener {
                if (nameClickListener != null) {
                    nameClickListener.onLongItemClick(it, model)
                    return@setOnLongClickListener true
                }
                false
            }
            messageView.contentGrid.setOnClickListener {
                imageClickListener?.onItemClick(it, model)
            }
            messageView.contentGrid.setOnLongClickListener {
                if (imageClickListener != null) {
                    imageClickListener.onLongItemClick(it, model)
                    return@setOnLongClickListener true
                }
                false
            }
        }
    }
}