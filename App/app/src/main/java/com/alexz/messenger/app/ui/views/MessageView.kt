//package com.alexz.messenger.app.ui.views
//
//import android.content.Context
//import android.net.Uri
//import android.util.AttributeSet
//import android.view.View
//import android.widget.ImageView
//import android.widget.RelativeLayout
//import android.widget.TextView
//import androidx.appcompat.content.res.AppCompatResources
//import androidx.cardview.widget.CardView
//import androidx.core.view.GravityCompat
//import com.alexz.messenger.app.data.entities.interfaces.IMediaMessage
//import com.alexz.messenger.app.data.entities.interfaces.IMessage
//import com.alexz.messenger.app.data.entities.interfaces.IVoiceMessage
//import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
//import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
//import com.alexz.messenger.app.data.repo.MessagesRepository
//import com.alexz.messenger.app.data.repo.UserListRepository
//import com.alexz.messenger.app.ui.common.contentgridlayout.ContentGridLayout
//import com.alexz.messenger.app.util.FirebaseUtil
//import com.alexz.messenger.app.util.MetrixUtil
//import com.alexz.messenger.app.util.getTime
//import com.messenger.app.R
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//
//class MessageView : RelativeLayout {
//
//    lateinit var msgDataLayout: RelativeLayout
//        private set
//    lateinit var avatarView: AvatarImageView
//        private set
//    lateinit var imageView: ImageView
//        private set
//    lateinit var nameView: TextView
//        private set
//    lateinit var textView: TextView
//        private set
//    lateinit var dateView: TextView
//        private set
//    lateinit var contentCardView: CardView
//        private set
//    lateinit var contentGrid : ContentGridLayout
//        private set
//    lateinit var voiceView : VoiceView
//        private set
//
//    private var bindedMessage: IMessage?=null
//
//    var disposable : Disposable? = null
//
//    private val textVerticalMargin: Int = resources.getDimension(R.dimen.message_text_vertical_margin).toInt()
//
//    constructor(context: Context) : super(context) {
//        init(context)
//    }
//
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        init(context)
//    }
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        init(context)
//    }
//
//    fun bind(message: IMessage) {
//        disposable?.dispose()
//        disposable = usersProvider.get(id = message.senderId)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        {
//                            if (bindedMessage?.isPrivate == true ||
//                                    bindedMessage?.senderId == FirebaseUtil.currentFireUser?.uid) {
//                                nameView.text = ""
//                            } else {
//                                nameView.text = it.name
//                            }
//                            if (bindedMessage?.isPrivate == false) {
//                                if (it.imageUri.isNotEmpty()) {
//                                    avatarView.setImageURI(Uri.parse(it.imageUri))
//                                } else {
//                                    avatarView.setImageResource(R.drawable.logo512)
//                                }
//                            }
//                        },
//                        {
//                            // TODO: 15.05.2021 observe user fail
//                        }
//                )
//        bindedMessage = message
//        val isOutcoming = message.senderId == FirebaseUtil.currentFireUser?.uid
//        if (!isOutcoming && !message.isPrivate) {
//            avatarView.visibility = View.VISIBLE
//            //avatarView.setImageURI(Uri.parse(uri))
//            //nameView.text = message.senderName
//            //nameView.visibility = View.VISIBLE
//        } else {
//            avatarView.visibility = View.INVISIBLE
//            //nameView.visibility = View.GONE
//            //nameView.text = ""
//        }
//        message.text
//        textView.text = message.text
//        textView.visibility = View.VISIBLE
//        voiceView.visibility = View.GONE
//        contentGrid.visibility = View.GONE
//        when(message) {
//            is IMediaMessage<*> -> {
//                val contentList = (message as IMediaMessage<*>).mediaContent
//                if (contentList.isNotEmpty()) {
//                    contentGrid.clearContent()
//                    for (content in (message as IMediaMessage<*>).mediaContent) {
//                        contentGrid.addContent(content)
//                    }
//                    contentGrid.reGroup()
//                    contentGrid.visibility = View.VISIBLE
//                }
//            }
//            is IVoiceMessage -> {
//                textView.visibility = View.GONE
//                voiceView.visibility = View.VISIBLE
//                if (isOutcoming)
//                    voiceView.background = AppCompatResources.getDrawable(context,R.drawable.drawable_message_outcoming)
//                else{
//                    voiceView.background = AppCompatResources.getDrawable(context,android.R.color.transparent)
//
//                }
//                voiceView.uri = Uri.parse(message.voiceUri)
//                voiceView.length = message.voiceLen.toLong()
//            }
//            else -> {
//                contentGrid.visibility = View.GONE
//            }
//        }
//        message.time
//        dateView.text = getTime(message.time)
//        transformLayoutParams(message, isOutcoming)
//    }
//
//    fun setNameClickListener(nameClickListener: OnClickListener?) {
//        nameView.setOnClickListener { view: View? ->
//            nameClickListener?.onClick(view)
//        }
//    }
//
//    fun setAvatarClickListener(avatarClickListener: OnClickListener?) {
//        avatarView.setOnClickListener { view: View? ->
//            avatarClickListener?.onClick(view)
//        }
//    }
//
//    fun setImageClickListener(imageClickListener: OnClickListener?) {
//        imageView.setOnClickListener { view: View? ->
//            imageClickListener?.onClick(view)
//        }
//    }
//
//    fun setNameLongClickListener(nameClickListener: OnLongClickListener?) {
//        nameView.setOnLongClickListener { view: View? ->
//            if (nameClickListener != null) {
//                return@setOnLongClickListener nameClickListener.onLongClick(view)
//            }
//            false
//        }
//    }
//
//    fun setAvatarLongClickListener(avatarClickListener: OnLongClickListener?) {
//        avatarView.setOnLongClickListener { view: View? ->
//            if (avatarClickListener != null) {
//                return@setOnLongClickListener avatarClickListener.onLongClick(view)
//            }
//            false
//        }
//    }
//
//    fun setImageLongClickListener(imageClickListener: OnLongClickListener?) {
//        imageView.setOnLongClickListener { view: View? ->
//            if (imageClickListener != null) {
//                return@setOnLongClickListener imageClickListener.onLongClick(view)
//            }
//            false
//        }
//    }
//
//    private fun init(context: Context) {
//        inflate(context, R.layout.item_message, this)
//        msgDataLayout = findViewById(R.id.message_data_layout)
//        avatarView = findViewById(R.id.message_avatar)
//        nameView = findViewById(R.id.message_sender)
//        textView = findViewById(R.id.message_text)
//        dateView = findViewById(R.id.message_date)
//        contentGrid = findViewById(R.id.message_media_content)
//        contentCardView = findViewById(R.id.message_content_cardview)
//        voiceView = findViewById(R.id.message_voice)
//        isClickable = true
//        isLongClickable = true
//    }
//
//    private fun transformLayoutParams(message: IMessage, outcoming: Boolean) {
//        val dataParams = msgDataLayout.layoutParams as LayoutParams
//        //RelativeLayout.LayoutParams dateParams = (RelativeLayout.LayoutParams) date.getLayoutParams();
//        val textParams = textView.layoutParams as LayoutParams
//        val contentParams = contentCardView.layoutParams as LayoutParams
//        val voiceParams = voiceView.layoutParams as LayoutParams
//
//        //dateParams.addRule(RelativeLayout.BELOW,R.id.message_image);
//        contentParams.removeRule(ALIGN_PARENT_TOP)
//        contentParams.removeRule(BELOW)
//        dataParams.removeRule(END_OF)
//        dataParams.removeRule(ALIGN_PARENT_END)
//        dataParams.removeRule(ALIGN_PARENT_START)
//        textParams.removeRule(TEXT_ALIGNMENT_GRAVITY)
//        textParams.removeRule(ALIGN_PARENT_TOP)
//        textParams.removeRule(BELOW)
//        dataParams.marginEnd = 0
//        dataParams.marginStart = 0
//        if (outcoming) {
//            msgDataLayout.setBackgroundColor(msgDataLayout.resources.getColor(R.color.message_outcoming))
//            msgDataLayout.background = AppCompatResources.getDrawable(msgDataLayout.context, R.drawable.drawable_message_outcoming)
//            dataParams.addRule(ALIGN_PARENT_END)
//            textParams.addRule(TEXT_ALIGNMENT_GRAVITY, GravityCompat.END)
//            textParams.addRule(ALIGN_PARENT_TOP)
//            textView.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
//            textParams.setMargins(textParams.leftMargin, textVerticalMargin, textParams.rightMargin, textParams.bottomMargin)
//            dataParams.marginStart = MetrixUtil.dpToPx(msgDataLayout.context, 50)
//            if (message is IMediaMessage<*> && (message as IMediaMessage<*>).mediaContent.isNotEmpty()) {
//                if (message.text.isEmpty()) {
//                    contentParams.addRule(ALIGN_PARENT_TOP)
//                } else {
//                    contentParams.addRule(BELOW, R.id.message_text)
//                }
//            } else if (message is IVoiceMessage){
//                voiceParams.addRule(ALIGN_PARENT_TOP)
//            }
//        } else {
//            voiceParams.removeRule(ALIGN_PARENT_TOP)
//            textView.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
//            msgDataLayout.setBackgroundColor(msgDataLayout.resources.getColor(R.color.message_incoming))
//            dataParams.marginEnd = MetrixUtil.dpToPx(msgDataLayout.context, 50)
//            textParams.addRule(TEXT_ALIGNMENT_GRAVITY, GravityCompat.START)
//            dataParams.addRule(END_OF, R.id.message_avatar)
//            if (message.isPrivate) {
//                msgDataLayout.background = AppCompatResources.getDrawable(msgDataLayout.context, R.drawable.drawable_message_incoming_private)
//            } else {
//                msgDataLayout.background = AppCompatResources.getDrawable(msgDataLayout.context, R.drawable.drawable_message_incoming_group)
//            }
//            if (message.text.isEmpty()) {
//                if (message.isPrivate) {
//                    contentParams.addRule(ALIGN_PARENT_TOP)
//                } else {
//                    contentParams.addRule(BELOW, R.id.message_sender)
//                }
//            } else {
//                //dateParams.addRule(RelativeLayout.BELOW,R.id.message_text);
//                contentParams.addRule(BELOW, R.id.message_text)
//                if (message.isPrivate) {
//                    textParams.addRule(ALIGN_PARENT_TOP)
//                    textParams.setMargins(textParams.leftMargin, textVerticalMargin, textParams.rightMargin, textParams.bottomMargin)
//                } else {
//                    textParams.addRule(BELOW, R.id.message_sender)
//                    textParams.setMargins(textParams.leftMargin, 0, textParams.rightMargin, textParams.bottomMargin)
//                }
//            }
//        }
//        msgDataLayout.requestLayout()
//        textView.requestLayout()
//        contentGrid.requestLayout()
//        voiceView.requestLayout()
//        //date.requestLayout();
//    }
//
//    companion object {
//        val messagesProvider : MessagesProvider by lazy { MessagesRepository() }
//        val usersProvider : UserListProvider by lazy { UserListRepository() }
//    }
//}