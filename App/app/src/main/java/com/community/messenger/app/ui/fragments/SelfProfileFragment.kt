package com.community.messenger.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentProfileBinding
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.viewmodels.AuthViewModel
import com.community.messenger.app.ui.viewmodels.SelfProfileViewModel
import com.community.messenger.app.ui.views.setUser
import com.community.messenger.common.entities.imp.MediaContent
import com.community.messenger.common.entities.interfaces.IMediaContent


class SelfProfileFragment : MainActivity.EdgeToEdgeFragment<FragmentProfileBinding>() {
    companion object {
        const val REQ_PROFILE_PIC = 1488
    }

    private val viewModel : SelfProfileViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.data.observe(viewLifecycleOwner, Observer {
            it.value?.let { u -> binding.profile.setUser(u) }
        })

//        findViewById<ImageView>(R.id.profile_avatar_change).setOnClickListener {
//            Toast.makeText(context,"change",Toast.LENGTH_SHORT).show()
//        }
//        findViewById<TextView>(R.id.profile_description_text).apply {
////            movementMethod = ScrollingMovementMethod()
//        }

        binding.btnProfileEdit.setOnClickListener(this::onClickEditProfile)
        binding.btnExit.setOnClickListener(this::onClickExit)

        binding.profile.setOnAvatarChangeClickListener(this::onChangeAvatarClick)
        binding.profile.setOnAvatarClickListener(this::onAvatarClick)
    }

    private fun onChangeAvatarClick(v:View){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQ_PROFILE_PIC)
    }

    private fun onAvatarClick(v:View) {
        if (binding.profile.avatarUrl != null){
            requireParentFragment().parentFragmentManager.replace(
                    R.id.fragment_host_main,
                    FullscreenContentFragment(),
                    FullscreenContentFragment.newBundle(listOf(MediaContent(type = IMediaContent.IMAGE,
                        url = binding.profile.avatarUrl.toString())))
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_PROFILE_PIC -> {
                data?.data?.let {

                    viewModel.updateProfilePic(it)

                    viewModel.uploadingStatus.observe(viewLifecycleOwner, Observer {progress ->
                        binding.profile.uploadingProgress = progress.toFloat()
                    })
                    binding.profile.avatarUrl = it
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile_self,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun onClickEditProfile(view : View){
        val user =  viewModel.data.value?.value
 //       findNavController().navigate(R.id.action_bottom_navigation_to_edit_profile,EditProfileFragment.newBundle(user,false))
        requireParentFragment().parentFragmentManager
                .replace(R.id.fragment_host_main,EditProfileFragment(),
                EditProfileFragment.newBundle(user,false)){
                    setCustomAnimations(R.anim.anim_fragment_in,R.anim.anim_fragment_out,
                        R.anim.anim_fragment_in,R.anim.anim_fragment_out)
                }
    }

    override fun onDestroyView() {
        viewModel.data.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    private fun onClickExit(view : View) {
        val dialog = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
                .setView(R.layout.dialog_exit)
                .setPositiveButton(getString(R.string.yes)) { di, _ ->
                    val viewModel: AuthViewModel by viewModels()
                    viewModel.signOut()

                    requireParentFragment().parentFragmentManager.replace(
                            R.id.fragment_host_main, AuthFragment(),
                            AuthFragment.newBundle(false)) {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        disallowAddToBackStack()
                    }
                    di.dismiss()
                }
                .setNegativeButton(R.string.cancel) { di, _ ->
                    di.dismiss()
                }.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(dialog.context, R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(dialog.context, R.color.blue))
        }
        dialog.show()
    }
}