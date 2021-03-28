package com.alexz.messenger.app.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alexz.messenger.app.ui.adapters.ChatsViewPagerAdapter
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.FirebaseUtil
import com.alexz.messenger.app.util.KeyboardUtil
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.messenger.app.R

class DialogsActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawerLayout: DrawerLayout? = null
    private var editSearch: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        editSearch = findViewById(R.id.edit_search)
        setupNavDrawer()
        setupToolbar()
        setupTabLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            drawerLayout?.closeDrawer(GravityCompat.START)
        } else if (editSearch?.visibility == View.VISIBLE) {
            editSearch?.visibility = View.GONE
            editSearch?.setText("")
            editSearch?.layoutParams?.width = 0
            editSearch?.requestLayout()
            if (!KeyboardUtil.hasHardwareKeyboard(this)) {
                KeyboardUtil.hideKeyboard(editSearch)
            }
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings, R.id.nav_account, R.id.nav_about ->
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
        }

        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
                drawerLayout?.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout?.openDrawer(GravityCompat.START)
            }
            R.id.action_search -> if (editSearch != null) {
                if (editSearch?.visibility == View.VISIBLE) {
                    editSearch?.visibility = View.GONE
                    editSearch?.setText("")
                    editSearch?.layoutParams?.width = 0
                    editSearch?.requestLayout()
                    if (!KeyboardUtil.hasHardwareKeyboard(this)) {
                        KeyboardUtil.hideKeyboard(editSearch)
                    }
                } else {
                    editSearch?.visibility = View.VISIBLE
                    editSearch?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
                    editSearch?.requestLayout()
                    if (!KeyboardUtil.hasHardwareKeyboard(this)) {
                        KeyboardUtil.showKeyboard(editSearch)
                    }
                    editSearch?.requestFocus()
                }
            }
        }
        return true
    }

    private fun setupNavDrawer() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val drawerAvatar: AvatarImageView = navigationView.getHeaderView(0).findViewById(R.id.image_drawer_avatar)
        val email = navigationView.getHeaderView(0).findViewById<TextView>(R.id.text_drawer_email)
        val name = navigationView.getHeaderView(0).findViewById<TextView>(R.id.text_drawer_name)
        val account = FirebaseUtil.getCurrentFireUser()
        if (account.photoUrl != null) {
            drawerAvatar.setImageURI(account.photoUrl)
        }
        if (email != null && account.email != null) {
            email.text = account.email
        }
        if (name != null && account.displayName != null) {
            name.text = account.displayName
        }
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.dialogs_toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu)
        setSupportActionBar(toolbar)
    }

    private fun setupTabLayout() {
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager>(R.id.main_viewpager)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        viewPager.adapter = ChatsViewPagerAdapter(
                supportFragmentManager,
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                getString(R.string.title_chats),
                getString(R.string.title_channels))
        tabLayout.setupWithViewPager(viewPager, true)
    }

    companion object {
        private const val STR_DIALOGS = "dialogs"
        private const val STR_RECYCLER_DATA = "recyclerview_data"
    }
}