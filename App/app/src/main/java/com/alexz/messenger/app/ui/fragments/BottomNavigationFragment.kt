package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.alexz.test.OnSystemInsetsChangedListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.messenger.app.R


class BottomNavigationFragment : Fragment() {

    private val profileFragment = ProfileFragment()
    private val chatsFragment = ChatsAndChannelsRootFragment()
    private val callsFragment = CallsFragment()

    private var currentFragment: Fragment?=null

    @IdRes
    private val startFragmentId = R.id.fragment_chats_channels

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar).apply {
            title = ""
        }

        bottomNavigationView.apply {
            setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
            selectedItemId = startFragmentId
        }

        setToolbar(toolbar)
        setEdgeToEdge(object : OnSystemInsetsChangedListener {
            override fun invoke(statusBarSize: Int, navigationBarSize: Int) {
                toolbar.apply {
                    (layoutParams as ViewGroup.MarginLayoutParams).topMargin = statusBarSize
                    requestLayout()
                }
                bottomNavigationView.apply {
                    (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = navigationBarSize
                    requestLayout()
                }
            }
        })

        return view
    }

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.fragment_profile -> {
                        if (currentFragment != profileFragment) {
                            currentFragment = profileFragment
                            childFragmentManager.replace(R.id.fragment_host_bottom_navigation, profileFragment,
                                    currentFragment!!.javaClass.simpleName)
                        }
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.fragment_chats_channels -> {
                        if (currentFragment != chatsFragment) {
                            currentFragment = chatsFragment
                            childFragmentManager.replace(R.id.fragment_host_bottom_navigation, chatsFragment,
                                    currentFragment!!.javaClass.simpleName)
                        }
                        //swapFragments(item.itemId, chats)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.fragment_calls -> {
                        if (currentFragment != callsFragment) {
                            currentFragment = callsFragment
                            childFragmentManager.replace(R.id.fragment_host_bottom_navigation, callsFragment,
                                    currentFragment!!.javaClass.simpleName)
                        }
                        // swapFragments(item.itemId, calls)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
}

