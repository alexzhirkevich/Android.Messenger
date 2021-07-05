package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.alexz.firerecadapter.BaseRecyclerAdapter
import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.ui.adapters.ChatRecyclerAdapter
import com.alexz.messenger.app.ui.viewmodels.ChatsViewModel
import com.messenger.app.R

class ChatsFragment : ChatsAndChannelsPagerFragment<IChat,ChatRecyclerAdapter.ChatViewHolder>() {

    private val viewModel: ChatsViewModel by activityViewModels()

    override val recyclerAdapter: BaseRecyclerAdapter<IChat, ChatRecyclerAdapter.ChatViewHolder> by lazy {
        ChatRecyclerAdapter().apply {
            viewModel.data.observe(viewLifecycleOwner, Observer {
                it.value?.let { set(it) }
            })
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_chats,menu)
    }

    override fun onItemClick(viewHolder: ChatRecyclerAdapter.ChatViewHolder) {
        super.onItemClick(viewHolder)
    }

    override fun onLongItemClick(viewHolder: ChatRecyclerAdapter.ChatViewHolder): Boolean {
        return super.onLongItemClick(viewHolder)
    }

    override fun onDestroyView() {
        super.onDestroyView()
       viewModel.data.removeObservers(viewLifecycleOwner)
    }


}
