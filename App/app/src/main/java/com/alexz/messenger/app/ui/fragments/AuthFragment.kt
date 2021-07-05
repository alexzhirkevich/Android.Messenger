package com.alexz.messenger.app.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.alexz.messenger.app.ui.activities.MainActivity
import com.alexz.messenger.app.ui.viewmodels.*
import com.alexz.messenger.app.ui.views.enableWithEditTexts
import com.alexz.messenger.app.ui.views.focus
import com.alexz.messenger.app.ui.views.setBottomMargin
import com.alexz.messenger.app.ui.views.unFocus
import com.alexz.messenger.app.util.MetrixUtil
import com.alexz.messenger.app.util.permissions
import com.alexz.test.RegisterViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.hbb20.CountryCodePicker
import com.messenger.app.R


class AuthFragment : MainActivity.EdgeToEdgeFragment() {

    companion object {
        private const val TAG : String = "AuthFragment"
        private const val EXTRA_RUN_ANIMATION = "EXTRA_RUN_ANIMATION"

        fun newBundle(withAnimation : Boolean) =
                bundleOf(EXTRA_RUN_ANIMATION to withAnimation)

    }

    private val withAnimation : Boolean by lazy { arguments?.getBoolean(EXTRA_RUN_ANIMATION,true) ?: true }

    private val viewModel: AuthViewModel by viewModels()

    private val scrollView : NestedScrollView by lazy { findViewById<NestedScrollView>(R.id.scrollview_login)  }
    private val logo: ImageView by lazy { findViewById<ImageView>(R.id.logo) }
    private val btnSendCode: Button by lazy {  findViewById<Button>(R.id.btn_send_code) }
    private val viewPager: ViewPager2 by lazy { findViewById<ViewPager2>(R.id.register_view_pager) }
    private val tabLayout: TabLayout by lazy{ findViewById<TabLayout>(R.id.register_tab_layout) }
    private val layoutInfo: ViewGroup by lazy { findViewById<ViewGroup>(R.id._info_layout) }
    private val layoutPhone: ViewGroup by lazy { findViewById<ViewGroup>(R.id._layout_phone) }
    private val layoutResend: ViewGroup by lazy { findViewById<ViewGroup>(R.id._layout_resend) }
    private val tvResend: TextView by lazy { findViewById<TextView>(R.id.tv_resend_btn) }
    private val editPhone: EditText by lazy { findViewById<EditText>(R.id.edit_phone) }
    private val editCode: EditText by lazy { findViewById<EditText>(R.id.edit_code) }
    private val progressBar: ProgressBar by lazy { findViewById<ProgressBar>(R.id.pb_login) }
    private val countryCodePicker: CountryCodePicker by lazy { findViewById<CountryCodePicker>(R.id.ccp) }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        countryCodePicker.registerCarrierNumberEditText(editPhone)

        viewPager.apply {
            adapter = RegisterViewPagerAdapter(context)
            TabLayoutMediator(tabLayout, this, TabLayoutMediator.TabConfigurationStrategy { _, _ -> }).attach()
        }

        btnSendCode.setOnClickListener(this::onBtnClick)
        tvResend.setOnClickListener { toPhoneMode() }

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
        scrollView.scrollTo(0, scrollView.bottom)
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        statusBarHeight = statusBarSize
        layoutResend.setBottomMargin(
                resources.getDimension(R.dimen.default_bottom_margin).toInt() + navigationBarSize)
    }

    private fun runAnimation() {
        layoutInfo.alpha = 0f
        layoutPhone.alpha = 0f
        btnSendCode.alpha = 0f

        editPhone.isFocusable = false

        val longAnimDuration = resources.getInteger(R.integer.anim_duration_long).toLong()
        val mediumAnimDuration = resources.getInteger(R.integer.anim_duration_medium).toLong()

        logo.y = (resources.displayMetrics.heightPixels -
                resources.getDimension(R.dimen.reg_logo_size))/2-
                resources.getDimension(R.dimen.register_logo_top_margin)+
                statusBarHeight + MetrixUtil.dpToPx(context,25)

        
        logo.animate().setDuration(longAnimDuration).setStartDelay(longAnimDuration).translationY(0f)
                .setInterpolator(DecelerateInterpolator(1.2f))
                .withEndAction {
                    layoutInfo.animate() .alpha(1f).setDuration(longAnimDuration)
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

    private fun onBtnClick(view : View) {
        setBtnEnabled(false)
        progressBar.isVisible = true
        if (inPhoneMode) {
            editPhone.unFocus()
            viewModel.sendCode(requireActivity(), countryCodePicker.fullNumberWithPlus)
        }  else {
            editCode.unFocus()
            viewModel.verifyCode(editCode.text.trim().toString())
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
        progressBar.isVisible = false

        if (errorMsg != R.string.error_login_invalid_code) {
            toPhoneMode()
        } else{
            editCode.focus()
        }
        setBtnEnabled(true)
        editPhone.isEnabled = true
    }

    private fun setBtnEnabled(enabled : Boolean){
        btnSendCode.isEnabled = enabled
        btnSendCode.setTextColor(ContextCompat.getColor(
                btnSendCode.context,
                if (enabled) R.color.black else R.color.gray)
        )
    }

    private fun toMainFragment(isNewUser : Boolean) {

        initSharedViewModels()

        if (isNewUser) {
            parentFragmentManager.replace(R.id.fragment_host_main,EditProfileFragment(),
                    EditProfileFragment.newBundle("","","",true)) {
                remove(this@AuthFragment)
                disallowAddToBackStack()
            }

        } else{
            parentFragmentManager.replace(R.id.fragment_host_main,BottomNavigationFragment()) {
                remove(this@AuthFragment)
                disallowAddToBackStack()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initSharedViewModels(){
        val chatsViewModel : ChatsViewModel by activityViewModels()
        chatsViewModel.update()

        val channelsViewModel : ChannelsViewModel by activityViewModels()
        channelsViewModel.update()

        val eventViewHolder : EventsViewModel by activityViewModels()
        eventViewHolder.update()

        val selfProfileViewModel : SelfProfileViewModel by activityViewModels()
        selfProfileViewModel.update()


        val writeMessageViewModel : WriteMessageViewModel by activityViewModels()
        if (permissions.has(android.Manifest.permission.READ_CONTACTS))
            writeMessageViewModel.init(requireContext().contentResolver)
    }

    private fun toCodeMode() {
        if (inPhoneMode) {
            inPhoneMode = false
            editCode.isVisible = true
            layoutResend.visibility = View.VISIBLE
            progressBar.isVisible = false
            editPhone.unFocus()
            layoutPhone.animate().translationX(-resources.getDimension(R.dimen.auth_edits_translation)).setDuration(tvAnimDuration)
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
                enableWithEditTexts(R.color.black, R.color.gray,
                        { editCode.text.length == codeLen }, editCode)
            }
        }
    }

    private fun toPhoneMode() {
        if (!inPhoneMode) {
            inPhoneMode = true
            editCode.unFocus()
            editPhone.isEnabled = true
            layoutResend.visibility = View.INVISIBLE
            progressBar.isVisible = false
            editCode.animate().translationX(resources.getDimension(R.dimen.auth_edits_translation)).setDuration(tvAnimDuration)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
            layoutPhone.animate().translationX(0f).setDuration(tvAnimDuration)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
            btnSendCode.apply {
                text = getString(R.string.send_code)
                enableWithEditTexts(R.color.black, R.color.gray,
                        { countryCodePicker.isValidFullNumber }, editPhone)
            }
        }
    }
}


