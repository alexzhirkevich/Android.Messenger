package com.community.messenger.app.ui.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.viewmodels.UserProfileViewModel
import com.community.messenger.app.ui.views.ProfileView
import com.community.messenger.app.ui.views.setTopMargin
import com.community.messenger.app.ui.views.setUser
import com.community.messenger.common.entities.imp.MediaContent
import com.community.messenger.common.entities.interfaces.IMediaContent
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentUserProfileBinding

class UserProfileFragment
    : MainActivity.EdgeToEdgeFragment<FragmentUserProfileBinding>() {

    companion object CREATOR{

        const val EXTRA_USER = "EXTRA_USER"

        fun newBundle(user : IUser) : Bundle{
            return bundleOf().apply { putParcelable(EXTRA_USER,user) }
        }
    }

    private val viewModel : UserProfileViewModel by viewModels()

    private val user : IUser by lazy { arguments?.getParcelable<IUser>(EXTRA_USER)!! }

    private val profileView : ProfileView by lazy { findViewById<ProfileView>(R.id.profile) }
    private val toolbar : Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileView.setUser(user)

        viewModel.apply {
            init(user.id)
            data.observe(viewLifecycleOwner, Observer {
                if (it.value != null) {
                    profileView.setUser(it.value)
                }
            })
        }

        addOnBackPressedListener {
            parentFragmentManager.popBackStackImmediate()
            false
        }

        profileView.setOnAvatarClickListener {
            if (user.imageUri.isNotEmpty()) {
                parentFragmentManager.replace(R.id.fragment_host_main, FullscreenContentFragment(),
                        FullscreenContentFragment.newBundle(listOf(MediaContent(id = user.id, type = IMediaContent.IMAGE, url = user.imageUri))))
            }
        }
        profileView.isSelf = false
    }

    override fun onResume() {
        super.onResume()
        setToolbar(toolbar.apply {
            title = if (user.username.isNotEmpty()) "@" + user.username else user.name
        })
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->
                parentFragmentManager.popBackStackImmediate()
        }
        return false
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        toolbar.setTopMargin(statusBarSize)
    }
}