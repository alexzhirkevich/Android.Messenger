package com.community.messenger.app.ui.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentEditProfileBinding
import com.community.messenger.app.ui.viewmodels.EditProfileViewModel
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.common.util.invoke
import com.community.messenger.common.util.isLatinDigitOrUnderscore


class EditProfileFragment : InputFragment<FragmentEditProfileBinding>() {

    companion object CREATOR {

        private const val EXTRA_USER = "EXTRA_NAME"
        private const val EXTRA_ISNEW = "EXTRA_NEW_USER"

        fun newBundle(user : IUser?,isNew : Boolean) = bundleOf(
                EXTRA_USER to user,
                EXTRA_ISNEW to isNew
        )
    }

    private val viewModel : EditProfileViewModel by viewModels()

    private val user: IUser? by lazy { arguments?.getParcelable(EXTRA_USER) }
    private val isNewUser: Boolean by lazy { arguments?.getBoolean(EXTRA_ISNEW,false) ?:false }

    private val nameLenMin : Int by lazy { resources.getInteger(R.integer.profile_name_len_min) }
    private val usernameLenMin : Int by lazy { resources.getInteger(R.integer.profile_username_len_min) }
    private val fabAnimLen : Long by lazy { resources.getInteger(R.integer.anim_duration_short).toLong() }


    override val inputs: Collection<EditText>
        get() = listOf(binding.editName,binding.editUsername,binding.editDescription)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setToolbar(toolbar)
        setHasOptionsMenu(!isNewUser)
        setDisplayHomeAsUpEnabled(!isNewUser)

        with(binding) {
            setupEditName(editName)
            setupEditUsername(editUsername)
            setupEditDescription(editDescription)
        }

        observeUsernameStatus(viewModel)
    }

    @SuppressLint("CheckResult")
    override fun onBtnClicked(v : View) {
        with(binding){
            fab.isEnabled = isFabCanBeEnabled
            editName.clearFocus()
            editUsername.clearFocus()
            editDescription.clearFocus()
            com.community.messenger.common.util.KeyboardUtils.hideKeyboard(fab)
            viewModel.setName(editName.text?.trim().toString()).invoke()
            viewModel.setUsername(editUsername.text?.trim().toString()).invoke()
            viewModel.setDescription(editDescription.text?.trim().toString()).invoke()
        }
        exit()
    }

    private fun setupEditName(editName : EditText){
        editName.apply {
            doOnTextChanged { _, _, _, _ ->
                with(binding) {
                    when {
                        text.isNotEmpty() && text.length < nameLenMin -> {
                            error = getString(R.string.eror_min_len, nameLenMin)
                            layoutName.boxStrokeColor = errorColor
                            layoutName.setStartIconTintList(ColorStateList.valueOf(errorColor))
                        }
                        text.any { !it.isLetter() && it != ' ' } -> {
                            error = getString(R.string.error_name_text)
                            layoutName.boxStrokeColor = errorColor
                            layoutName.setStartIconTintList(ColorStateList.valueOf(errorColor))
                        }
                        else -> {
                            layoutName.boxStrokeColor = successColor
                            layoutName.setStartIconTintList(ColorStateList.valueOf(successColor))
                        }
                    }
                }
            }
            setText(user?.name)
        }
    }

    private fun setupEditUsername(editUsername : EditText) {
        editUsername.apply {
            doOnTextChanged { _, _, _, _ ->
                with(binding) {
                    when {
                        text.any { it !in 'a'..'z' union 'A'..'Z' union '0'..'9' && it != '_' } -> {
                            error = getString(R.string.error_username_text)
                            layoutUsername.boxStrokeColor = errorColor
                            layoutUsername.setStartIconTintList(ColorStateList.valueOf(errorColor))
                        }
                        text.length < usernameLenMin && text.isNotEmpty() -> {
                            error = getString(R.string.eror_min_len, usernameLenMin)
                            layoutUsername.boxStrokeColor = errorColor
                            layoutUsername.setStartIconTintList(ColorStateList.valueOf(errorColor))
                        }
                        text.isEmpty() -> {
                            layoutUsername.boxStrokeColor = successColor
                            layoutUsername.setStartIconTintList(ColorStateList.valueOf(successColor))
                        }
                        else -> {
                            isBtnBlocked = true
                            viewModel.checkUsername { text.toString() }
                            layoutUsername.boxStrokeColor = neutralColor
                            layoutUsername.setStartIconTintList(ColorStateList.valueOf(neutralColor))
                        }
                    }
                }
            }
            setText(user?.username)
        }
    }

    private fun setupEditDescription(editDescription: EditText) {
        editDescription.apply {
            doOnTextChanged { _, _, _, _ ->
                with(binding) {
                    if (text.count { it.toString() == System.lineSeparator() } > 1) {
                        error = getString(R.string.error_description_text)
                        layoutDescription.boxStrokeColor = errorColor
                        layoutDescription.setStartIconTintList(ColorStateList.valueOf(errorColor))
                    } else {
                        layoutDescription.boxStrokeColor = successColor
                        layoutDescription.setStartIconTintList(ColorStateList.valueOf(successColor))
                    }
                }
            }
            setText(user?.description)
        }
    }

    override val isFabCanBeEnabled : Boolean
        get() = with(binding) {
            editName.text?.length ?: 0 >= nameLenMin &&
                    editName.text?.all { it.isLetter() || it == ' ' }==true &&
                    ((editUsername.text.toString().isLatinDigitOrUnderscore &&
                            editUsername.text?.length?:0 >= usernameLenMin) || editUsername.text?.isEmpty()==true) &&
                    editDescription.text?.count { it.toString() == System.lineSeparator() }?:0 <= 1 && !isBtnBlocked &&
                    viewModel.userNameAvailable.value == true
        }

    private fun observeUsernameStatus(viewModel: EditProfileViewModel) {
        viewModel.userNameAvailable.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            isBtnBlocked = false
            with(binding) {
                if (editUsername.error == null ||
                    editUsername.error == getString(R.string.error_username_taken)
                ) {
                    if (!it) {
                        layoutUsername.boxStrokeColor = errorColor
                        layoutUsername.setStartIconTintList(ColorStateList.valueOf(errorColor))
                        editUsername.error = getString(R.string.error_username_taken)
                    } else {
                        layoutUsername.boxStrokeColor = successColor
                        layoutUsername.setStartIconTintList(ColorStateList.valueOf(successColor))
                        editUsername.error = null
                        fab.isEnabled = isFabCanBeEnabled
                    }
                }
            }
        })
    }


    private fun onError(t : Throwable){
        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
        fab.isEnabled = isFabCanBeEnabled    }

    override fun exit() {
        com.community.messenger.common.util.KeyboardUtils.hideKeyboard(requireView())
        findNavController().popBackStack()
        parentFragmentManager.apply {
            if (backStackEntryCount>0)
                popBackStack()
            else {
                replace(R.id.fragment_host_main, BottomNavigationFragment(), BottomNavigationFragment.newBundle(R.id.fragment_profile)) {
                    disallowAddToBackStack()
                    remove(this@EditProfileFragment)
                }
            }
        }
    }
}