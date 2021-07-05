package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.ui.adapters.ChannelRecyclerAdapter
import com.alexz.messenger.app.ui.viewmodels.ChannelsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.messenger.app.R

class ChannelsFragment : ChatsAndChannelsPagerFragment<IChannel,ChannelRecyclerAdapter.ChannelViewHolder>(){

    val viewModel : ChannelsViewModel by activityViewModels()

    override val recyclerAdapter : ChannelRecyclerAdapter by lazy {
        ChannelRecyclerAdapter().apply {
            viewModel.data.observe(viewLifecycleOwner, Observer {
                it.value?.let { set(it) }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_channels, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = parentFragment?.view?.findViewById<FloatingActionButton>(R.id.fab)

        view.findViewById<RecyclerView>(R.id.recycler_view).apply {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_chats,menu)
    }

    override fun onItemClick(viewHolder: ChannelRecyclerAdapter.ChannelViewHolder) {
        super.onItemClick(viewHolder)
    }

    override fun onLongItemClick(viewHolder: ChannelRecyclerAdapter.ChannelViewHolder): Boolean {
        return super.onLongItemClick(viewHolder)
    }

    override fun onDestroyView() {
        viewModel.data.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }
}

