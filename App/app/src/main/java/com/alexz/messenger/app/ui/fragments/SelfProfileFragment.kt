package com.alexz.messenger.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.alexz.messenger.app.data.entities.imp.MediaContent
import com.alexz.messenger.app.data.entities.interfaces.IMediaContent
import com.alexz.messenger.app.ui.viewmodels.AuthViewModel
import com.alexz.messenger.app.ui.viewmodels.SelfProfileViewModel
import com.alexz.messenger.app.ui.views.ProfileView
import com.alexz.messenger.app.ui.views.setUser
import com.messenger.app.R


class SelfProfileFragment : Fragment() {

    private val profileView : ProfileView by lazy { findViewById<ProfileView>(R.id.layout_profile) }
    private val viewModel : SelfProfileViewModel by activityViewModels()

    companion object {
        const val REQ_PROFILE_PIC = 1488
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.data.observe(viewLifecycleOwner, Observer {
            it.value?.let { u -> profileView.setUser(u) }
        })

//        findViewById<ImageView>(R.id.profile_avatar_change).setOnClickListener {
//            Toast.makeText(context,"change",Toast.LENGTH_SHORT).show()
//        }
//        findViewById<TextView>(R.id.profile_description_text).apply {
////            movementMethod = ScrollingMovementMethod()
//        }

        findViewById<Button>(R.id.btn_profile_edit).setOnClickListener(this::onClickEditProfile)
        findViewById<Button>(R.id.exit).setOnClickListener(this::onClickExit)

        profileView.setOnAvatarChangeClickListener(this::onChangeAvatarClick)
        profileView.setOnAvatarClickListener(this::onAvatarClick)
    }

    private fun onChangeAvatarClick(v:View){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQ_PROFILE_PIC)
    }

    private fun onAvatarClick(v:View) {
        if (profileView.avatarUrl != null){
            requireParentFragment().parentFragmentManager.replace(
                    R.id.fragment_host_main,
                    FullscreenContentFragment(),
                    FullscreenContentFragment.newBundle(listOf(MediaContent(type = IMediaContent.IMAGE,url = profileView.avatarUrl.toString())))
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
                        profileView.uploadingProgress = progress.toFloat()
                    })
                    profileView.avatarUrl = it

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
        val name = user?.name.orEmpty()
        val username = user?.username.orEmpty()
        val description  = user?.description.orEmpty()
        requireParentFragment().parentFragmentManager
                .replace(
                R.id.fragment_host_main,EditProfileFragment(),
                EditProfileFragment.newBundle(name,username,description,false)){
                    addToBackStack(EditProfileFragment::class.java.name)
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