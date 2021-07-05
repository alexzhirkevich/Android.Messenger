package com.alexz.messenger.app.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexz.firerecadapter.BaseRecyclerAdapter
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.ui.activities.MainActivity
import com.alexz.messenger.app.ui.viewholders.UserViewHolder
import com.alexz.messenger.app.ui.viewmodels.WriteMessageViewModel
import com.alexz.messenger.app.ui.views.CustomDialogBuilder
import com.alexz.messenger.app.ui.views.setBottomMargin
import com.alexz.messenger.app.ui.views.setTopMargin
import com.alexz.messenger.app.util.permissions
import com.messenger.app.R

class WriteMessageFragment : MainActivity.EdgeToEdgeFragment(){

    companion object {
        private const val PERMISSION_READ_CONTACT = 1337
    }

    private val viewModel : WriteMessageViewModel by activityViewModels()

    private val toolbar : Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val recyclerView : RecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val searchView : SearchView by lazy { toolbar.findViewById<SearchView>(R.id.search) }
    private val layoutError : ViewGroup by lazy { findViewById<ViewGroup>(R.id.layout_error)}

    private val recyclerAdapter = object : BaseRecyclerAdapter<IUser,UserViewHolder>(){
        override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
            UserViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user,parent,false))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_write_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        searchView.apply {
            queryHint = getString(R.string.name)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerAdapter.apply {
                itemClickListener = this@WriteMessageFragment::onItemClick
            }
        }

        updateContacts()

        addOnBackPressedListener {
            exit()
            false
        }
    }

    override fun onResume() {
        super.onResume()
        setToolbar(toolbar.apply { title = "" })
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
        toolbar.setTopMargin(statusBarSize)
        recyclerView.setBottomMargin(navigationBarSize)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
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

    private fun onItemClick(viewHolder: UserViewHolder) {
        parentFragmentManager.replace(R.id.fragment_host_main,UserProfileFragment(),
                UserProfileFragment.newBundle(viewHolder.entity!!))
    }

    @SuppressLint("MissingPermission")
    private fun updateContacts() {

        val perms = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)


        val hasPermission = perms.all { permissions.has(it) }

        when {
            hasPermission -> {
                viewModel.init(requireContext().contentResolver)

                viewModel.data.observe(viewLifecycleOwner, Observer {
                    if (it.value != null) {
                        recyclerAdapter.set(it.value)
                        recyclerView.isVisible = it.value.isNotEmpty()
                        searchView.isVisible = it.value.isNotEmpty()
                        layoutError.isVisible = it.value.isEmpty()
                    }
                })
            }
            perms.any { permissions.shouldShowEducationalDiallog(requireActivity(),it) } -> {

                var close = true

                CustomDialogBuilder(requireContext(), R.style.DialogTheme)
                        .setTitle(R.string.perm_request)
                        .setMessage(R.string.perm_request_contact)
                        .setIcon(R.drawable.ic_question)
                        .setPositiveButton(R.string.ok) { _, _, _ ->
                            close = false
                            permissions.request(this, PERMISSION_READ_CONTACT, *perms)
                        }
                        .setNegativeButton(R.string.cancel){_,_,_ ->}.setOnDismissListener {
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

    override fun onDestroyView() {
        viewModel.data.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    private fun exit() {
        parentFragmentManager.popBackStack()
    }
}