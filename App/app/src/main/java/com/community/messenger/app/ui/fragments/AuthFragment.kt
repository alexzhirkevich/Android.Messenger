package com.community.messenger.app.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.community.messenger.app.R
import com.community.messenger.app.data.settings
import com.community.messenger.app.databinding.FragmentAuthBinding
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.viewmodels.*
import com.community.messenger.app.ui.views.enableWithEditTexts
import com.community.messenger.app.ui.views.focus
import com.community.messenger.app.ui.views.setBottomMargin
import com.community.messenger.app.ui.views.unFocus
import com.community.messenger.common.util.MetrixUtil
import com.community.messenger.common.util.permissions
import com.community.test.RegisterViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException


class AuthFragment : MainActivity.EdgeToEdgeFragment<FragmentAuthBinding>() {

    companion object{
        private const val TAG : String = "AuthFragment"
        private const val EXTRA_RUN_ANIMATION = "EXTRA_RUN_ANIMATION"

        fun newBundle(withAnimation : Boolean) =
                bundleOf(EXTRA_RUN_ANIMATION to withAnimation)
    }

   // private lateinit var binding : FragmentAuthBinding

    private val withAnimation : Boolean by lazy { arguments?.getBoolean(EXTRA_RUN_ANIMATION,true) ?: true }

    private val viewModel: AuthViewModel by viewModels()


    private var inPhoneMode = false
    private val codeLen : Int by lazy { resources.getInteger(R.integer.verification_code_size) }
    private val tvAnimDuration : Long by lazy { resources.getInteger(R.integer.anim_duration_medium).toLong()}
    private var statusBarHeight = 0

