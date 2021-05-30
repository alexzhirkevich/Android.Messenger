package com.alexz.test

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.alexz.messenger.app.ui.fragments.ChatsAndChannelsRootFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.messenger.app.R

class BottomNavigationFragment : Fragment() {

    private var savedStateSparseArray = SparseArray<SavedState>()
    private var currentSelectItemId = R.id.fragment_profile

    companion object {
        const val SAVED_STATE_CONTAINER_KEY = "ContainerKey"
        const val SAVED_STATE_CURRENT_TAB_KEY = "CurrentTabKey"

        const val profile = "profile"
        const val chats = "chats"
        const val calls = "calls"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (savedInstanceState != null) {
            savedStateSparseArray =
                savedInstanceState.getSparseParcelableArray(SAVED_STATE_CONTAINER_KEY)
                    ?: savedStateSparseArray
            currentSelectItemId = savedInstanceState.getInt(SAVED_STATE_CURRENT_TAB_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar).apply {
            title = ""
        }

        //val navHost = childFragmentManager.findFragmentById(R.id.fragment_host_bottom_navigation) as NavHostFragment
        //bottomNavigationView.setupWithNavController(navHost.navController)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            setWindowTransparency(object : OnSystemInsetsChangedListener {
                override fun invoke(statusBarSize: Int, navigationBarSize: Int) {
                    (toolbar.layoutParams as ViewGroup.MarginLayoutParams).topMargin = statusBarSize
                    toolbar.requestLayout()

                    (bottomNavigationView.layoutParams as ViewGroup.MarginLayoutParams)
                        .bottomMargin = navigationBarSize
                    bottomNavigationView.requestLayout()
                }
            })
        }

        bottomNavigationView.selectedItemId = currentSelectItemId

        return view
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_profile -> {
                    swapFragments(item.itemId, profile)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.fragment_chats_channels -> {
                    swapFragments(item.itemId, chats)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.fragment_calls -> {
                    swapFragments(item.itemId, calls)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSparseParcelableArray(SAVED_STATE_CONTAINER_KEY, savedStateSparseArray)
        outState.putInt(SAVED_STATE_CURRENT_TAB_KEY, currentSelectItemId)
    }


    fun onBackPressed() {
        childFragmentManager.fragments.forEach { fragment ->
            if (fragment != null && fragment.isVisible) {
                with(fragment.childFragmentManager) {
                    if (backStackEntryCount > 0) {
                        popBackStack()
                        return
                    }
                }
            }
        }
    }

    private fun swapFragments(@IdRes actionId: Int, key: String) {
        if (childFragmentManager.findFragmentByTag(key) == null) {
            savedFragmentState(actionId)
            createFragment(key, actionId)
        }
    }

    private fun createFragment(key: String, actionId: Int) {
        when (key) {
            profile -> ProfileFragment()
            chats -> ChatsAndChannelsRootFragment()
            calls -> CallsFragment()
            else -> null
        }?.let {
            if (!it.isAdded) {
                it.setInitialSavedState(savedStateSparseArray[actionId])
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_host_bottom_navigation, it, key)
                .commit()
        }
    }

    private fun savedFragmentState(actionId: Int) {
        val currentFragment = childFragmentManager.findFragmentById(R.id.fragment_host_bottom_navigation)
        if (currentFragment != null) {
            savedStateSparseArray.put(currentSelectItemId,
                childFragmentManager.saveFragmentInstanceState(currentFragment)
            )
        }
        currentSelectItemId = actionId
    }
}