package com.community.messenger.app.ui.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentChannelBinding
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.adapters.recycler.PostRecyclerAdapter
import com.community.messenger.app.ui.viewmodels.ChannelDataViewModel
import com.community.messenger.app.ui.viewmodels.ChannelDataViewModelFactory
import com.community.messenger.app.ui.views.setBottomMargin
import com.community.messenger.app.ui.views.setTopMargin
import com.community.messenger.common.entities.imp.Post
import com.community.messenger.common.entities.interfaces.IChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChannelFragment : MainActivity.EdgeToEdgeFragment<FragmentChannelBinding>() {

    companion object CREATOR {
        private const val EXTRA_CHANNEL = "EXTRA_CHANNEL"
        fun newBundle(channel : IChannel) =  bundleOf(
            EXTRA_CHANNEL to channel
        )
    }

    private val recyclerAdapter :PostRecyclerAdapter by lazy {
        PostRecyclerAdapter(binding.recyclerView,channel)
    }


    private val channel : IChannel by lazy {
        arguments?.getParcelable(EXTRA_CHANNEL)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(binding.toolbar.root)
        setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        binding.recyclerView.apply {
            //layoutManager = LinearLayoutManager(view.context)
            adapter = recyclerAdapter
        }

        setupInput()
        setChannelInfo(channel)
        observe()

        addOnBackPressedListener {
            it.remove()
            exit()
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.menu_channel,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                exit()
                false
            }
            else -> true
        }
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        with(binding){
            toolbar.root.setTopMargin(statusBarSize)
            binding.messageInput.setBottomMargin(navigationBarSize)
        }
    }

    private fun setCanPost(canPost : Boolean){
        with(binding){
            messageShadow.isVisible = canPost
            messageInput.isVisible = canPost
        }
    }

    private fun setChannelInfo(channel: IChannel){
        if (channel.imageUri.isNotEmpty())
            binding.toolbar.ivAvatar
                .setImageURI(Uri.parse(channel.imageUri))
        else
            binding.toolbar.ivAvatar
                .setupWithText(channel.name,R.dimen.font_size_medium,R.color.channels)
        binding.toolbar.tvName.text = channel.name
    }

    private fun setupInput(){

        val viewModel = initViewModel(channel.id)

        with(binding.messageInput){
            onSendClicked = {
                lifecycleScope.launch(Dispatchers.Main) {
                    try {
                        val post =  Post(
                            text = binding.editInput.text?.toString().orEmpty()
                        )
                        binding.editInput.text?.clear()
                        isKeyboardVisible = false
                        viewModel.post(post)
                    }catch (t : Throwable){
                        Toast.makeText(requireContext(),R.string.error_post,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setChannelSubscribers(subCount : Long){
        with(binding.toolbar) {
            tvName.text = channel.name
            tvSecondLine.text = "$subCount ${getString(R.string.subscribers).lowercase()}"
        }
    }

    private fun initViewModel(id : String) : ChannelDataViewModel{
        val viewModel: ChannelDataViewModel by viewModels {
            ChannelDataViewModelFactory(channel.id)
        }

        if (viewModel.parameter != channel.id){
            viewModel.parameter = channel.id
        }

        return viewModel
    }

    private fun observe() {

        val viewModel = initViewModel(channel.id)

        viewModel.subscribers.observe(viewLifecycleOwner, {
            it.value?.let { cnt -> setChannelSubscribers(cnt) }
        })

        viewModel.data.observe(viewLifecycleOwner, {
            it.value?.let { channel ->
                setChannelInfo(channel)
                recyclerAdapter.onChanged(channel)

            }
        })

        viewModel.admins.observe(viewLifecycleOwner, {
            setCanPost(it.value?.find { adm -> adm.id == viewModel.currentUserId }?.canPost == true)
        })


        viewModel.posts.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                it.value?.let { posts -> recyclerAdapter.set(posts.toList()) }
            }
        })

    }

    private fun exit(){
        parentFragmentManager.popBackStack()
    }
}
