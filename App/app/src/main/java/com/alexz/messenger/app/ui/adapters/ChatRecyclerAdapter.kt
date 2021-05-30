//package com.alexz.messenger.app.ui.adapters
//
//import android.net.Uri
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import com.alexz.firerecadapter.firestore.FirestoreMapRecyclerAdapter
//import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
//import com.alexz.messenger.app.data.entities.imp.Chat
//import com.alexz.messenger.app.data.entities.imp.User
//import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
//import com.alexz.messenger.app.data.providers.imp.FirestoreMessagesProvider
//import com.alexz.messenger.app.data.providers.imp.FirestoreUserListProvider
//import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
//import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
//import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
//import com.alexz.messenger.app.ui.adapters.ChatRecyclerAdapter.ChatViewHolder
//import com.alexz.messenger.app.ui.views.AvatarImageView
//import com.alexz.messenger.app.util.FirebaseUtil.CHATS
//import com.alexz.messenger.app.util.FirebaseUtil.chatsCollection
//import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
//import com.alexz.messenger.app.util.getTime
//import com.google.firebase.firestore.DocumentReference
//import com.messenger.app.R
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//
//class ChatRecyclerAdapter : FirestoreMapRecyclerAdapter<Chat, ChatViewHolder>(
//        Chat::class.java,
//        usersCollection.document(User().id).collection(CHATS)) {
//
//    override fun onCreateEntityReference(id: String): DocumentReference =
//            chatsCollection.document(id)
//
//    override fun onEntityNotFound(id: String) {
//        chatsProvider.remove(id)
//    }
//
//    override fun onSelect(key: String, model: Chat) = model.name.contains(key,true)
//
//    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
//        val root = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_dialog, parent, false)
//        return ChatViewHolder(root)
//    }
//
//    class ChatViewHolder(itemView: View) : FirebaseViewHolder<Chat>(itemView) {
//
//        var lastMsg : Disposable? = null
//        var lastSender : Disposable? = null
//
//        val imgView: AvatarImageView = itemView.findViewById(R.id.dialog_avatar)
//        val nameView: TextView = itemView.findViewById(R.id.dialog_name)
//        private val senderView: TextView = itemView.findViewById(R.id.dialog_last_message_sender)
//        private val messageView: TextView = itemView.findViewById(R.id.dialog_last_message)
//        private val dateView: TextView = itemView.findViewById(R.id.dialog_last_message_date)
//        private val unreadView: TextView = itemView.findViewById(R.id.dialog_unread_count)
//        init {
//            unreadView.visibility = View.INVISIBLE
//        }
//
//        override fun bind(model: Chat) {
//            super.bind(model)
//
//            lastMsg?.dispose()
//            lastSender?.dispose()
//
//            if (model.imageUri.isNotEmpty()) {
//                imgView.setImageURI(Uri.parse(model.imageUri))
//            } else {
//                imgView.setImageResource(R.drawable.logo256)
//            }
//            nameView.text = model.name
//            if (model.lastMessageId.isNotEmpty()) {
//                lastMsg = messagesProvider.get(model.id, model.lastMessageId)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                { msg ->
//                                    messageView.text = msg.text
//                                    if (msg.senderId == User().id) {
//                                        senderView.setText(R.string.title_you)
//                                    } else {
//                                        lastSender = usersProvider.get(id = msg.senderId)
//                                                .subscribeOn(Schedulers.io())
//                                                .observeOn(AndroidSchedulers.mainThread())
//                                                .subscribe (
//                                                        {u-> senderView.text = u.id},
//                                                        {TODO("get user failed")})
//                                        TODO("SENDER ID TO NAME")
//                                    }
//                                    senderView.append(":")
//                                    dateView.text = getTime(msg.time)
//                                },
//                                {
//                                    TODO("get message error")
//                                }
//                        )
//            } else {
//                messageView.text = ""
//                senderView.text = ""
//                dateView.text = ""
//            }
//        }
//    }
//
//    companion object {
//        val TAG = ChatRecyclerAdapter::class.java.simpleName
//
//        val usersProvider : UserListProvider by lazy { FirestoreUserListProvider() }
//
//        val chatsProvider: ChatsProvider by lazy { FirestoreChatsProvider() }
//
//        val messagesProvider: MessagesProvider by lazy { FirestoreMessagesProvider() }
//    }
//}