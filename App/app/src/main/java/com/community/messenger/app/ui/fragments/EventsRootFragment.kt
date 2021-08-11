package com.community.messenger.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.adapters.recycler.EventRecyclerAdapter
import com.community.messenger.app.ui.viewmodels.EventsViewModel
import com.community.messenger.app.ui.views.setTopMargin
import com.community.messenger.common.entities.imp.Event
import com.community.messenger.common.entities.interfaces.IEvent
import com.community.messenger.common.util.intents.AlarmIntent
import com.community.messenger.common.util.intents.EventIntent
import com.community.messenger.common.util.intents.MapIntent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentEventsBinding
import com.community.messenger.app.databinding.FragmentRootEventsBinding
import java.util.*
import kotlin.random.Random


class EventsRootFragment
    : MainActivity.EdgeToEdgeFragment<FragmentRootEventsBinding>() {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupTabLayout()

        val viewPagerTitles = arrayOf(getString(R.string.my_events), getString(R.string.public_events))
        TabLayoutMediator(binding.layoutTab, binding.viewPager) { tab, position ->
            tab.text = viewPagerTitles[position]
        }.attach()
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        binding.toolbar.setTopMargin(statusBarSize)
    }

    override fun onResume() {
        super.onResume()
        setToolbar(binding.toolbar.apply { title="" })
    }

    private fun setupViewPager(){
        binding.viewPager.apply {
            getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = 1
            adapter = object : FragmentStateAdapter(this@EventsRootFragment){

                override fun getItemCount(): Int = 2

                override fun createFragment(position: Int): Fragment =
                        if (position == 0) PersonalEventsFragment() else PublicEventsFragment()
            }
        }
    }

    private fun setupTabLayout() {
        binding.layoutTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            val duration = resources.getInteger(R.integer.anim_duration_medium)

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.fab.show()
                binding.fab.animate()
                        .rotation(tab.position * 90f)
                        .setDuration(duration.toLong())
                        .start()
            }
        })
    }
}

open class EventsPagerFragment : MainActivity.EdgeToEdgeFragment<FragmentEventsBinding>(){

    val viewModel : EventsViewModel by activityViewModels()

    private val fab : FloatingActionButton by lazy {
        requireParentFragment().findViewById<FloatingActionButton>(R.id.fab)
    }

    private val recyclerView : RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }
    }

    private val recyclerAdapter : EventRecyclerAdapter by lazy {
        EventRecyclerAdapter().apply {

            starClickListener = this@EventsPagerFragment::onStarClick
            eventClickListener = this@EventsPagerFragment::onEventClick
            addressClickListener = this@EventsPagerFragment::onAddressClick
            timeClickListener =  this@EventsPagerFragment::onTimeClick
            dateClickListener = this@EventsPagerFragment::onDateClick
            deleteButtonClickListener = this@EventsPagerFragment::onDeleteEventClick

            viewModel.data.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                it.value?.let { eventList ->
                    recyclerAdapter.set(onFilterEvents(eventList))
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy>0)
                    fab.hide()
                else
                    fab.show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fab.setOnClickListener { recyclerAdapter.add(Event(id = Random.nextInt(100000000).toString())) }
    }

    override fun onDestroyView() {
        viewModel.data.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    open fun onFilterEvents(events : Collection<IEvent>) : Collection<IEvent> {
        return events
    }

    @CallSuper
    open fun onDeleteEventClick(vh : EventRecyclerAdapter.EventViewHolder) {
        vh.removeAnimated()
    }


    @CallSuper
    open fun onEventClick(vh : EventRecyclerAdapter.EventViewHolder) {
        vh.setDescriptionVisible(!vh.isDescriptionVisible)
    }

    @CallSuper
    open fun onAddressClick(vh : EventRecyclerAdapter.EventViewHolder) {
        vh.entity?.let {
            MapIntent.Builder()
                    .setLatitude(it.location.first)
                    .setLongitude(it.location.second)
                    .setQuery(it.address)
                    .start(this)
        }
    }

    @CallSuper
    open fun onTimeClick(vh : EventRecyclerAdapter.EventViewHolder){
        vh.entity?.let {

            val calendar = Calendar.getInstance().apply { timeInMillis = it.time }

            AlarmIntent.Builder()
                    .setMessage(it.name)
                    .setDays(arrayListOf(calendar.get(Calendar.DAY_OF_WEEK)))
                    .setHours(calendar.get(Calendar.HOUR))
                    .setMinutes(calendar.get(Calendar.MINUTE))
                    .start(this)
        }
    }

    @CallSuper
    open fun onDateClick(vh : EventRecyclerAdapter.EventViewHolder) {
        vh.entity?.let {
            EventIntent.Builder()
                    .setTitle(it.name)
                    .setLocation(it.address)
                    .setBeginTime(it.time)
                    .setDescription(it.description)
                    .start(this)
        }
    }

    @CallSuper
    open fun onStarClick(vh : EventRecyclerAdapter.EventViewHolder){
        vh.isFavourite = !vh.isFavourite
    }
}

