package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.alexz.messenger.app.ui.activities.animateColor
import com.alexz.test.ChatsAndChannelsViewPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.messenger.app.R

class ChatsAndChannelsRootFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var searchView: SearchView

    private var showSearch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        addOnBackPressedListener {

            if (!searchView.isIconified) {
                searchView.isIconified = true
                false
            } else {
                true
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        try {
            searchView.isIconified = true
            searchView.isVisible = !hidden
        }catch (t : Throwable){
            showSearch = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        (requireParentFragment().requireView()
//                .findViewById<FragmentContainerView>(R.id.fragment_host_bottom_navigation).
//                layoutParams as RelativeLayout.LayoutParams).removeRule(RelativeLayout.ALIGN_PARENT_TOP)

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        searchView = toolbar.findViewById<SearchView>(R.id.search)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab_chats_channels)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewpager_chats_channels)
        tabLayout = view.findViewById(R.id.tab_layout_chats_channels)


        viewPager.apply {

            adapter = ChatsAndChannelsViewPagerAdapter(this@ChatsAndChannelsRootFragment)

            val viewPagerTitles = arrayOf(getString(R.string.chats), getString(R.string.channels))

            TabLayoutMediator(tabLayout, this) { tab, position ->
                tab.text = viewPagerTitles[position]
            }.attach()
        }

        tabLayout.apply {

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

                val duration = resources.getInteger(R.integer.fab_anim_duration)
                val fabColors = listOf(
                        ContextCompat.getColor(requireContext(),R.color.red),
                        ContextCompat.getColor(requireContext(),R.color.green)
                )

                override fun onTabReselected(tab: TabLayout.Tab) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    fab.setOnClickListener {  }
                }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    fab.show()
                    fab.animateColor(fabColors[1-tab.position],fabColors[tab.position],duration.toLong())
                    fab.animate()
                            .rotation(tab.position*90f)
                            .setDuration(duration.toLong())
                            .start()
                }
            })
        }

        if (showSearch){
            searchView.isVisible = true
            searchView.isIconified = true
            showSearch = false
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_root_chats_channels, container, false)
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
