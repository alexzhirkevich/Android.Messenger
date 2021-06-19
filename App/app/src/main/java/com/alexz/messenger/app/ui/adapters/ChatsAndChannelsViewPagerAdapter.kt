package com.alexz.test

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alexz.messenger.app.ui.fragments.ChannelsFragment
import com.alexz.messenger.app.ui.fragments.ChatsFragment

class ChatsAndChannelsViewPagerAdapter(fragment : Fragment) : FragmentStateAdapter(fragment){

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        if (position == 0) ChatsFragment() else ChannelsFragment()
}
