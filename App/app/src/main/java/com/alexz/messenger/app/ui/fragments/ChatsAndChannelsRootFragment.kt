package com.alexz.messenger.app.ui.fragments

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alexz.firerecadapter.BaseRecyclerAdapter
import com.alexz.firerecadapter.IEntity
import com.alexz.firerecadapter.viewholder.BaseViewHolder
import com.alexz.messenger.app.ui.activities.MainActivity
import com.alexz.messenger.app.ui.views.animateColor
import com.alexz.messenger.app.ui.views.setTopMargin
import com.alexz.messenger.app.util.MetrixUtil
import com.alexz.test.ChatsAndChannelsViewPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.messenger.app.R

class ChatsAndChannelsRootFragment : MainActivity.EdgeToEdgeFragment() {

    private val tabLayout: TabLayout by lazy { findViewById<TabLayout>(R.id.tab_layout_chats_channels) }
    private val toolbar : Toolbar by lazy {  findViewById<Toolbar>(R.id.toolbar) }
    private val viewPager : ViewPager2 by lazy { findViewById<ViewPager2>(R.id.viewpager_chats_channels) }

    private val btnWriteMessage : FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.btn_write_message) }
    private val btnCreateGroup : FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.btn_create_group) }
    private val btnCreateChannel : FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.btn_create_channel) }
    private val tvWriteMessage : TextView by lazy { findViewById<TextView>(R.id.tv_write_message) }
    private val tvCreateGroup : TextView by lazy { findViewById<TextView>(R.id.tv_create_group) }
    private val tvCreateChannnel : TextView by lazy { findViewById<TextView>(R.id.tv_create_channel) }

    private val fab : FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.fab) }
    private val fabShadow : View by lazy { findViewById<View>(R.id.fab_shadow) }
    private val layoutFab : ViewGroup by lazy { findViewById<ViewGroup>(R.id.layout_fab) }

    private var isFabOpen = false

    private val fabControls : Collection<View> by lazy { listOf(
            btnWriteMessage,btnCreateGroup,btnCreateChannel, tvWriteMessage,tvCreateChannnel,tvCreateGroup) }

    private val chatsColor : Int by lazy { ContextCompat.getColor(requireContext(),R.color.chats) }
    private val channelsColor : Int by lazy { ContextCompat.getColor(requireContext(),R.color.channels) }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        toolbar.setTopMargin(statusBarSize)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (isFabOpen) {
            onChatsFabClicked(fab)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_root_chats_channels, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewPager.apply {

            offscreenPageLimit = 1
            getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
            adapter = ChatsAndChannelsViewPagerAdapter(this@ChatsAndChannelsRootFragment)

            val viewPagerTitles = arrayOf(getString(R.string.chats), getString(R.string.channels))
            TabLayoutMediator(tabLayout, this) { tab, position ->
                tab.text = viewPagerTitles[position]
            }.attach()
        }

        setupFabControls()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            val duration = resources.getInteger(R.integer.anim_duration_medium)
            val fabColors = listOf(chatsColor,channelsColor)

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
//             fab.show()
                fab.animateColor(fabColors[1 - tab.position], fabColors[tab.position], duration.toLong())
                fab.animate()
                        .rotation(tab.position * 90f)
                        .setDuration(duration.toLong())
                        .start()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        setToolbar(toolbar.apply { title="" })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFabControls(){
        fab.setOnClickListener(this::onChatsFabClicked)
        fabShadow.setOnTouchListener { _, _ ->
            if(isFabOpen){
                onChatsFabClicked(fab)
                true
            } else false
        }
        layoutFab.apply {
            pivotX = MetrixUtil.dpToPx(requireContext(),225).toFloat()
            pivotY = MetrixUtil.dpToPx(requireContext(),160).toFloat()
            scaleX = 0.5f
            scaleY = 0.5f
        }

        fabControls.forEach {
            it.alpha = 0f
            it.visibility = View.INVISIBLE
        }
        listOf(btnWriteMessage,tvWriteMessage).forEach {
            it.setOnClickListener{
                requireParentFragment().parentFragmentManager.replace(
                        R.id.fragment_host_main,WriteMessageFragment()){
                    addToBackStack(WriteMessageFragment::class.java.name)
                }
                onChatsFabClicked(fab)
            }
        }
    }

    private fun onChatsFabClicked(v:View){
        val animLen = resources.getInteger(R.integer.anim_duration_short).toLong()
        val onChats = viewPager.currentItem == 0
        if (!isFabOpen) {
            isFabOpen = true

            fab.animate().rotation(if (onChats) 135f else 225f).setDuration(animLen).start()
            fab.animateColor(
                    if (onChats) chatsColor else channelsColor,
                            ContextCompat.getColor(requireContext(),R.color.shadow),
                    animLen
            )

            fabShadow.animate().alpha(0.6f).setDuration(animLen).start()

            layoutFab.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(animLen).start()

            fabControls.forEach {
                it.visibility = View.VISIBLE
                it.animate().alpha(1f).setDuration(animLen)
                        .start()
            }


        } else{
            isFabOpen = false

            fabShadow.animate().alpha(0f).setDuration(animLen).start()

            layoutFab.animate()
                    .scaleX(0.5f).scaleY(0.5f)
                    .setDuration(animLen).start()

            fab.animate().rotation(if (onChats) 0f else 90f).setDuration(animLen).start()
            fab.animateColor(
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

abstract class ChatsAndChannelsPagerFragment<Entity: IEntity, VH : BaseViewHolder<Entity>> : MainActivity.EdgeToEdgeFragment() {

    protected abstract val recyclerAdapter: BaseRecyclerAdapter<Entity,VH>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerAdapter.itemClickListener = this::onItemClick
        recyclerAdapter.itemLongClickListener = this::onLongItemClick
        recyclerAdapter.onSelectedStateChangedListener = this::onSelectedStateChanged

        recyclerView.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        fab.hide()
//                        if (!isUiHidden && !isAnimRunning) {
//                            hideUi()
//                        }
                    } else {
                        fab.show()
//                        if (isUiHidden && !isAnimRunning) {
//                            showUi()
//                        }
                    }
                }
            })
        }
    }


    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        navBarSize = navigationBarSize
    }

    override fun onPause() {
        super.onPause()
        backPressedRemovable?.remove()
    }

    override fun onResume() {
        super.onResume()
        onSelectedStateChanged(recyclerAdapter.inSelectingMode)
        backPressedRemovable = addOnBackPressedListener {
            if (recyclerAdapter.inSelectingMode) {
                recyclerAdapter.inSelectingMode = false
                false
            } else
                true
        }
        isUiHidden = (bottomNavigationView.layoutParams as RelativeLayout.LayoutParams).bottomMargin<0
        if (isUiHidden && !recyclerView.canScrollVertically(-1))
            showUi()
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
                    requireParentFragment().requireParentFragment()
                            .parentFragmentManager.replace(R.id.fragment_host_main, SearchFragment(), null)
                }
            }
        }
    }

    private fun hideUi(){
        isAnimRunning = true
        fab.hide()
        ValueAnimator.ofInt(-navBarSize, bottomNavigationView.height).apply {
            addUpdateListener {
                (bottomNavigationView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = -(it.animatedValue as Int)
                bottomNavigationView.requestLayout()
                if ((it.animatedValue as Int) >= bottomNavigationView.height-navBarSize) {
                    (fragmentContainerView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = navBarSize - bottomNavigationView.height + it.animatedValue as Int
                    fragmentContainerView.requestLayout()
                }
            }
            duration = fab.hideMotionSpec?.totalDuration ?: 150

            doOnEnd {
                isUiHidden = true
                isAnimRunning = false

            }
        }.start()
    }

    private fun showUi(){
        isAnimRunning = true
        fab.show()
        ValueAnimator.ofInt( bottomNavigationView.height, -navBarSize).apply {
            addUpdateListener {
                (bottomNavigationView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = -(it.animatedValue as Int)
                bottomNavigationView.requestLayout()
                if ((it.animatedValue as Int) <= 0) {
                    (fragmentContainerView.layoutParams as RelativeLayout.LayoutParams).bottomMargin = navBarSize + it.animatedValue as Int
                    fragmentContainerView.requestLayout()
                }
            }
            duration = fab.hideMotionSpec?.totalDuration ?: 150
            doOnEnd {
                isUiHidden = false
                isAnimRunning = false
            }
        }.start()
    }

    private val fragmentContainerView : View by lazy {
        requireParentFragment().requireParentFragment().findViewById<View>(R.id.fragment_host_bottom_navigation)
    }
    private val bottomNavigationView : View by lazy {
        requireParentFragment().requireParentFragment().findViewById<View>(R.id.bottom_navigation)
    }
    private val bottomNavigationViewShadow : View by lazy {
        requireParentFragment().requireParentFragment().findViewById<View>(R.id.bottom_navigation_shadow)
    }

    private val btnToolbar : ImageButton by lazy {
        requireParentFragment().findViewById<View>(R.id.toolbar).findViewById<ImageButton>(R.id.search)
    }

    private val tvToolbar : TextView by lazy {
        requireParentFragment().findViewById<View>(R.id.toolbar).findViewById<TextView>(R.id.tv_selected_count)
    }

    private val fab :FloatingActionButton by lazy {
        requireParentFragment().findViewById<FloatingActionButton>(R.id.fab)
    }

    private val recyclerView : RecyclerView by lazy {
        requireView().findViewById<RecyclerView>(R.id.recycler_view)
    }

    private var backPressedRemovable : Removable?=null

    private var navBarSize : Int = 0

    private var isUiHidden = false
    private var isAnimRunning = false
}

