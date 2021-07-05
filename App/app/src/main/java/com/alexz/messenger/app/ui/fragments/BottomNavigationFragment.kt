package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.alexz.messenger.app.ui.activities.MainActivity
import com.alexz.messenger.app.ui.views.setBottomMargin
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.messenger.app.R


class BottomNavigationFragment : MainActivity.EdgeToEdgeFragment(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val profileFragment = SelfProfileFragment()
    private val chatsFragment = ChatsAndChannelsRootFragment()
    private val callsFragment = CallsFragment()
    private val eventsFragment = EventsFragment()
    private var currentFragment: Fragment?=null



    private val bottomNavigationView : BottomNavigationView by lazy{
        findViewById<BottomNavigationView>(R.id.bottom_navigation) }



    companion object CREATOR {

        fun newBundle(@IdRes fragment : Int) = bundleOf(EXTRA_START_FRAGMENT_ID to fragment)

        private const val EXTRA_START_FRAGMENT_ID = "EXTRA_START_FRAGMENT_ID"
        private const val EXTRA_CURRENT_FRAGMENT_ID = "EXTRA_CURRENT_FRAGMENT_ID"

    }

    private val startFragmentId : Int by lazy {
        arguments?.getInt(EXTRA_START_FRAGMENT_ID, R.id.fragment_chats_channels) ?: R.id.fragment_chats_channels }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_bottom_navigation, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView.apply {
            setOnNavigationItemSelectedListener(this@BottomNavigationFragment)
            selectedItemId = startFragmentId
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_CURRENT_FRAGMENT_ID,when (currentFragment){
            profileFragment -> R.id.fragment_profile
            chatsFragment -> R.id.fragment_chats_channels
            else -> R.id.fragment_calls
        })
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val curFragment = savedInstanceState?.getInt(EXTRA_CURRENT_FRAGMENT_ID)
        curFragment?.let { bottomNavigationView.selectedItemId = it }
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        bottomNavigationView.setBottomMargin(navigationBarSize)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.fragment_profile -> {
                replaceFragment(profileFragment)
            }
            R.id.fragment_chats_channels -> {
                replaceFragment(chatsFragment)
            }
            R.id.fragment_calls -> {
                replaceFragment(callsFragment)
            }
            R.id.fragment_events -> {
                replaceFragment(eventsFragment)
            }
            else -> false
        }
    }


    private fun replaceFragment(fragment : Fragment) : Boolean {
        return if (currentFragment != fragment) {
            currentFragment = fragment
            childFragmentManager.replace(R.id.fragment_host_bottom_navigation, fragment) {
                setTransition(FragmentTransaction.TRANSIT_NONE)
                disallowAddToBackStack()
            }
            true
        } else false
    }
}

