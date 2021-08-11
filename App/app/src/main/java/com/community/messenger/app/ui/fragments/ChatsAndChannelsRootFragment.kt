package com.community.messenger.app.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentRootChatsChannelsBinding
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.views.animateColor
import com.community.messenger.app.ui.views.setTopMargin
import com.community.messenger.common.entities.interfaces.IEntity
import com.community.recadapter.BaseViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChatsAndChannelsRootFragment
    : MainActivity.EdgeToEdgeFragment<FragmentRootChatsChannelsBinding>() {

    private var isFabOpen = false

    private val fabControls : Collection<View> by lazy {
        listOf(
            binding.layoutFab.btnWriteMessage,
            binding.layoutFab.btnCreateGroup,
            binding.layoutFab.btnCreateChannel,
            binding.layoutFab.tvWriteMessage,
            binding.layoutFab.tvCreateChannel,
            binding.layoutFab.tvCreateGroup
        )
    }

    private val chatsColor : Int by lazy { ContextCompat.getColor(requireContext(),R.color.chats) }
    private val channelsColor : Int by lazy { ContextCompat.getColor(requireContext(),R.color.channels) }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        binding.toolbar.setTopMargin(statusBarSize)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (isFabOpen) {
            onChatsFabClicked(binding.fab)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFabControls()
        binding.viewPager
            .apply {

            offscreenPageLimit = 1
            getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
            adapter = object : FragmentStateAdapter(this@ChatsAndChannelsRootFragment) {

                override fun getItemCount(): Int = 2

                override fun createFragment(position: Int): Fragment =
                        if (position == 0) ChatsFragment() else ChannelsFragment()
            }
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            val duration = resources.getInteger(R.integer.anim_duration_medium)
            val fabColors = listOf(chatsColor,channelsColor)

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.fab.apply {
                    show()
                    animateColor(
                        fabColors[1 - tab.position],
                        fabColors[tab.position],
                        duration.toLong()
                    )
                    animate()
                        .rotation(tab.position * 90f)
                        .setDuration(duration.toLong())
                        .start()
                }
            }
        })

        val viewPagerTitles = arrayOf(getString(R.string.chats), getString(R.string.channels))
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = viewPagerTitles[position]
        }.attach()

    }

    override fun onResume() {
        super.onResume()
        setToolbar(binding.toolbar.apply { title="" })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFabControls(){
        binding.fab.setOnClickListener(this::onChatsFabClicked)
        binding.fabShadow.setOnTouchListener { _, _ ->
            if(isFabOpen){
                onChatsFabClicked(binding.fab)
                true
            } else false
        }

        binding.layoutFab.root.apply {
            pivotX = com.community.messenger.common.util.MetrixUtil.dpToPx(requireContext(),225).toFloat()
            pivotY = com.community.messenger.common.util.MetrixUtil.dpToPx(requireContext(),160).toFloat()
            scaleX = 0.5f
            scaleY = 0.5f
        }

        fabControls.forEach {
            it.alpha = 0f
            it.visibility = View.INVISIBLE
        }

        setupChildFab(
            binding.layoutFab.btnWriteMessage,binding.layoutFab.tvWriteMessage,
            ContactsFragment::class.java,
            ContactsFragment.newBundle(ContactsFragment.ContactAction.writeMessage()))

//        setupChildFab(binding.layoutFab.btnCreateGroup,binding.layoutFab.tvCreateGroup,
//            ContactsFragment::class.java,
//            ContactsFragment.newBundle(ContactsFragment.ContactAction.groupInvite(Gro)))

        setupChildFab(
            binding.layoutFab.btnCreateChannel,binding.layoutFab.tvCreateChannel,
            EditChannelFragment::class.java)
    }

    private fun setupChildFab(
        btn : FloatingActionButton,
        tv : TextView,
        navigateTo : Class<out Fragment>,
        args : Bundle?= null) {

        listOf(btn, tv).forEach {
            it.setOnClickListener {
                requireParentFragment().parentFragmentManager
                    .replace(R.id.fragment_host_main, navigateTo.newInstance(),args) {
                        setCustomAnimations(R.anim.anim_fragment_in,R.anim.anim_fragment_out,
                            R.anim.anim_fragment_in,R.anim.anim_fragment_out)
                    }
                onChatsFabClicked(binding.fab)
            }
        }
    }

    private fun onChatsFabClicked(v:View){
        val animLen = resources.getInteger(R.integer.anim_duration_short).toLong()
        val onChats = binding.viewPager.currentItem == 0
        if (!isFabOpen) {
            isFabOpen = true

            binding.fab.animate().rotation(if (onChats) 135f else 225f).setDuration(animLen).start()
            binding.fab.animateColor(
                    if (onChats) chatsColor else channelsColor,
                            ContextCompat.getColor(requireContext(),R.color.shadow),
                    animLen
            )

            binding.fabShadow.animate().alpha(0.6f).setDuration(animLen).start()

            binding.layoutFab.root.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(animLen).start()

            fabControls.forEach {
                it.visibility = View.VISIBLE
                it.animate().alpha(1f).setDuration(animLen)
                        .start()
            }


        } else{
            isFabOpen = false

            binding.fabShadow.animate().alpha(0f).setDuration(animLen).start()

            binding.layoutFab.root.animate()
                    .scaleX(0.5f).scaleY(0.5f)
                    .setDuration(animLen).start()

            binding.fab.animate().rotation(if (onChats) 0f else 90f).setDuration(animLen).start()
            binding.fab.animateColor(
                    ContextCompat.getColor(requireContext(),R.color.shadow),
                    if (onChats) chatsColor else channelsColor,
                    animLen
            )

            fabControls.forEach {
                it.animate().alpha(0f).setDuration(animLen)
                        .withEndAction {
                            it.visibility = View.INVISIBLE
                        }
                        .start()
            }

        }
    }
}

