package com.alexz.messenger.app.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexz.firerecadapter.AdapterCallback
import com.alexz.firerecadapter.ItemClickListener
import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
import com.alexz.messenger.app.data.entities.imp.*
import com.alexz.messenger.app.data.entities.interfaces.IMediaContent
import com.alexz.messenger.app.ui.adapters.MessageRecyclerAdapter
import com.alexz.messenger.app.ui.viewmodels.ChatActivityViewModel
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.ui.views.MessageInput
import com.alexz.messenger.app.util.PermissionChecker
import com.alexz.messenger.app.util.getDayMonth
import com.messenger.app.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.File

class ChatActivity : BaseActivity(),
        PopupMenu.OnMenuItemClickListener {

    private var chat: Chat? = null
    private var uploadedImage: String? = null
    private lateinit var adapter: MessageRecyclerAdapter
    private lateinit var messageInput: MessageInput
    val viewModel : ChatActivityViewModel by viewModels()

    private var disposer : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chat = intent.getParcelableExtra(EXTRA_CHAT)
        chat?.let {
            setupChatInfo(it)
            disposer = viewModel.createObserver(it.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { c -> setupChatInfo(c) }
        }
        setupRecyclerView()
        setupInput()
        setupToolbar()
        adapter.startListening()
        // mRecyclerView.postDelayed({ mRecyclerView.smoothScrollToPosition(mRecyclerView.bottom) }, 200)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else {
            when(item.itemId){
                R.id.menu_chat_invite-> chat?.id?.let {
                    viewModel.copyInviteLink(it)
                    Toast.makeText(this@ChatActivity, getString(R.string.action_link_copied), Toast.LENGTH_SHORT).show()
                    return true
                }
                R.id.menu_chat_users ->chat?.id?.let { UserListActivity.startActivity(this, it)}
            }
        }
        return true
    }

    private fun onSendClicked() {
        if (messageInput.text.toString().trim { it <= ' ' }.isEmpty() && uploadedImage == null) {
            return
        }
        chat?.let {
            val m = MediaMessage(it.id)
            m.text = messageInput.text.trim()
            m.isPrivate = !it.isGroup
            m.mediaContent = uploadedImage?.let { uri -> listOf(MediaContent(IMediaContent.IMAGE, uri)) }
                    ?: emptyList()
            viewModel.sendMessage(m)
            adapter.add(m, true)

            messageRecyclerView.postDelayed(
                    { messageRecyclerView.smoothScrollToPosition(messageRecyclerView.bottom) }, 100)
            uploadedImage?.let {
                uploadedImage = null; restoreAttachButton()
            }
        }
        messageInput.text = ""
        messageInput.isModCanBeChanged = true
        messageInput.inVoiceMode = true
    }

    private fun onAttachClicked() {
        if (uploadedImage == null) {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, SELECT_PHOTO)
        } else {
            uploadedImage = null
            restoreAttachButton()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            SELECT_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                onStartImageLoad()
                imageReturnedIntent?.data?.let { uri ->
                    messageInput.isModCanBeChanged = true
                    messageInput.inVoiceMode = false
                    messageInput.isModCanBeChanged = false
                    viewModel.uploadImage(uri)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext { pair ->
                                if (pair.second != null) {
                                    uploadedImage = pair.second.toString()
                                }
                            }
                            .doOnError { _ ->
                                Toast.makeText(
                                        this@ChatActivity,
                                        getString(R.string.error_upload_file),
                                        Toast.LENGTH_SHORT).show()
                                onImageAttached(null, false)
                            }
                }
            }
        }

    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STR_UPLOADED_IMAGE, uploadedImage)
        val recManager = messageRecyclerView.layoutManager
        recManager?.let {
            outState.putParcelable(STR_RECYCLER_DATA, it.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupRecyclerView()
        uploadedImage = savedInstanceState.getString(STR_UPLOADED_IMAGE)
        val layoutManager = messageRecyclerView.layoutManager
        layoutManager?.onRestoreInstanceState(savedInstanceState)
    }

    private fun newItemClickListener(): ItemClickListener<Message> {
        return object : ItemClickListener<Message> {

            override fun onLongItemClick(viewHolder: FirebaseViewHolder<Message>): Boolean {
                val pm = PopupMenu(this@ChatActivity, viewHolder.itemView)
                pm.gravity = Gravity.RIGHT
                viewHolder.entity?.let {
                    if (it.senderId == User().id) {
                        pm.inflate(R.menu.menu_message_out)
                    } else {
                        pm.inflate(R.menu.menu_message_in)
                    }
                    pm.setOnMenuItemClickListener { e: MenuItem ->
                        when (e.itemId) {
                            R.id.message_delete -> {
                                viewModel.deleteMessage(it.chatId,it.id)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.message_copy -> {
                                viewModel.copyMessage(it)
                                Toast.makeText(this@ChatActivity, getString(R.string.action_message_copied), Toast.LENGTH_SHORT).show()
                                return@setOnMenuItemClickListener true
                            }
                        }
                        false
                    }
                    pm.show()
                }
                return true
            }
        }
    }

    private fun restoreAttachButton() {
        messageInput.attachButton.apply {
            setColorFilter(ContextCompat.getColor(this@ChatActivity, R.color.color_primary))
            setImageResource(R.drawable.ic_attach)
            isEnabled = true
        }
        messageInput.isModCanBeChanged = true
        if (messageInput.text.isEmpty()){
            messageInput.inVoiceMode = true
        }
    }

    private fun onStartImageLoad() {
        messageInput.progressBar.visibility = View.VISIBLE
        messageInput.attachButton.isEnabled = false
    }

    private fun onImageAttached(image: Uri?, success: Boolean) {
        messageInput.progressBar.visibility = View.GONE
        val attach = messageInput.attachButton
        if (success) {
            attach.setImageURI(image)
            attach.colorFilter = null
            attach.isEnabled = true
        } else {
            restoreAttachButton()
            uploadedImage = null
        }
    }

    private fun setupRecyclerView() {
        val linearLayout = LinearLayoutManager(this)
        linearLayout.stackFromEnd = true
        messageRecyclerView.layoutManager = linearLayout
        adapter = MessageRecyclerAdapter(chat?.id ?: "")
        messageRecyclerView.adapter = adapter
        adapter.itemClickListener = newItemClickListener()
        adapter.adapterCallback = object : AdapterCallback<Message>{
            override fun onItemAdded(item: Message) {
                MediaPlayer.create(this@ChatActivity, R.raw.tap).apply {
                    start()
                }.setOnCompletionListener { it.release() }
            }

            override fun onItemRemoved(item: Message) {}
        }
        adapter.transitionActivity = this
        messageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val pos = (messageRecyclerView.layoutManager as LinearLayoutManager)
                        .findFirstVisibleItemPosition()
                if (pos >=0  && pos < adapter.models.size) {
                    chatPopupDate.text = getDayMonth(adapter.models[pos].time)
                    if (chatPopupDate.text.isNotEmpty()){
                        chatPopupDate.visibility = View.VISIBLE
                    } else{
                        chatPopupDate.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    private fun setupInput() {
        messageInput = findViewById(R.id.message_input)
        messageInput.apply {
            onSendClicked = { onSendClicked() }
            attachButton.setOnClickListener { onAttachClicked() }
            onVoiceTouched = {
                PermissionChecker(this@ChatActivity)
                        .check(Manifest.permission.RECORD_AUDIO)
                        ?.request(this@ChatActivity, REQ_MIC)
                        ?: startRecording()
            }
            onVoiceReleased = {
                stopRecording().doOnSuccess { time ->
                    if (chat == null || time == null || time < 500L && recorderOutput == null) {
                        return@doOnSuccess
                    }
                    viewModel.uploadVoice(Uri.fromFile(File(recorderOutput!!)))
                            .doOnNext { data ->
                                if (data != null) {
                                    if (chat != null) {
                                        val m = VoiceMessage(chat!!.id, data.first.toString(), time.toInt())
                                        viewModel.sendMessage(m)
                                        adapter.add(m, true)
                                    }
                                } else {
                                    TODO("VOICE LOADING PROGRESS")
                                }
                            }
                            .doOnError {
                                Toast.makeText(this@ChatActivity, getString(R.string.error_voice_send), Toast.LENGTH_SHORT).show()
                            }.subscribe()
                }.doOnError {
                    Toast.makeText(this@ChatActivity, getString(R.string.error_voice_send), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.chat_toolbar)
        toolbar?.let { setSupportActionBar(it) }
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupChatInfo(chat: Chat) {
        val avatarImageView = findViewById<AvatarImageView>(R.id.chat_avatar)
        val tvChatName = findViewById<TextView>(R.id.chat_name)
        if (tvChatName != null && chat.name.isNotEmpty()) {
            tvChatName.text = chat.name
        }
        if (avatarImageView != null && chat.imageUri.isNotEmpty()) {
            avatarImageView.setImageURI(Uri.parse(chat.imageUri))
        }
    }

    companion object STARTER {
        private const val REQ_MIC = 5341
        private const val EXTRA_CHAT = "EXTRA_CHAT"
        private const val PERM_STORAGE = 2001
        private const val SELECT_PHOTO = 1001
        private const val STR_RECYCLER_DATA = "rec_data"
        private const val STR_UPLOADED_IMAGE = "msg_image"
        fun startActivity(context: Context, chat: Chat?, bundle: Bundle? = null) {
            val intent = getIntent(context, chat)
            if (bundle == null) {
                context.startActivity(intent)
            } else {
                context.startActivity(intent, bundle)
            }
        }

        fun getIntent(context: Context?, chat: Chat?): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(EXTRA_CHAT, chat)
            return intent
        }
    }
}