//package com.community.messenger.app.ui.activities
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.MenuItem
//import android.view.View
//import android.widget.ProgressBar
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.widget.Toolbar
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.community.firerecadapter.LoadingCallback
//import com.community.messenger.app.ui.adapters.UserListRecyclerAdapter
//import com.community.messenger.app.ui.viewmodels.UserListActivityViewModel
//import com.community.features.avatarimageview.AvatarImageView
//import com.community.messenger.app.R
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//
//class UserListActivity : BaseActivity() {
//
//    private lateinit var usersRecyclerView: RecyclerView
//    private lateinit var adapter: UserListRecyclerAdapter
//    private val viewModel : UserListActivityViewModel by viewModels()
//    private var observerDisposable : Disposable?= null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_user_list)
//        val chatId = intent.getStringExtra(EXTRA_CHAT_ID).orEmpty()
//        setupRecyclerView(chatId)
//        setupToolbar()
//        setupChatInfo(chatId)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            onBackPressed()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        adapter.stopListening()
//        observerDisposable?.dispose()
//    }
//
//    private fun setupToolbar() {
//        val toolbar = findViewById<Toolbar>(R.id.chat_toolbar)
//        setSupportActionBar(toolbar)
//        val actionBar = supportActionBar
//        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true)
//            actionBar.setDisplayHomeAsUpEnabled(true)
//        }
//    }
//
//    private fun setupRecyclerView(chatId: String) {
//        usersRecyclerView = findViewById(R.id.user_recycler_view)
//        val layoutManager = LinearLayoutManager(this)
//        usersRecyclerView.layoutManager = layoutManager
//        adapter = UserListRecyclerAdapter(chatId)
//        usersRecyclerView.adapter = adapter
//        val pb = findViewById<ProgressBar>(R.id.user_loading_pb)
//        adapter.loadingCallback = object : LoadingCallback {
//            override fun onStartLoading() {
//                pb.visibility = View.VISIBLE
//            }
//
//            override fun onEndLoading() {
//                pb.visibility = View.GONE
//            }
//        }
//        adapter.startListening()
//    }
//
//    private fun setupChatInfo(chatId: String) {
//        val avatarImageView = findViewById<AvatarImageView>(R.id.chat_avatar)
//        val tvChatName = findViewById<TextView>(R.id.chat_name)
//        observerDisposable = viewModel.getChat(chatId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        {
//                            if (it != null) {
//                                val avatarUrl = it.imageUri
//                                val chatName = it.name
//                                Result
//                                if (tvChatName != null) {
//                                    tvChatName.text = chatName
//                                }
//                                avatarImageView?.setImageURI(Uri.parse(avatarUrl))
//                            }
//                        },
//                        {
//                            Toast.makeText(this@UserListActivity, getString(R.string.error_chat_load),
//                                    Toast.LENGTH_SHORT).show()
//                            finish()
//                        }
//                )
//    }
//
//    companion object {
//        private const val EXTRA_CHAT_ID = "EXTRA_CHAT_ID"
//        fun startActivity(context: Context, chatID: String?) {
//            val starter = Intent(context, UserListActivity::class.java)
//            starter.putExtra(EXTRA_CHAT_ID, chatID)
//            context.startActivity(starter)
//        }
//    }
//}