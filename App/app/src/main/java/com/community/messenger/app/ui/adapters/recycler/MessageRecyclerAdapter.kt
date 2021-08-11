package com.community.messenger.app.ui.adapters.recycler

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.community.messenger.app.R
import com.community.messenger.app.databinding.ItemMessageTextIncomingBinding
import com.community.messenger.app.databinding.ItemMessageTextOutcomingBinding
import com.community.messenger.common.entities.interfaces.IMediaMessage
import com.community.messenger.common.entities.interfaces.IMessage
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.common.entities.interfaces.IVoiceMessage
import com.community.messenger.common.util.dateTime
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.community.recadapter.BaseRecyclerAdapter
import com.community.recadapter.BaseViewHolder
import kotlinx.coroutines.rx2.asFlow

class MessageRecyclerAdapter(private val owner : LifecycleOwner) : BaseRecyclerAdapter<IMessage, MessageRecyclerAdapter.MessageViewHolder>() {

    private companion object {
        private const val VT_IN_TEXT = 1
        private const val VT_OUT_TEXT = 2
        private const val VT_IN_MEDIA = 3
        private const val VT_OUT_MEDIA = 4
        private const val VT_IN_VOICE = 5
        private const val VT_OUT_VOICE = 6

        private val usersProvider : UsersProvider by lazy {
            DaggerUsersProviderComponent.create().getProvider()
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return entities[position].id.hashCode().toLong()
    }

    private val users : MutableMap<String,LiveData<IUser>> = hashMapOf()

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        users.values.forEach { it.removeObservers(owner) }
        users.clear()
    }

    @Throws(IllegalArgumentException::class)
    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return when (viewType){
            VT_IN_TEXT -> IncomingTextMessageViewHolder(
                ItemMessageTextIncomingBinding.inflate(inflater,parent,false)
            )

            VT_OUT_TEXT -> OutcomingTextMessageViewHolder(
                ItemMessageTextOutcomingBinding.inflate(inflater,parent,false)
            )
            else -> throw IllegalArgumentException("Unknown message type")

        }
    }

    override fun onViewRecycled(holder: MessageViewHolder) {
        holder.entity?.senderId?.let {
            users[it]?.removeObserver(holder.userObserver)
        }
        super.onViewRecycled(holder)
    }

    @Throws(IllegalArgumentException::class)
    override fun getItemViewType(position: Int): Int {

        val item = entities[position]

        return when{
            item.id != usersProvider.currentUserId && item is IVoiceMessage ->
                VT_IN_VOICE
            item.id == usersProvider.currentUserId && item is IVoiceMessage ->
                VT_OUT_VOICE
            item.id != usersProvider.currentUserId && item is IMediaMessage<*> && item.text.isEmpty()->
                VT_IN_MEDIA
            item.id == usersProvider.currentUserId && item is IMediaMessage<*> && item.text.isEmpty() ->
                VT_OUT_MEDIA
            item.id != usersProvider.currentUserId ->
                VT_IN_TEXT
            item.id == usersProvider.currentUserId ->
                VT_OUT_TEXT
            else -> throw IllegalArgumentException("Unknown message type")
        }

    }

    open inner class MessageViewHolder(v : View) : BaseViewHolder<IMessage>(v){

        val userObserver = Observer<IUser> {
            setUser(it)
        }

        @CallSuper
        override fun onBind(entity: IMessage) {
            var userLiveData = users[entity.id]
            if (userLiveData == null){
                userLiveData = usersProvider.get(entity.id)
                    .asFlow().asLiveData(owner.lifecycleScope.coroutineContext)
                users[entity.id] = userLiveData
            }
            userLiveData.observe(owner,userObserver)
        }

        open fun setUser(user: IUser){

        }
    }

    inner class IncomingTextMessageViewHolder(private val binding : ItemMessageTextIncomingBinding)
        : MessageViewHolder(binding.root) {

        override fun onBind(entity: IMessage) {
            super.onBind(entity)

            binding.tvMessage.text = entity.text
            binding.tvDate.text = entity.time.dateTime(itemView.context).time

            binding.ivAvatar.isVisible = position == 0 || entities[position!!-1].id != entity.id
        }

        override fun setUser(user: IUser){
            with(binding) {

                ivAvatar.post {
                    if (user.imageUri.isEmpty()) {
                        ivAvatar.setupWithText(user.name, R.dimen.font_size_small, R.color.chats)
                    } else {
                        ivAvatar.setImageURI(Uri.parse(user.imageUri))
                    }
                }
                tvName.post {
                    tvName.text = user.name
                }
            }
        }
    }

    inner class OutcomingTextMessageViewHolder(private val binding : ItemMessageTextOutcomingBinding)
        : MessageViewHolder(binding.root) {

        @SuppressLint("MissingSuperCall")
        override fun onBind(entity: IMessage) {

            binding.tvMessage.text = entity.text
            binding.tvDate.text = entity.time.dateTime(itemView.context).time
        }
    }
}