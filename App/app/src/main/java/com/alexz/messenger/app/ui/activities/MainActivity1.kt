//package com.alexz.messenger.app.ui.activities
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.FragmentStatePagerAdapter
//import androidx.viewpager.widget.ViewPager
//import com.alexz.messenger.app.ui.adapters.ChatsViewPagerAdapter
//import com.alexz.messenger.app.ui.viewmodels.ChannelActivityViewModel
//import com.alexz.messenger.app.ui.viewmodels.MainActivityViewModel
//import com.alexz.messenger.app.ui.views.AvatarImageView
//import com.alexz.messenger.app.util.FirebaseUtil
//import com.alexz.messenger.app.util.KeyboardUtil
//import com.google.android.material.navigation.NavigationView
//import com.google.android.material.tabs.TabLayout
//import com.messenger.app.R
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//
//class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
//
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var editSearch: EditText
//    private val viewModel : MainActivityViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        overridePendingTransition(R.anim.anim_alpha_in,R.anim.anim_alpha_out)
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        drawerLayout = findViewById(R.id.drawer_layout)
//        editSearch = findViewById(R.id.edit_search)
//        setupNavDrawer()
//        setupToolbar()
//        setupTabLayout()
//
//        val uri = intent.getParcelableExtra<Uri>(EXTRA_DATA)
//        uri?.let { processRequest(it) }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
//        } else if (editSearch.visibility == View.VISIBLE) {
//            editSearch.visibility = View.GONE
//            editSearch.setText("")
//            editSearch.layoutParams?.width = 0
//            editSearch.requestLayout()
//            if (!KeyboardUtil.hasHardwareKeyboard(this)) {
//                KeyboardUtil.hideKeyboard(editSearch)
//            }
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.nav_settings, R.id.nav_account, R.id.nav_about ->
//                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                drawerLayout.closeDrawer(GravityCompat.START)
//            } else {
//                drawerLayout.openDrawer(GravityCompat.START)
//            }
//            R.id.action_search ->   if (editSearch.visibility == View.VISIBLE) {
//                editSearch.visibility = View.GONE
//                editSearch.setText("")
//                if (!KeyboardUtil.hasHardwareKeyboard(this)) {
//                    KeyboardUtil.hideKeyboard(editSearch)
//                }
//            } else {
//                editSearch.visibility = View.VISIBLE
//                editSearch.requestLayout()
//                if (!KeyboardUtil.hasHardwareKeyboard(this)) {
//                    KeyboardUtil.showKeyboard(editSearch)
//                }
//                editSearch.requestFocus()
//            }
//        }
//        return true
//    }
//
//    private fun setupNavDrawer() {
//        val navigationView = findViewById<NavigationView>(R.id.nav_view)
//        val drawerAvatar: AvatarImageView = navigationView.getHeaderView(0).findViewById(R.id.image_drawer_avatar)
//        val email = navigationView.getHeaderView(0).findViewById<TextView>(R.id.text_drawer_email)
//        val name = navigationView.getHeaderView(0).findViewById<TextView>(R.id.text_drawer_name)
//        val account = FirebaseUtil.currentFireUser
//        if (account?.photoUrl != null) {
//            drawerAvatar.setImageURI(account.photoUrl)
//        }
//        if (email != null && account?.email != null) {
//            email.text = account.email
//        }
//        if (name != null && account?.displayName != null) {
//            name.text = account.displayName
//        }
//        navigationView.setNavigationItemSelectedListener(this)
//    }
//
//    private fun setupToolbar() {
//        val toolbar = findViewById<Toolbar>(R.id.dialogs_toolbar)
//        toolbar.setNavigationIcon(R.drawable.ic_menu)
//        setSupportActionBar(toolbar)
//    }
//
//    private fun setupTabLayout() {
//        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
//        val viewPager = findViewById<ViewPager>(R.id.main_viewpager)
//        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
//        viewPager.adapter = ChatsViewPagerAdapter(
//                supportFragmentManager,
//                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
//                getString(R.string.title_chats),
//                getString(R.string.title_channels))
//        tabLayout.setupWithViewPager(viewPager, true)
//    }
//
//    private fun processRequest(data : Uri) {
//        if (data.pathSegments.contains(FirebaseUtil.LINK_CHAT)) {
//            data.lastPathSegment?.let {
//                viewModel.joinChat(it)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                { chat -> ChatActivity.startActivity(this, chat) },
//                                { Toast.makeText(this, getString(R.string.error_channel_join), Toast.LENGTH_LONG).show() }
//                        )
//            }
//        } else
//            if (data.pathSegments.contains(FirebaseUtil.LINK_CHANNEL)) {
//                data.lastPathSegment?.let { id ->
//                    val viewModel: ChannelActivityViewModel by viewModels()
//                    viewModel.joinChannel(id)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(
//                                    { ChannelActivity.startActivity(this, it) },
//                                    { Toast.makeText(this, getString(R.string.error_channel_join), Toast.LENGTH_LONG).show() }
//                            )
//                }
//            }
//    }
//    companion object STARTER{
//        private const val STR_DIALOGS = "dialogs"
//        private const val STR_RECYCLER_DATA = "recyclerview_data"
//        private const val EXTRA_DATA = "EXTRA_DATA"
//        private const val SEGMENT_JOINCHANNEL = "joinchannel"
//
//
//
//        @JvmStatic
//        fun startActivity(context: Context,data: Uri? = null) {
//            val starter = Intent(context, MainActivity::class.java)
//            starter.putExtra(EXTRA_DATA,data)
//            context.startActivity(starter)
//        }
//    }
//}