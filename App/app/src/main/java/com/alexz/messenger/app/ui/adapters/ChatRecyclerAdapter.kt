package com.alexz.messenger.app.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alexz.firerecadapter.firestore.FirestoreMapRecyclerAdapter
import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
import com.alexz.messenger.app.data.entities.imp.Chat
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import com.alexz.messenger.app.data.repo.ChatsRepository
import com.alexz.messenger.app.data.repo.MessagesRepository
import com.alexz.messenger.app.data.repo.UserListRepository
import com.alexz.messenger.app.ui.adapters.ChatRecyclerAdapter.ChatViewHolder
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.FirebaseUtil.CHATS
import com.alexz.messenger.app.util.FirebaseUtil.chatsCollection
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import com.alexz.messenger.app.util.getTime
import com.google.firebase.firestore.DocumentReference
import com.messenger.app.R
import io.reactivex.rxjava3.disposables.Disposable

class ChatRecyclerAdapter : FirestoreMapRecyclerAdapter<Chat, ChatViewHolder>(
        Chat::class.java,
        usersCollection.document(User().id).collection(CHATS)) {

    override fun onCreateEntityReference(id: String): DocumentReference =
            chatsCollection.document(id)

    override fun onEntityNotFound(id: String) {
        chatsProvider.removeChat(id)
    }

    override fun onSelect(key: String, model: Chat) = model.name.contains(key,true)

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dialog, parent, false)
        return ChatViewHolder(root)
    }

    override fun onViewRecycled(holder: ChatViewHolder) {
        super.onViewRecycled(holder)
    }

    class ChatViewHolder(itemView: View) : FirebaseViewHolder<Chat>(itemView) {

        var lastMsg : Disposable? = null
        var lastSender : Disposable? = null

        val imgView: AvatarImageView = itemView.findViewById(R.id.dialog_avatar)
        val nameView: TextView = itemView.findViewById(R.id.dialog_name)
        private val senderView: TextView = itemView.findViewById(R.id.dialog_last_message_sender)
        private val messageView: TextView = itemView.findViewById(R.id.dialog_last_message)
        private val dateView: TextView = itemView.findViewById(R.id.dialog_last_message_date)
        private val unreadView: TextView = itemView.findViewById(R.id.dialog_unread_count)
        init {
            unreadView.visibility = View.INVISIBLE
        }

        override fun bind(model: Chat) {
            super.bind(model)

            lastMsg?.dispose()
            lastSender?.dispose()

            if (model.imageUri.isNotEmpty()) {
                imgView.setImageURI(Uri.parse(model.imageUri))
            } else {
                imgView.setImageResource(R.drawable.logo256)
            }
            nameView.text = model.name
            if (model.lastMessageId.isNotEmpty()) {
                lastMsg = messagesProvider.getMessage(model.id,model.lastMessageId).subscribe { msg ->
                    messageView.text = msg.text
                    if (msg.senderId == User().id) {
                        senderView.setText(R.string.title_you)
                    } else {
                        lastSender = usersProvider.getUser(msg.senderId).subscribe {
                            senderView.text = msg.senderId

                        }
                        TODO("SENDER ID TO NAME")
                    }
                    senderView.append(":")
                    dateView.text = getTime(msg.time)
                }
            } else {
                messageView.text = ""
                senderView.text = ""
                dateView.text = ""
            }
        }
    }

    companion object {
        val TAG = ChatRecyclerAdapter::class.java.simpleName

        val usersProvider : UserListProvider by lazy { UserListRepository() }

        val chatsProvider: ChatsProvider by lazy { ChatsRepository() }

        val messagesProvider: MessagesProvider by lazy { MessagesRepository() }
    }
}