//package com.alexz.messenger.app.ui.adapters
//
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.FragmentPagerAdapter
//import com.alexz.messenger.app.ui.fragments.ChannelsFragment
//import com.alexz.messenger.app.ui.fragments.ChatsFragment
//
//class ChatsViewPagerAdapter(fm: FragmentManager, behavior: Int, private val chatsTitle: String, private val channelsTitle: String) : FragmentPagerAdapter(fm, behavior) {
//    private val chatsFragment = ChatsFragment()
//    private val channelsFragment = ChannelsFragment()
//    override fun getCount(): Int {
//        return 2
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        return if (position == 0) {
//            chatsTitle
//        } else {
//            channelsTitle
//        }
//    }
//
//    override fun getItem(position: Int): Fragment {
//        return if (position == 0) {
//            chatsFragment
//        } else {
//            channelsFragment
//        }
//    }
//
//}