package com.community.messenger.app.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentChatsBinding
import com.community.messenger.app.ui.adapters.recycler.ChatRecyclerAdapter
import com.community.messenger.app.ui.viewmodels.ChatsViewModel
import com.community.messenger.common.entities.interfaces.IChat

class ChatsFragment : ChatsAndChannelsPagerFragment<
        IChat,
        ChatRecyclerAdapter.ChatViewHolder,
        FragmentChatsBinding>() {

    private val viewModel: ChatsViewModel by activityViewModels()

    override val recyclerAdapter: com.community.recadapter.BaseRecyclerAdapter<IChat, ChatRecyclerAdapter.ChatViewHolder> by lazy {
        ChatRecyclerAdapter().apply {
            viewModel.data.observe(viewLifecycleOwner, {
                it.value?.let { set(it) }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_chats,menu)
    }

    override fun onItemClick(viewHolder: ChatRecyclerAdapter.ChatViewHolder) {
        super.onItemClick(viewHolder)
        requireParentFragment().requireParentFragment().parentFragmentManager.replace(
            R.id.fragment_host_main,TestFragment(), ChatFragment.newBundle(viewHolder.entity!!)
        ){
            setCustomAnimations(R.anim.anim_fragment_in,R.anim.anim_fragment_out,
                R.anim.anim_fragment_in,R.anim.anim_fragment_out)
        }
    }

    override fun onLongItemClick(viewHolder: ChatRecyclerAdapter.ChatViewHolder): Boolean {
        return super.onLongItemClick(viewHolder)
    }

    override fun onDestroyView() {
        super.onDestroyView()
       viewModel.data.removeObservers(viewLifecycleOwner)
    }


}
