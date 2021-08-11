//package com.community.messenger.app.ui.activities
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.MenuItem
//import android.widget.TextView
//import androidx.activity.viewModels
//import androidx.appcompat.widget.PopupMenu
//import androidx.appcompat.widget.Toolbar
//import androidx.fragment.app.FragmentContainerView
//import androidx.navigation.NavController
//import androidx.navigation.fragment.NavHostFragment
//import com.community.messenger.common.entities.imp.Channel
//import com.community.messenger.app.ui.fragments.ChannelPostsFragment
//import com.community.messenger.app.ui.viewmodels.ChannelActivityViewModel
//import com.community.features.avatarimageview.AvatarImageView
//import com.community.messenger.app.R
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//
//class ChannelActivity : BaseActivity(),
//        PopupMenu.OnMenuItemClickListener {
//
//    var channelDispose : Disposable? = null
//    private val viewModel: ChannelActivityViewModel by viewModels()
//
//    var fragmentContainer: FragmentContainerView? = null
//    private lateinit var fragmentController : NavController
//    private var channel: Channel? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_channel)
//
//
//        channel = intent.getParcelableExtra(EXTRA_CHANNEL)
//        channel?.let { c ->
//            channelDispose = viewModel.getChannel(c.id)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                            { c->
//                                channel = c
//                                setupChatInfo(c.id, c.name, c.imageUri) },
//                            {
//                                // TODO: 15.05.2021 channel observe fail
//                            }
//                    )
//            setupChatInfo(c.id, c.name, c.imageUri)
//            fragmentContainer = findViewById(R.id.channel_fragment_container)
//            fragmentController = (supportFragmentManager.findFragmentById(R.id.channel_fragment_container) as NavHostFragment).navController
//            fragmentController.navigate(R.id.fragment_posts, ChannelPostsFragment.newBundle(c))
//        }
//        setupToolbar()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        channelDispose?.dispose()
//    }
//
////    override fun onCreateOptionsMenu(menu: Menu): Boolean {
////        menuInflater.inflate(R.menu.menu_chat, menu)
////        return true
////    }
//
//    override fun onMenuItemClick(item: MenuItem?): Boolean {
//        onBackPressed()
//        return true
//    }
//
////    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        if (item.itemId == android.R.id.home) {
////            onBackPressed()
////        } else {
////            if (item.itemId == R.id.menu_channel_invite) {
////                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
////                val cd = ClipData.newPlainText("Chat id", channel?.id)
////                cm.setPrimaryClip(cd)
////                Toast.makeText(this@ChannelActivity, getString(R.string.action_chat_id_copied), Toast.LENGTH_SHORT).show()
////                return true
////            } else if (item.itemId == R.id.menu_chat_users) {
////                UserListActivity.startActivity(this, channel?.id)
////            }
////        }
////        return true
////    }
//
//    private fun setupToolbar() {
//        val toolbar = findViewById<Toolbar>(R.id.chat_toolbar)
//        toolbar?.let { setSupportActionBar(it) }
//        supportActionBar?.setHomeButtonEnabled(true)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//    }
//
//    private fun setupChatInfo(chatId: String, chatName: String?, chatPhoto: String?) {
//        val avatarImageView = findViewById<AvatarImageView>(R.id.chat_avatar)
//        val tvChatName = findViewById<TextView>(R.id.chat_name)
//        if (tvChatName != null && chatName != null && chatName.isNotEmpty()) {
//            tvChatName.text = chatName
//        }
//        if (avatarImageView != null && chatPhoto != null && chatPhoto.isNotEmpty()) {
//            avatarImageView.setImageURI(Uri.parse(chatPhoto))
//        }
//    }
//
//    companion object STARTER {
//        private const val EXTRA_CHANNEL = "EXTRA_CHAT"
//        private const val STR_RECYCLER_DATA = "rec_data"
//        fun startActivity(context: Context, channel: Channel?, bundle: Bundle? = null) {
//            if (bundle != null) {
//                context.startActivity(getIntent(context, channel),bundle)
//            } else{
//                context.startActivity(getIntent(context, channel))
//            }
//        }
//
//        fun getIntent(context: Context?, chat: Channel?): Intent {
//            val intent = Intent(context, ChannelActivity::class.java)
//            intent.putExtra(EXTRA_CHANNEL, chat)
//            return intent
//        }
//    }
//}