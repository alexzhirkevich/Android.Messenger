//package com.community.messenger.app.ui.adapters
//
//import android.app.Activity
//import android.view.ViewGroup
//import com.community.firerecadapter.ItemClickListener
//import com.community.firerecadapter.realtimedb.RealtimeDatabaseListRecyclerAdapter
//import com.community.firerecadapter.viewholder.FirebaseViewHolder
//import com.community.messenger.common.entities.imp.MediaMessage
//import com.community.messenger.common.entities.imp.Message
//import com.community.messenger.common.entities.imp.VoiceMessage
//import com.community.messenger.app.ui.adapters.MessageRecyclerAdapter.MessageViewHolder
//import com.community.messenger.app.ui.common.contentgridlayout.ContentClickListener
//import com.community.messenger.app.ui.views.MessageView
//import com.community.messenger.core.providers.imp.FirebaseUtil.CHATS
//import com.community.messenger.core.providers.imp.FirebaseUtil.MEDIA_CONTENT
//import com.community.messenger.core.providers.imp.FirebaseUtil.MESSAGES
//import com.community.messenger.core.providers.imp.FirebaseUtil.VOICE_LEN
//import com.community.messenger.core.providers.imp.FirebaseUtil.VOICE_URI
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.Query
//
//class MessageRecyclerAdapter(private val chatID: String) :
//        RealtimeDatabaseListRecyclerAdapter<Message, MessageViewHolder>(Message::class.java) {
//
//    var contentClickListener: ContentClickListener? = null
//    var avatarClickListener: ItemClickListener<Message>? = null
//    var nameClickListener: ItemClickListener<Message>? = null
//
//    var transitionActivity : Activity? = null
//
//    override fun onCreateEntitiesQuery(): Query {
//        return FirebaseDatabase.getInstance().reference
//                .child(CHATS)
//                .child(chatID)
//                .child(MESSAGES)
//    }
//
//
//    override fun parse(snapshot: DataSnapshot): Message? {
//        return try {
//            when {
//                snapshot.hasChild(MEDIA_CONTENT) -> {
//                    snapshot.getValue(MediaMessage::class.java)
//                }
//                snapshot.hasChild(VOICE_LEN) and snapshot.hasChild(VOICE_URI) -> {
//                    return snapshot.getValue(VoiceMessage::class.java)
//                }
//                else -> super.parse(snapshot)
//            }
//        } catch (ignore: Throwable) {
//            null
//        }
//    }
//
//    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
//        return MessageViewHolder(MessageView(parent.context))
//    }
//
//    inner class MessageViewHolder(itemView: MessageView) : FirebaseViewHolder<Message>(itemView) {
//
//        val messageView = itemView
//
//        override fun bind(entity: Message) {
//            super.bind(entity)
//            messageView.bind(entity)
//        }
//
//        init {
//            messageView.avatarView.setOnClickListener {
//                avatarClickListener?.onItemClick(this)
//            }
//            messageView.avatarView.setOnLongClickListener {
//                if (avatarClickListener != null) {
//                    avatarClickListener?.onLongItemClick(this)
//                    return@setOnLongClickListener true
//                }
//                false
//            }
//            messageView.nameView.setOnClickListener {
//                nameClickListener?.onItemClick(this)
//            }
//            messageView.nameView.setOnLongClickListener {
//                if (nameClickListener != null) {
//                    nameClickListener?.onLongItemClick(this)
//                    return@setOnLongClickListener true
//                }
//                false
//            }
////            messageView.contentGrid.contentClickListener = object : ContentClickListener{
////                override fun onImageClick(view: ImageView, content: IMediaContent) {
////                    contentClickListener?.onImageClick(view,content)
////                }
////
////                override fun onVideoClick(playerView: PlayerView, content: IMediaContent) {
////                    contentClickListener?.onVideoClick(playerView,content)
////                }
////
////            }
//            messageView.contentGrid.setFullscreenTransition(transitionActivity)
//        }
//    }
//}