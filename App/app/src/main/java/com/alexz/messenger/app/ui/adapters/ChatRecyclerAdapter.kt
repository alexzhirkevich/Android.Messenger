package com.alexz.messenger.app.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alexz.firerecadapter.FirebaseMapRecyclerAdapter
import com.alexz.firerecadapter.FirebaseViewHolder
import com.alexz.messenger.app.data.model.imp.Chat
import com.alexz.messenger.app.data.repo.DialogsRepository
import com.alexz.messenger.app.ui.adapters.ChatRecyclerAdapter.ChatViewHolder
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.DateUtil
import com.alexz.messenger.app.util.FirebaseUtil
import com.messenger.app.R
import java.util.*

class ChatRecyclerAdapter : FirebaseMapRecyclerAdapter<Chat, ChatViewHolder>(Chat::class.java) {

    override fun onCreateKeyQuery() = DialogsRepository.chatIds

    override fun onCreateModelQuery(modelId: String) =  DialogsRepository.getChat(modelId)

    override fun onModelNotFound(modelId: String) = DialogsRepository.removeEmptyChatId(modelId)

    override fun onSelect(key: String, model: Chat) = model.name.contains(key,true)

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dialog, parent, false)
        return ChatViewHolder(root)
    }

    class ChatViewHolder(itemView: View) : FirebaseViewHolder<Chat>(itemView) {
        private val imgView: AvatarImageView = itemView.findViewById(R.id.dialog_avatar)
        private val nameView: TextView = itemView.findViewById(R.id.dialog_name)
        private val senderView: TextView = itemView.findViewById(R.id.dialog_last_message_sender)
        private val messageView: TextView = itemView.findViewById(R.id.dialog_last_message)
        private val dateView: TextView = itemView.findViewById(R.id.dialog_last_message_date)
//        private val unreadView: TextView = itemView.findViewById(R.id.dialog_unread_count)
//        init {
//            unreadView.visibility = View.INVISIBLE
//        }

        override fun bind(model: Chat) {
            super.bind(model)
            model.imageUri
            if (model.imageUri.isNotEmpty()) {
                imgView.setImageURI(Uri.parse(model.imageUri))
            } else {
                imgView.setImageResource(R.drawable.logo256)
            }
            nameView.text = model.name
            if (model.lastMessage != null) {
                messageView.text = model.lastMessage?.text
                if (model.lastMessage?.senderId == FirebaseUtil.getCurrentUser().id) {
                    senderView.setText(R.string.title_you)
                } else {
                    senderView.text = model.lastMessage?.senderName
                }
                senderView.append(":")
                dateView.text = DateUtil.getTime(Date(model.lastMessage!!.time))
            } else {
                messageView.text = ""
                senderView.text = ""
                dateView.text = ""
            }
        }
    }

    companion object {
        val TAG = ChatRecyclerAdapter::class.java.simpleName
    }
}