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
import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.data.entities.interfaces.IMessageable
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import com.alexz.messenger.app.data.providers.test.TestChatsProvider
import com.alexz.messenger.app.data.providers.test.TestMessagesProvider
import com.alexz.messenger.app.data.providers.test.TestUsersProvider
import com.alexz.messenger.app.ui.adapters.ChatRecyclerAdapter.ChatViewHolder
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.FirebaseUtil.CHATS
import com.alexz.messenger.app.util.FirebaseUtil.chatsCollection
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import com.alexz.messenger.app.util.timeVisualizer
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.messenger.app.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChatRecyclerAdapter : FirestoreMapRecyclerAdapter<IMessageable, ChatViewHolder>(
        Chat::class.java as Class<IMessageable>,
        usersCollection.document("User().id").collection(CHATS)) {

    override fun onCreateEntityReference(id: String): DocumentReference =
            chatsCollection.document(id)

    override fun onEntityNotFound(id: String) {
        chatsProvider.remove(id, User())
    }


//    override fun onSelect(key: String, model: IMessageable) = true //model.name.contains(key,true)

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_channel, parent, false)
        return ChatViewHolder(root)
    }

    class ChatViewHolder(itemView: View) : FirebaseViewHolder<IMessageable>(itemView) {

        var lastMessageDisposable: Disposable? = null
        var lastSenderDisposable: Disposable? = null

        private val imgView: AvatarImageView = itemView.findViewById(R.id.dialog_avatar)
        private val nameView: TextView = itemView.findViewById(R.id.dialog_name)
        private val senderView: TextView = itemView.findViewById(R.id.dialog_last_message_sender)
        private val messageView: TextView = itemView.findViewById(R.id.dialog_last_message)
        private val dateView: TextView = itemView.findViewById(R.id.dialog_last_message_date)
        private val unreadView: TextView = itemView.findViewById(R.id.dialog_unread_count)

        override fun bind(entity: IMessageable) {
            super.bind(entity)

            lastMessageDisposable?.dispose()
            lastSenderDisposable?.dispose()

            when (entity) {
                is IChat -> {
                    if (entity.imageUri.isNotEmpty()) {
                        imgView.setImageURI(Uri.parse(entity.imageUri))
                    } else {
                        imgView.setImageResource(R.drawable.logo)
                    }

                    nameView.text = entity.name
                    if (entity.lastMessageId.isNotEmpty()) {
                        lastMessageDisposable?.dispose()
                        lastMessageDisposable = messagesProvider.get(entity.id, entity.lastMessageId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { msg ->
                                            messageView.text = msg.text
                                            if (msg.senderId == User().id) {
                                                senderView.setText(R.string.you)
                                            } else {
                                                lastSenderDisposable?.dispose()
                                                lastSenderDisposable = usersProvider.get(id = msg.senderId)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(
                                                                { u -> senderView.text = u.name + ":" },
                                                                { TODO("get user failed") })
                                            }
                                            senderView.append(":")
                                            dateView.text = msg.time.timeVisualizer().time
                                        },
                                        {
                                            TODO("get message error")
                                        }
                                )
                    } else {
                        messageView.text = ""
                        senderView.text = ""
                        dateView.text = ""
                    }
                }
            }
        }
    }

    companion object {
        val TAG = ChatRecyclerAdapter::class.java.simpleName

        val usersProvider : UsersProvider by lazy { TestUsersProvider() }

        val chatsProvider: ChatsProvider by lazy { TestChatsProvider() }

        val messagesProvider: MessagesProvider by lazy { TestMessagesProvider() }
    }

    override fun parse(snapshot: DocumentSnapshot): IMessageable? {
        TODO("Not yet implemented")
    }
}