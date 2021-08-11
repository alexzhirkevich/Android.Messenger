package com.community.messenger.app.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentChatsBinding
import com.community.messenger.app.ui.adapters.recycler.ChannelRecyclerAdapter
import com.community.messenger.app.ui.viewmodels.ChannelsViewModel
import com.community.messenger.common.entities.interfaces.IChannel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChannelsFragment : ChatsAndChannelsPagerFragment
    <IChannel, ChannelRecyclerAdapter.ChannelViewHolder, FragmentChatsBinding>() {

    override val recyclerAdapter : ChannelRecyclerAdapter by lazy {
        ChannelRecyclerAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        observe()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_chats,menu)
    }

    override fun onItemClick(viewHolder: ChannelRecyclerAdapter.ChannelViewHolder) {
        super.onItemClick(viewHolder)
        if (!recyclerAdapter.inSelectingMode) {
            requireParentFragment().requireParentFragment().parentFragmentManager.replace(
                R.id.fragment_host_main,
                ChannelFragment(),
                ChannelFragment.newBundle(viewHolder.entity!!)
            ) {
                setCustomAnimations(
                    R.anim.anim_fragment_in, R.anim.anim_fragment_out,
                    R.anim.anim_fragment_in, R.anim.anim_fragment_out
                )
            }
        }
    }

    private fun observe(){
        val viewModel : ChannelsViewModel by activityViewModels()

        viewModel.data.observe(viewLifecycleOwner, {
            it.value?.let { data -> recyclerAdapter.set(data) }
        })
    }

    private fun setupRecyclerView(){
        val fab = parentFragment?.view?.findViewById<FloatingActionButton>(R.id.fab)

        requireView().findViewById<RecyclerView>(R.id.recycler_view).apply {
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

    override fun onLongItemClick(viewHolder: ChannelRecyclerAdapter.ChannelViewHolder): Boolean {
        return super.onLongItemClick(viewHolder)
    }
}

