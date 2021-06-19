package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexz.firerecadapter.BaseRecyclerAdapter
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.ui.adapters.ChannelRecyclerAdapter
import com.alexz.messenger.app.ui.viewmodels.ChannelViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.messenger.app.R

class ChannelsFragment : Fragment() {

    val viewModel : ChannelViewModel by viewModels()

    private val recyclerAdapter : BaseRecyclerAdapter<IChannel, ChannelRecyclerAdapter.ChannelViewHolder> by lazy {
        object : BaseRecyclerAdapter<IChannel, ChannelRecyclerAdapter.ChannelViewHolder>() {
            override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): ChannelRecyclerAdapter.ChannelViewHolder {
                val root = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_channel, parent, false)
                return ChannelRecyclerAdapter.ChannelViewHolder(root)
            }
        }.apply {
            viewModel.data.observe(viewLifecycleOwner, Observer {
                if (it.value != null) {
                   // thread(start = true) {
                        set(it.value)
                   // }
                }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_channels, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = parentFragment?.view?.findViewById<FloatingActionButton>(R.id.fab_chats_channels)
        view.findViewById<RecyclerView>(R.id.recyclerview_channels).apply {
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy>0)
                        fab?.hide()
                    else
                        fab?.show()
                }
            })
            adapter = recyclerAdapter
        }
    }
}

