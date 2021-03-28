package com.alexz.messenger.app.ui.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alexz.messenger.app.data.model.imp.Chat
import com.alexz.messenger.app.data.repo.DialogsRepository.chatIds
import com.alexz.messenger.app.data.repo.DialogsRepository.getChat
import com.alexz.messenger.app.ui.adapters.ChatRecyclerAdapter.ChatViewHolder
import com.alexz.messenger.app.ui.common.firerecyclerview.FirebaseMapRecyclerAdapter
import com.alexz.messenger.app.ui.common.firerecyclerview.FirebaseViewHolder
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.DateUtil
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.messenger.app.BuildConfig
import com.messenger.app.R
import java.util.*

class ChatRecyclerAdapter : FirebaseMapRecyclerAdapter<Chat, ChatViewHolder>(Chat::class.java) {
    override fun onCreateKeyQuery(): Query {
        return chatIds
    }

    override fun onCreateModelQuery(modelId: String): Query {
        return getChat(modelId)
    }

    override fun onModelNotFound(modelId: String) {
        FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.USERS)
                .child(FirebaseUtil.getCurrentUser().id)
                .child(FirebaseUtil.CHATS)
                .child(modelId)
                .removeValue()
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Invalid chat removed: $modelId")
        }
    }

    override fun onSelect(selectionKey: String?, model: Chat): Boolean {
        return if (selectionKey == null) {
            true
        } else model.name.toLowerCase().contains(selectionKey.toLowerCase())
    }

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dialog, parent, false)
        return ChatViewHolder(root)
    }

    class ChatViewHolder(itemView: View) : FirebaseViewHolder<Chat>(itemView) {
        private val image: AvatarImageView = itemView.findViewById(R.id.dialog_avatar)
        private val name: TextView = itemView.findViewById(R.id.dialog_name)
        private val lastSender: TextView = itemView.findViewById(R.id.dialog_last_message_sender)
        private val lastMessage: TextView = itemView.findViewById(R.id.dialog_last_message)
        private val date: TextView = itemView.findViewById(R.id.dialog_last_message_date)
        private val unread: TextView = itemView.findViewById(R.id.dialog_unread_count)
        init {
            unread.visibility = View.INVISIBLE
        }

        override fun bind(model: Chat) {
            super.bind(model)
            model.imageUri
            if (model.imageUri.isNotEmpty()) {
                image.setImageURI(Uri.parse(model.imageUri))
            } else {
                image.setImageResource(R.drawable.logo256)
            }
            name.text = model.name
            if (model.lastMessage != null) {
                lastMessage.text = model.lastMessage!!.text
                if (model.lastMessage?.senderId == FirebaseUtil.getCurrentUser().id) {
                    lastSender.setText(R.string.title_you)
                } else {
                    lastSender.text = model.lastMessage?.senderName
                }
                lastSender.append(":")
                date.text = DateUtil.getTime(Date(model.lastMessage!!.time))
            } else {
                lastMessage.text = ""
                lastSender.text = ""
                date.text = ""
            }
        }
    }

    companion object {
        val TAG = ChatRecyclerAdapter::class.java.simpleName
    }
}