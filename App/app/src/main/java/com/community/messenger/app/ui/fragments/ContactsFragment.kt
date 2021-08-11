package com.community.messenger.app.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.messenger.app.R
import com.community.messenger.app.data.settings
import com.community.messenger.app.databinding.FragmentContactsBinding
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.adapters.recycler.UserRecyclerAdapter
import com.community.messenger.app.ui.viewmodels.ContactsViewModel
import com.community.messenger.app.ui.viewmodels.ContactsViewModelFactory
import com.community.messenger.app.ui.views.CustomDialogBuilder
import com.community.messenger.app.ui.views.setBottomMargin
import com.community.messenger.app.ui.views.setTopMargin
import com.community.messenger.common.entities.interfaces.IGroup
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.common.util.permissions
import com.community.recadapter.BaseRecyclerAdapter
import com.community.recadapter.BaseViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.Serializable

class ContactsFragment
    : MainActivity.EdgeToEdgeFragment<FragmentContactsBinding>() {

    abstract class ContactAction : Serializable {

        companion object {
            @JvmStatic
            fun writeMessage(): ContactAction = WriteMessageAction()

            @JvmStatic
            fun groupInvite(group : IGroup, isNewGroup : Boolean): ContactAction = GroupInviteAction(group,isNewGroup)


            @JvmStatic
            fun channelInvite(id: String): ContactAction = ChannelInviteAction(id)
        }

    }


    private class WriteMessageAction : ContactAction()
    private class GroupInviteAction(val group : IGroup,val isNewGroup : Boolean) : ContactAction()
    private class ChannelInviteAction(val channelId: String) : ContactAction()


    companion object Builder {

        private const val PERMISSION_READ_CONTACT = 1337
        private const val EXTRA_ACTION = "EXTRA_ACTION"

        @JvmStatic
        fun newBundle(action: ContactAction) = bundleOf(
            EXTRA_ACTION to action
        )
    }

    val viewModel: ContactsViewModel by activityViewModels {
        ContactsViewModelFactory(requireContext().contentResolver, settings)
    }

    private val action: ContactAction? by lazy { arguments?.get(EXTRA_ACTION) as? ContactAction? }

    private val recyclerAdapter: BaseRecyclerAdapter<IUser, BaseViewHolder<IUser>> by lazy {
        UserRecyclerAdapter()
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View {
//        binding = FragmentContactsBinding.inflate(inflater,container,false)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearch(binding.searchView)
        setupRecyclerView(binding.recyclerView)
        setupFab(binding.fab)

        updateContacts()

        addOnBackPressedListener {
            exit()
            false
        }

        setToolbar(binding.toolbar.apply { title = "" })
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                exit()
                true
            }
            else -> false
        }
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        binding.toolbar.setTopMargin(statusBarSize)
        binding.recyclerView.setBottomMargin(navigationBarSize)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_READ_CONTACT -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    updateContacts()
                } else {
                    CustomDialogBuilder(requireContext(), R.style.DialogTheme)
                        .setIcon(R.drawable.ic_error)
                        .setTitle(R.string.error)
                        .setMessage(R.string.perm_message_contacts)
                        .setPositiveButton(R.string.ok) { di, _, _ ->
                            di.dismiss()
                        }.setOnDismissListener {
                            exit()
                        }
                        .show()
                }
            }
        }
    }


    private fun onWriteMessageAction(viewHolder: BaseViewHolder<IUser>) {
        parentFragmentManager.replace(
            R.id.fragment_host_main, UserProfileFragment(),
            UserProfileFragment.newBundle(viewHolder.entity!!)
        )

    }

    private fun onGroupInviteAction(viewHolder: BaseViewHolder<IUser>) {
        if (action is GroupInviteAction) {

        }
    }

    private fun onChannelInviteAction(viewHolder: BaseViewHolder<IUser>) {

    }

    private fun setupSearch(searchView: SearchView) {
        with(searchView) {
            queryHint = getString(R.string.name_profile)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    isKeyboardVisible = false
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    recyclerAdapter.setVisible {
                        it.name.contains(newText.orEmpty(), true) ||
                                it.phone.contains(newText.orEmpty(), true)
                    }
                    return true
                }
            })
        }


    }

    private fun setupFab(fab: FloatingActionButton) {
        with(fab) {
            isVisible = action is GroupInviteAction
            setOnClickListener {
                with(action as GroupInviteAction) {
                    requireParentFragment().parentFragmentManager.apply {

                        lifecycleScope.launch {
                            viewModel.createGroup(group,recyclerAdapter.selectedEntities)
                        }

                        if (isNewGroup && backStackEntryCount > 1) {
                            popBackStack()
                            popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        with(recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerAdapter.apply {
                itemClickListener =
                    when (action) {
                        is WriteMessageAction -> this@ContactsFragment::onWriteMessageAction
                        is GroupInviteAction -> this@ContactsFragment::onGroupInviteAction
                        is ChannelInviteAction -> this@ContactsFragment::onChannelInviteAction
                        else -> {
                            {}
                        }
                    }
                if (action is GroupInviteAction) {
                    inSelectingMode = true
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun updateContacts() {

        val perms =
            arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)

        val hasPermission = perms.all { permissions.has(it) }

        when {
            hasPermission -> {



                viewModel.data.observe(viewLifecycleOwner, {
                    if (it.value != null) {
                        recyclerAdapter.set(it.value)
                    }
                    //binding.recyclerView.isVisible = it.value.isNotEmpty()
                    val hasFriends = it.value?.isNotEmpty() == true
                    binding.searchView.isVisible = hasFriends
                    binding.layoutError.isVisible = !hasFriends
                })
            }
            perms.any { permissions.shouldShowEducationalDialog(requireActivity(), it) } -> {

                var close = true

                CustomDialogBuilder(requireContext(), R.style.DialogTheme)
                    .setTitle(R.string.perm_request)
                    .setMessage(R.string.perm_request_contact)
                    .setIcon(R.drawable.ic_question)
                    .setPositiveButton(R.string.ok) { _, _, _ ->
                        close = false
                        permissions.request(this, PERMISSION_READ_CONTACT, *perms)
                    }
                    .setNegativeButton(R.string.cancel) { _, _, _ -> }.setOnDismissListener {
                        if (close) {
                            exit()
                        }
                    }
                    .show()
            }
            else -> {
                permissions.request(this, PERMISSION_READ_CONTACT, *perms)
            }
        }
    }

    private fun exit() {
        parentFragmentManager.popBackStack()
    }
}