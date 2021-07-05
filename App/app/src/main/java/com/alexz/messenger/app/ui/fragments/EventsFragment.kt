package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexz.messenger.app.ui.activities.MainActivity
import com.alexz.messenger.app.ui.adapters.EventRecyclerAdapter
import com.alexz.messenger.app.ui.viewmodels.EventsViewModel
import com.alexz.messenger.app.ui.views.setTopMargin
import com.messenger.app.R

class EventsFragment : MainActivity.EdgeToEdgeFragment() {

    val viewModel : EventsViewModel by activityViewModels()

    private val toolbar : Toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }

    private val recyclerView : RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private val recyclerAdapter : EventRecyclerAdapter by lazy {
        EventRecyclerAdapter().apply {
            viewModel.data.observe(viewLifecycleOwner, Observer {
                it.value?.let { set(it) }
            })
            starClickListener = this@EventsFragment::onStarClicked
            eventClickListener = this@EventsFragment::onEventClicked
        }
    }

    private fun onEventClicked(vh : EventRecyclerAdapter.EventViewHolder) {
        vh.setDescriptionVisible(!vh.isDescriptionVisible)
    }

    private fun onStarClicked(vh : EventRecyclerAdapter.EventViewHolder){
        vh.isFavourite = !vh.isFavourite
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        toolbar.setTopMargin(statusBarSize)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView
    }
}