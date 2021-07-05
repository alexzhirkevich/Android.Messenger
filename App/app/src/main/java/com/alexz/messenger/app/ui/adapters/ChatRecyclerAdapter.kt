package com.alexz.messenger.app.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.alexz.firerecadapter.BaseRecyclerAdapter
import com.alexz.firerecadapter.viewholder.BaseViewHolder
import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.data.entities.interfaces.IDialog
import com.alexz.messenger.app.data.entities.interfaces.IGroup
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import com.alexz.messenger.app.data.providers.test.TestMessagesProvider
import com.alexz.messenger.app.data.providers.test.TestUsersProvider
import com.alexz.messenger.app.ui.adapters.ChatRecyclerAdapter.ChatViewHolder
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.timeVisualizer
import com.messenger.app.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChatRecyclerAdapter : BaseRecyclerAdapter<IChat, ChatViewHolder>() {

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_constraint, parent, false)
        return ChatViewHolder(root)
    }

    override fun onViewRecycled(holder: ChatViewHolder) {
        super.onViewRecycled(holder)
        recycle(holder)
    }

    override fun onFailedToRecycleView(holder: ChatViewHolder): Boolean {
        recycle(holder)
        return super.onFailedToRecycleView(holder)
    }

    private fun recycle(holder: ChatViewHolder){
        holder.withUserDisposable?.dispose()
        holder.lastMessageDisposable?.dispose()
        holder.lastSenderDisposable?.dispose()
    }

    inner class ChatViewHolder(itemView: View) : BaseViewHolder<IChat>(itemView) {

        var lastMessageDisposable: Disposable? = null
        var lastSenderDisposable: Disposable? = null
        var withUserDisposable: Disposable? = null

        private val imgView: AvatarImageView = itemView.findViewById(R.id.chat_avatar)
        private val nameView: TextView = itemView.findViewById(R.id.chat_name)
        private val senderView: TextView = itemView.findViewById(R.id.chat_last_message_sender)
        private val messageView: TextView = itemView.findViewById(R.id.chat_last_message)
        private val dateView: TextView = itemView.findViewById(R.id.chat_last_message_date)
        private val unreadView: TextView = itemView.findViewById(R.id.chat_unread_count)
        private val selector: CheckBox = itemView.findViewById(R.id.chat_selector)

        override fun bind(entity: IChat) {
            super.bind(entity)

            val isSelected = isSelected(entity.id)

            selector.isVisible = inSelectingMode
            selector.isChecked = isSelected
            itemView.setBackgroundColor(ContextCompat.getColor(imgView.context,
                    if (isSelected) R.color.selected else R.color.app_back
            ))

            if (entity.lastMessageId.isNotEmpty()) {
                subscribeLastMessage(entity.id,entity.lastMessageId)
            } else {
                messageView.text = ""
                senderView.text = ""
                dateView.text = ""
            }

            when (entity) {
                is IGroup -> {
                    setupImage(entity.imageUri,entity.name)
                    nameView.text = entity.name
                }

                is IDialog -> {
                    val withUser = listOf(entity.user1,entity.user2)
                            .firstOrNull { it != usersProvider.currentUserId }
                    if (withUser != null){
                        subscribeWithUser(withUser)
                    }
                }
            }
        }

        private fun subscribeWithUser(userId : String){
            withUserDisposable?.dispose()
            withUserDisposable = usersProvider.get(userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {u->
                                setupImage(u.imageUri,u.name)
                                nameView.text = u.name
                            },
                            {}
                    )
        }

        private fun subscribeLastMessage(chatId : String ,msgID : String) {
            lastMessageDisposable?.dispose()
            lastMessageDisposable = messagesProvider.get(chatId, msgID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { msg ->
                                messageView.text = msg.text
                                if (msg.senderId == usersProvider.currentUserId) {
                                    senderView.setText(R.string.you)
                                } else {
                                    subscribeLastSender(msg.senderId)
                                }
                                senderView.append(":")
                                dateView.text = msg.time.timeVisualizer().timeNearly
                            },
                            {
                               // TODO("get message error")
                            }
                    )
        }

        private fun subscribeLastSender(senderId: String){
            lastSenderDisposable?.dispose()
            lastSenderDisposable = usersProvider.get(id = senderId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { u ->
                                senderView.text = u.name + ":"
                            },
                            {
                                //TODO("get user failed")
                            })
        }

        private fun setupImage(uri : String, name : String){
            if (uri.isNotEmpty()){
                imgView.setImageURI(Uri.parse(uri))
            }else {
                imgView.setupWithText(name, R.dimen.font_size_min,R.color.chats)
            }
        }
    }



    private companion object {
        private val TAG = ChatRecyclerAdapter::class.java.simpleName

        private val usersProvider : UsersProvider by lazy { TestUsersProvider() }

        private val messagesProvider: MessagesProvider by lazy { TestMessagesProvider() }
    }

}