abstract class ChatsAndChannelsPagerFragment<
        Entity: IEntity,
        VH : BaseViewHolder<Entity>,
        Binding : ViewBinding>
    : MainActivity.EdgeToEdgeFragment<Binding>() {

    protected abstract val recyclerAdapter: com.community.recadapter.BaseRecyclerAdapter<Entity, VH>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            adapter = recyclerAdapter.apply {
                itemClickListener = this@ChatsAndChannelsPagerFragment::onItemClick
                itemLongClickListener = this@ChatsAndChannelsPagerFragment::onLongItemClick
                onSelectedStateChangedListener = this@ChatsAndChannelsPagerFragment::onSelectedStateChanged
            }
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        fab.hide()
                    } else {
                        fab.show()
                    }
                }
            })
        }

        bpRemovable = addOnBackPressedListener {
            if (recyclerAdapter.inSelectingMode) {
                recyclerAdapter.inSelectingMode = false
                false
            } else
                true
        }
    }

    override fun onDestroyView() {
        bpRemovable?.remove()
        super.onDestroyView()
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        navBarSize = navigationBarSize
    }

    override fun onResume() {
        super.onResume()
        onSelectedStateChanged(recyclerAdapter.inSelectingMode)
//        isUiHidden = (bottomNavigationView.layoutParams as RelativeLayout.LayoutParams).bottomMargin<0
//        if (isUiHidden && !recyclerView.canScrollVertically(-1))
//            showUi()
    }

    @CallSuper
    open fun onItemClick(viewHolder: VH){
        if (recyclerAdapter.inSelectingMode) {
            val id = viewHolder.entity?.id
            if (id != null) {
                recyclerAdapter.setSelected(id, !recyclerAdapter.isSelected(id))
                tvToolbar.text = recyclerAdapter.selectedEntities.size.toString()
            }
        }
    }

    @CallSuper
    open fun onLongItemClick(viewHolder: VH): Boolean {
        val id = viewHolder.entity?.id
        if (id != null) {
            recyclerAdapter.setSelected(id, !recyclerAdapter.isSelected(id))
            tvToolbar.text = recyclerAdapter.selectedEntities.size.toString()
        }
        return true
    }

    private fun onSelectedStateChanged(selected: Boolean) {
        setHasOptionsMenu(selected)
        tvToolbar.isVisible = selected
        tvToolbar.text = recyclerAdapter.selectedEntities.size.toString()
        if (selected) {
            btnToolbar.apply {
                setImageResource(R.drawable.ic_cross)
                setOnClickListener {
                    recyclerAdapter.inSelectingMode = false
                }
            }
            tvToolbar.text = recyclerAdapter.selectedEntities.size.toString()
        } else {
            btnToolbar.apply {
                setImageResource(R.drawable.ic_search)

                setOnClickListener {
                    requireParentFragment().requireParentFragment().parentFragmentManager
                        .replace(R.id.fragment_host_main, SearchFragment(), null)
                }
            }
        }
    }

//    private fun hideUi(){
//        isAnimRunning = true
//        fab.hide()
//        ValueAnimator.ofInt(-navBarSize, bottomNavigationView.height).apply {
//            addUpdateListener {
//                (bottomNavigationView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = -(it.animatedValue as Int)
//                bottomNavigationView.requestLayout()
//                if ((it.animatedValue as Int) >= bottomNavigationView.height-navBarSize) {
//                    (fragmentContainerView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = navBarSize - bottomNavigationView.height + it.animatedValue as Int
//                    fragmentContainerView.requestLayout()
//                }
//            }
//            duration = fab.hideMotionSpec?.totalDuration ?: 150
//
//            doOnEnd {
//                isUiHidden = true
//                isAnimRunning = false
//
//            }
//        }.start()
//    }
//
//    private fun showUi(){
//        isAnimRunning = true
//        fab.show()
//        ValueAnimator.ofInt( bottomNavigationView.height, -navBarSize).apply {
//            addUpdateListener {
//                (bottomNavigationView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = -(it.animatedValue as Int)
//                bottomNavigationView.requestLayout()
//                if ((it.animatedValue as Int) <= 0) {
//                    (fragmentContainerView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = navBarSize + it.animatedValue as Int
//                    fragmentContainerView.requestLayout()
//                }
//            }
//            duration = fab.hideMotionSpec?.totalDuration ?: 150
//            doOnEnd {
//                isUiHidden = false
//                isAnimRunning = false
//            }
//        }.start()
//    }

//    private val fragmentContainerView : View by lazy {
//        requireParentFragment().requireParentFragment().findViewById(R.id.fragment_host_bottom_navigation)
//    }
//    private val bottomNavigationView : View by lazy {
//        requireParentFragment().requireParentFragment().findViewById(R.id.bottom_navigation)
//    }
//    private val bottomNavigationViewShadow : View by lazy {
//        requireParentFragment().requireParentFragment().findViewById(R.id.bottom_navigation_shadow)
//    }

    private val btnToolbar : ImageButton by lazy {
        requireParentFragment().findViewById<View>(R.id.toolbar).findViewById(R.id.search_view)
    }

    private val tvToolbar : TextView by lazy {
        requireParentFragment().findViewById<View>(R.id.toolbar).findViewById(R.id.tv_selected_count)
    }

    private val fab :FloatingActionButton by lazy {
        requireParentFragment().findViewById(R.id.fab)
    }

    private val recyclerView : RecyclerView by lazy {
        requireView().findViewById(R.id.recycler_view)
    }

    private var bpRemovable : Removable?=null

    private var navBarSize : Int = 0

    //private var isUiHidden = false
    //private var isAnimRunning = false
}

