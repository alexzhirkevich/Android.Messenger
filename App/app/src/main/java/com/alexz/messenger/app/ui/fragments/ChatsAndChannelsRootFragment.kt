package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.alexz.test.ChatsAndChannelsViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.messenger.app.R

class ChatsAndChannelsRootFragment : Fragment() {

    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_root_chats_channels, container, false)


//        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
//        tabLayout.y = toolbar.y + toolbar.height

        tabLayout = view.findViewById(R.id.tab_layout_chats_channels)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewpager_chats_channels)

        viewPager.adapter = ChatsAndChannelsViewPagerAdapter(this)

        val viewPagerTitles = arrayOf( getString(R.string.chats), getString(R.string.channels))

        TabLayoutMediator(tabLayout,viewPager){tab, position ->
            tab.text = viewPagerTitles[position]

        }.attach()

        return view;
    }

    override fun onStart() {
        super.onStart()
        tabLayout.visibility = View.VISIBLE
    }


    override fun onStop() {
        super.onStop()
        tabLayout.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile_self,menu)
    }
}