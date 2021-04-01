package com.alexz.messenger.app.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexz.messenger.app.data.model.imp.Chat
import com.alexz.messenger.app.data.model.imp.MediaContent
import com.alexz.messenger.app.data.model.imp.MediaMessage
import com.alexz.messenger.app.data.model.imp.Message
import com.alexz.messenger.app.data.model.interfaces.IMediaContent
import com.alexz.messenger.app.ui.adapters.MessageRecyclerAdapter
import com.alexz.ItemClickListener
import com.alexz.messenger.app.ui.viewmodels.ChatActivityViewModel
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.ui.views.MessageInput
import com.alexz.messenger.app.util.FirebaseUtil
import com.messenger.app.R

class ChatActivity : BaseActivity(),
        PopupMenu.OnMenuItemClickListener {

    private var chat: Chat? = null
    private var uploadedImage: String? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adapter: MessageRecyclerAdapter
    private lateinit var viewModel: ChatActivityViewModel
    private lateinit var messageInput: MessageInput

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chat = intent.getParcelableExtra(EXTRA_CHAT)
        viewModel = ViewModelProvider(this).get(ChatActivityViewModel::class.java)
        chat?.let {
            viewModel.chatId = it.id
            setupChatInfo(it.id, it.name, it.imageUri)
        }
        viewModel.chatChangingState.observe(this,
                Observer { chat: Chat -> setupChatInfo(chat.id, chat.name, chat.imageUri) })
        setupRecyclerView()
        setupInput()
        setupToolbar()
        adapter.startListening()
        viewModel.startListening()
        mRecyclerView.postDelayed({ mRecyclerView.smoothScrollToPosition(mRecyclerView.bottom) }, 200)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
        viewModel.stopListening()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else {
            if (item.itemId == R.id.menu_chat_invite) {
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val cd = ClipData.newPlainText("Chat id", chat!!.id)
                cm.setPrimaryClip(cd)
                Toast.makeText(this@ChatActivity, getString(R.string.action_chat_id_copied), Toast.LENGTH_SHORT).show()
                return true
            } else if (item.itemId == R.id.menu_chat_users) {
                UserListActivity.startActivity(this, chat!!.id)
            }
        }
        return true
    }

    private fun onSendClicked(btn: ImageButton, input: TextView) {
        if (input.text.toString().trim { it <= ' ' }.isEmpty() && uploadedImage == null) {
            return
        }
        chat?.let {
            val m = MediaMessage(it.id)
            m.text = input.text.toString().trim()
            m.isPrivate = !it.isGroup
            m.mediaContent = uploadedImage?.let { uri -> listOf(MediaContent(IMediaContent.IMAGE, uri)) }
                    ?: emptyList()
            viewModel.sendMessage(m, getString(R.string.title_file))
            input.text = ""
            mRecyclerView.postDelayed({ mRecyclerView.smoothScrollToPosition(mRecyclerView.bottom) }, 100)
            uploadedImage?.let { uploadedImage = null; restoreAttachButton() }
        }
    }

    private fun onAttachClicked(btn: ImageButton, input: TextView) {
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
                FirebaseUtil.uploadPhoto(imageReturnedIntent?.data)
                        .addOnSuccessResultListener {
                            uploadedImage = it?.first.toString()
                            onImageAttached(imageReturnedIntent?.data, true)
                        }
                        .addOnErrorResultListener {
                            Toast.makeText(this@ChatActivity, getString(it), Toast.LENGTH_SHORT).show()
                            onImageAttached(null, false)
                        }
            }
            PERM_STORAGE -> if (resultCode == Activity.RESULT_OK) {
                onAttachClicked(messageInput.attachButton, messageInput.inputTextView)
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STR_UPLOADED_IMAGE, uploadedImage)
        val recManager = mRecyclerView.layoutManager
        recManager?.let {
            outState.putParcelable(STR_RECYCLER_DATA, it.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupRecyclerView()
        uploadedImage = savedInstanceState.getString(STR_UPLOADED_IMAGE)
        val layoutManager = mRecyclerView.layoutManager
        layoutManager?.onRestoreInstanceState(savedInstanceState)
    }

    private fun newItemClickListener(): ItemClickListener<Message> {
        return object : ItemClickListener<Message> {
            @SuppressLint("NonConstantResourceId")
            override fun onLongItemClick(view: View, data: Message?): Boolean {
                val pm = PopupMenu(this@ChatActivity, view)
                pm.gravity = Gravity.RIGHT
                if (data?.senderId == FirebaseUtil.getCurrentUser().id) {
                    pm.inflate(R.menu.menu_message_out)
                } else {
                    pm.inflate(R.menu.menu_message_in)
                }
                pm.setOnMenuItemClickListener { e: MenuItem ->
                    when (e.itemId) {
                        R.id.message_delete -> {
                            viewModel.deleteMessage(data)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.message_copy -> {
                            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val text = data?.text
                            val cd = ClipData.newPlainText("Msg from " + data?.senderId, text)
                            cm.setPrimaryClip(cd)
                            Toast.makeText(this@ChatActivity, getString(R.string.action_message_copied), Toast.LENGTH_SHORT).show()
                            return@setOnMenuItemClickListener true
                        }
                    }
                    false
                }
                pm.show()
                return true
            }
        }
    }

    private fun restoreAttachButton() {
        val attach = messageInput.attachButton
        attach.setColorFilter(ContextCompat.getColor(this, R.color.color_primary))
        attach.setImageResource(R.drawable.ic_attach)
        attach.isEnabled = true
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
        mRecyclerView = findViewById(R.id.message_recycler_view)
        val linearLayout = LinearLayoutManager(this)
        linearLayout.stackFromEnd = true
        mRecyclerView.layoutManager = linearLayout
        adapter = MessageRecyclerAdapter(chat?.id
                ?: "")
        mRecyclerView.adapter = adapter
        adapter.itemClickListener = newItemClickListener()
        //        adapter.setImageClickListener(new ItemClickListener<Message>() {
//            @Override
//            public void onItemClick(View view, Message data) {
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        ChatActivity.this,view,getString(R.string.util_transition_image_fullscreen));
//                FullscreenImageActivity.startActivity(ChatActivity.this, data.getMediaContent(),options.toBundle());
//            }
//
//            @Override
//            public boolean onLongItemClick(View view, Message data) {
//                return adapter.getOnItemClickListener().onLongItemClick(view,data);
//            }
//        });
    }

    private fun setupInput() {
        messageInput = findViewById(R.id.message_input)
        messageInput.sendButton.setOnClickListener {
            onSendClicked(it as ImageButton, messageInput.inputTextView);
        }
        messageInput.attachButton.setOnClickListener {
            onAttachClicked(it as ImageButton, messageInput.inputTextView)
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.chat_toolbar)
        toolbar?.let { setSupportActionBar(it) }
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupChatInfo(chatId: String, chatName: String?, chatPhoto: String?) {
        val avatarImageView = findViewById<AvatarImageView>(R.id.chat_avatar)
        val tvChatName = findViewById<TextView>(R.id.chat_name)
        var loadInfo = false
        if (tvChatName != null && chatName != null && chatName.isNotEmpty()) {
            tvChatName.text = chatName
        } else {
            loadInfo = true
        }
        if (avatarImageView != null && chatPhoto != null && chatPhoto.isNotEmpty()) {
            avatarImageView.setImageURI(Uri.parse(chatPhoto))
        } else {
            loadInfo = true
        }
        if (loadInfo) {
            FirebaseUtil.getChatInfo(chatId)
                    .addOnSuccessResultListener {
                        tvChatName?.text = it?.name
                        avatarImageView?.setImageURI(Uri.parse(it?.imageUri))
                    }
                    .addOnErrorResultListener() {
                        Toast.makeText(this@ChatActivity, getString(it), Toast.LENGTH_SHORT).show()
                        finish()
                    }
        }
    }

    companion object STARTER {
        private const val EXTRA_CHAT = "EXTRA_CHAT"
        private const val PERM_STORAGE = 2001
        private const val SELECT_PHOTO = 1001
        private const val STR_RECYCLER_DATA = "rec_data"
        private const val STR_UPLOADED_IMAGE = "msg_image"
        fun startActivity(context: Context, chat: Chat?) {
            context.startActivity(getIntent(context, chat))
        }

        fun getIntent(context: Context?, chat: Chat?): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(EXTRA_CHAT, chat)
            return intent
        }
    }
}