    private var leaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.isAuthenticated) {
            leaved = true
            toMainFragment(false)

        }
        else {
            if (withAnimation) {
                (activity as MainActivity).isFullscreen = true
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.countryCodePicker.registerCarrierNumberEditText(binding.editPhone)

        binding.viewPager.apply {
            adapter = RegisterViewPagerAdapter(context)
            TabLayoutMediator(binding.tabLayout, this, TabLayoutMediator.TabConfigurationStrategy { _, _ -> }).attach()
        }

        binding.btnSendCode.setOnClickListener(this::onBtnClick)
        binding.tvResend.setOnClickListener { toPhoneMode() }

        viewModel.loginStatus.observe(viewLifecycleOwner, Observer {

            if (it.value != null && !leaved){
                toMainFragment(it.value.isNew)
            }
            if (it.error != null)
                processLoginError(it.error)
        })

        viewModel.codeSendStatus.observe(viewLifecycleOwner, Observer { isSend ->
            if (isSend)
                toCodeMode()
            else
                toPhoneMode()
        })

        if (withAnimation) {
            runAnimation()
        }
    }

    override fun onKeyboardVisibilityChanged(visible: Boolean) {
        super.onKeyboardVisibilityChanged(visible)
        binding.scrollView.scrollTo(0, binding.scrollView.bottom)
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        statusBarHeight = statusBarSize
        binding.layoutResend.setBottomMargin(
                resources.getDimension(R.dimen.default_bottom_margin).toInt() + navigationBarSize)
    }

    private fun runAnimation() {
        with(binding) {
            layoutInfo.alpha = 0f
            layoutPhone.alpha = 0f
            btnSendCode.alpha = 0f

            editPhone.isFocusable = false

            val longAnimDuration = resources.getInteger(R.integer.anim_duration_long).toLong()
            val mediumAnimDuration = resources.getInteger(R.integer.anim_duration_medium).toLong()

            logo.y = (resources.displayMetrics.heightPixels -
                    resources.getDimension(R.dimen.reg_logo_size)) / 2 -
                    resources.getDimension(R.dimen.register_logo_top_margin) +
                    statusBarHeight + MetrixUtil.dpToPx(context, 25)


            logo.animate().setDuration(longAnimDuration).setStartDelay(longAnimDuration)
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator(1.2f))
                .withEndAction {
                    layoutInfo.animate().alpha(1f).setDuration(longAnimDuration)
                        .setStartDelay(0).start()
                    btnSendCode.animate().alpha(1f).setDuration(longAnimDuration)
                        .setStartDelay(mediumAnimDuration).start()
                    layoutPhone.animate().setDuration(longAnimDuration).alpha(1f)
                        .withEndAction {
                            editPhone.isFocusableInTouchMode = true
                            (activity as MainActivity).isFullscreen = false
                        }.setStartDelay(mediumAnimDuration).start()
                }.start()
        }
    }

    private fun onBtnClick(view : View) {
        setBtnEnabled(false)
        with(binding) {
            progressBar.isVisible = true
            if (inPhoneMode) {
                editPhone.unFocus()
                viewModel.sendCode(requireActivity(), countryCodePicker.fullNumberWithPlus)
            } else {
                editCode.unFocus()
                viewModel.verifyCode(editCode.text.trim().toString())
            }
        }
    }

    private fun processLoginError(t : Throwable){
        val errorMsg = when (t) {
            is FirebaseTooManyRequestsException ->
                R.string.error_login_too_many_requests
            is FirebaseAuthInvalidCredentialsException ->
                if (inPhoneMode) {
                    R.string.error_login_invalid_phone
                } else R.string.error_login_invalid_code
            else -> R.string.error_login
        }
        Log.e(TAG, t.toString())
        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        binding.progressBar.isVisible = false

        if (errorMsg != R.string.error_login_invalid_code) {
            toPhoneMode()
        } else{
            binding.editCode.focus()
        }
        setBtnEnabled(true)
        binding.editPhone.isEnabled = true
    }

    private fun setBtnEnabled(enabled : Boolean){
        with(binding) {
            btnSendCode.isEnabled = enabled
            btnSendCode.setTextColor(
                ContextCompat.getColor(
                    btnSendCode.context,
                    if (enabled) R.color.black else R.color.gray
                )
            )
        }
    }

    private fun toMainFragment(isNewUser : Boolean) {

        initViewModels()
        isKeyboardVisible = false

        if (isNewUser) {
  //          findNavController().navigate(R.id.action_auth_to_edit_profile,EditProfileFragment.newBundle(null,true))
            parentFragmentManager.replace(
                    R.id.fragment_host_main,EditProfileFragment(),
                    EditProfileFragment.newBundle(null,true)) {
                //remove(this@AuthFragment)
                setCustomAnimations(R.anim.anim_fragment_in,R.anim.anim_fragment_out,
                    R.anim.anim_fragment_in,R.anim.anim_fragment_out)
                disallowAddToBackStack()
            }

        } else{
 //          findNavController().navigate(R.id.action_auth_to_bottom_navigation)
            parentFragmentManager.replace(R.id.fragment_host_main,BottomNavigationFragment()) {
               // remove(this@AuthFragment)
                disallowAddToBackStack()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initViewModels(){
        val chatsViewModel : ChatsViewModel by activityViewModels()
        chatsViewModel

        val channelsViewModel : ChannelsViewModel by activityViewModels()
        channelsViewModel

        val eventViewModel : EventsViewModel by activityViewModels()
        eventViewModel

        val selfProfileViewModel : SelfProfileViewModel by activityViewModels()
        selfProfileViewModel

        if (permissions.has(android.Manifest.permission.READ_CONTACTS)) {
            val contactsViewModel: ContactsViewModel by activityViewModels {
                ContactsViewModelFactory(requireContext().contentResolver,settings)
            }
            contactsViewModel
        }
    }

    private fun toCodeMode() {
        if (inPhoneMode) {

            with(binding) {
                inPhoneMode = false
                editCode.isVisible = true
                layoutResend.visibility = View.VISIBLE
                progressBar.isVisible = false
                editPhone.unFocus()
                layoutPhone.animate()
                    .translationX(-resources.getDimension(R.dimen.auth_edits_translation))
                    .setDuration(tvAnimDuration)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .setStartDelay(0).withEndAction {
                        editCode.focus()
                    }.start()
                editCode.apply {
                    text.clear()
                    animate().translationX(0f).setDuration(tvAnimDuration)
                        .setStartDelay(0).setInterpolator(AccelerateDecelerateInterpolator())
                        .start()
                }

                btnSendCode.apply {
                    text = getString(R.string.verify)
                    enableWithEditTexts(
                        R.color.black, R.color.gray,
                        { editCode.text.length == codeLen }, editCode
                    )
                }
            }
        }
    }

    private fun toPhoneMode() {
        if (!inPhoneMode) {
            with(binding) {
                inPhoneMode = true
                editCode.unFocus()
                editPhone.isEnabled = true
                layoutResend.visibility = View.INVISIBLE
                progressBar.isVisible = false
                editCode.animate()
                    .translationX(resources.getDimension(R.dimen.auth_edits_translation))
                    .setDuration(tvAnimDuration)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
                layoutPhone.animate().translationX(0f).setDuration(tvAnimDuration)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
                btnSendCode.apply {
                    text = getString(R.string.send_code)
                    enableWithEditTexts(R.color.black, R.color.gray,
                        { countryCodePicker.isValidFullNumber }, editPhone
                    )
                }
            }
        }
    }
}


