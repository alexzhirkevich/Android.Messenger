package com.community.messenger.app.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentChatBinding
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.adapters.recycler.MessageRecyclerAdapter
import com.community.messenger.app.ui.viewmodels.ChatDataViewModel
import com.community.messenger.app.ui.views.ProfileView
import com.community.messenger.app.ui.views.setBottomMargin
import com.community.messenger.app.ui.views.setTopMargin
import com.community.messenger.common.entities.interfaces.IChat
import com.community.messenger.common.entities.interfaces.IDialog
import com.community.messenger.common.entities.interfaces.IGroup
import com.community.messenger.common.entities.interfaces.IListable

class ChatFragment : MainActivity.EdgeToEdgeFragment<FragmentChatBinding>(){

    companion object CREATOR{

        private const val EXTRA_CHAT = "EXTRA_CHAT"

        fun newBundle(chat : IChat) = bundleOf(EXTRA_CHAT to chat)
    }

    private val chat : IChat by lazy { requireArguments().getParcelable(EXTRA_CHAT)!! }

    private val recyclerAdapter : MessageRecyclerAdapter by lazy {
        MessageRecyclerAdapter(viewLifecycleOwner).apply {
            itemClickListener = this@ChatFragment::onItemClick
            itemLongClickListener = this@ChatFragment::onItemLongClick
        }
    }

    private val viewModel : ChatDataViewModel by lazy {
        val vm : ChatDataViewModel by viewModels{ ChatDataViewModel.Factory(chat) }
        if (vm.parameter !== chat){
            vm.parameter = chat
        }
        vm
    }

    fun onItemClick(holder : MessageRecyclerAdapter.MessageViewHolder){

    }

    fun onItemLongClick(holder: MessageRecyclerAdapter.MessageViewHolder) : Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChat(chat)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(view.context).apply { stackFromEnd=true }
            adapter = recyclerAdapter
        }
        //setToolbar(binding.toolbar.root)
    }


    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        binding.toolbar.root.setTopMargin(statusBarSize)
        binding.messageInput.setBottomMargin(navigationBarSize)

    }

    private fun setupRecyclerView(){
        binding.toolbar
    }

    private fun setupChat(chat : IChat){
        when(chat){
            is IGroup -> {
                setupAvatarAndName(chat)
                viewModel.data.observe(viewLifecycleOwner, {
                        data -> data.value?.let { setupAvatarAndName(it as IGroup) }}
                )
                viewModel.members.observe(viewLifecycleOwner,{
                    setupSecondLine("$it ${getString(R.string.members)}")
                })
            }

            is IDialog -> viewModel.companion.observe(viewLifecycleOwner, {
                setupAvatarAndName(it)
                setupSecondLine(ProfileView.getOnlineString(requireContext(),it))
            })
        }
    }

    private fun setupAvatarAndName(source : IListable) {

        with(binding.toolbar) {
            if (source.imageUri.isEmpty()) {
                ivAvatar.setupWithText(source.name, R.dimen.font_size_small, R.color.chats)
            } else {
                ivAvatar.setImageURI(Uri.parse(source.imageUri))
            }
            tvName.text = source.name

        }
    }

    private fun setupSecondLine(text : String){
        binding.toolbar.tvSecondLine.text = text
    }
}