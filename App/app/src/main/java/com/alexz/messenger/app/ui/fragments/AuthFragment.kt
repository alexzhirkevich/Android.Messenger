package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.alexz.messenger.app.ui.activities.enableWithEditTexts
import com.alexz.messenger.app.ui.viewmodels.AuthViewModel
import com.alexz.messenger.app.util.KeyboardUtils.*
import com.alexz.test.OnSystemInsetsChangedListener
import com.alexz.test.RegisterViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.hbb20.CountryCodePicker
import com.messenger.app.R

class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private val logo: ImageView by lazy { requireView().findViewById<ImageView>(R.id.logo) }
    private val btnSendCode: Button by lazy {  requireView().findViewById<Button>(R.id.btn_send_code) }
    private val viewPager: ViewPager2 by lazy { requireView().findViewById<ViewPager2>(R.id.register_view_pager) }
    private val tabLayout: TabLayout by lazy{ requireView().findViewById<TabLayout>(R.id.register_tab_layout) }
    private val layoutInfo: ViewGroup by lazy { requireView().findViewById<ViewGroup>(R.id._info_layout) }
    private val layoutPhone: ViewGroup by lazy { requireView().findViewById<ViewGroup>(R.id._layout_phone) }
    private val layoutResend: ViewGroup by lazy { requireView().findViewById<ViewGroup>(R.id._layout_resend) }
    private val tvResend: TextView by lazy { requireView().findViewById<TextView>(R.id.tv_resend_btn) }
    private val editPhone: EditText by lazy { requireView().findViewById<EditText>(R.id.edit_phone) }
    private val editCode: EditText by lazy { requireView().findViewById<EditText>(R.id.edit_code) }
    private val progressBar: ProgressBar by lazy { requireView().findViewById<ProgressBar>(R.id.pb_login) }
    private val countryCodePicker: CountryCodePicker by lazy { requireView().findViewById<CountryCodePicker>(R.id.ccp) }

    private var inPhoneMode = false
    private val codeLen : Int by lazy { resources.getInteger(R.integer.verification_code_size) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.isAuthenticated) {
            toMainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Setup code picker with edit text
        countryCodePicker.registerCarrierNumberEditText(editPhone)

        // Info viewpager fade appear animation
        layoutInfo.apply {
            alpha = 0f
            animate().setDuration(300).setStartDelay(300).alpha(1f).start()
        }

        //Info viewpager binding
        viewPager.apply {
            adapter = RegisterViewPagerAdapter(context)
            TabLayoutMediator(tabLayout, this, TabLayoutMediator.TabConfigurationStrategy { _, _ -> }).attach()
        }

        //Set transparent status bar and bottom bar
        setEdgeToEdge(object : OnSystemInsetsChangedListener {
            override fun invoke(statusBarSize: Int, navigationBarSize: Int) {
                logo.apply {
                    (layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                            (resources.getDimension(R.dimen.register_logo_top_margin) + statusBarSize).toInt()
                    logo.requestLayout()
                }

                btnSendCode.apply {
                    (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                            (resources.getDimension(R.dimen.register_btn_bottom_margin) + navigationBarSize).toInt()
                    requestLayout()
                }
            }
        })

        tvResend.setOnClickListener {
            toPhoneMode()
        }

        btnSendCode.enableWithEditTexts(R.color.black, R.color.gray, { countryCodePicker.isValidFullNumber }, editPhone)

        //Observe login status
        viewModel.loginStatus.observe(viewLifecycleOwner, Observer {
            if (it.value != null) {
                toMainFragment()
            } else {
                val errorMsg = when (it.error) {
                    is FirebaseTooManyRequestsException ->
                        R.string.error_login_too_many_requests
                    is FirebaseAuthInvalidCredentialsException ->
                        if (inPhoneMode) {
                            R.string.error_login_invalid_phone
                        } else R.string.error_login_invalid_code
                    else -> R.string.error_login
                }
                Log.e("RegisterFragment", it.error.toString())
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                progressBar.isVisible = false

                if (errorMsg != R.string.error_login_invalid_code) {
                    toPhoneMode()
                }
                setBtnEnabled(true)
            }
        })

        //Observe code send status
        viewModel.codeSendStatus.observe(viewLifecycleOwner, Observer { isSend ->
            if (isSend) {
                toCodeMode()
            } else {
                toPhoneMode()
            }
        })
    }

    private fun setBtnEnabled(enabled : Boolean){
        btnSendCode.isEnabled = enabled
        btnSendCode.setTextColor(ContextCompat.getColor(
                btnSendCode.context,
                if (enabled) R.color.black else R.color.gray)
        )
    }

    private fun toMainFragment() = findNavController().navigate(R.id.action_auth_to_bottom_navigation)

    private fun toCodeMode() {
        if (inPhoneMode) {
            inPhoneMode = false
            layoutResend.isVisible = true
            progressBar.isVisible = false
            editPhone.apply {
                isEnabled = false
                clearFocus()
            }
            layoutPhone.animate()
                    .translationX(-resources.getDimension(R.dimen.auth_edits_translation))
                    .setDuration(300)
                    .withEndAction {
                       editCode.focus()
                    }
                    .start()
            editCode.animate().translationX(0f).setDuration(300).start()
            editCode.text.clear()

            btnSendCode.apply {
                text = getString(R.string.verify)
                enableWithEditTexts(R.color.black, R.color.gray, { editCode.text.length == codeLen }, editCode)
                setOnClickListener {
                    viewModel.verifyCode(editCode.text.trim().toString())
                    progressBar.isVisible = true
                    setBtnEnabled(false)
                }
            }
        }
    }

    private fun toPhoneMode() {
        if (!inPhoneMode) {
            inPhoneMode = true
            editCode.unFocus()
            editPhone.isEnabled = true
            layoutResend.isVisible = false
            progressBar.isVisible = false
            editCode.animate()
                    .translationX(resources.getDimension(R.dimen.auth_edits_translation))
                    .setDuration(300)
                    .withEndAction {
                        editCode.text.clear()
                    }
            layoutPhone.animate().translationX(0f).setDuration(300).start()
            btnSendCode.apply {
                text = getString(R.string.send_code)
                enableWithEditTexts(R.color.black, R.color.gray, {
                        countryCodePicker.isValidFullNumber
                    }, editPhone)
                setOnClickListener {
                    viewModel.sendCode(requireActivity(), countryCodePicker.fullNumberWithPlus)
                    progressBar.isVisible = true
                    setBtnEnabled(false)
                    editPhone.unFocus()
                }
            }
        }
    }
}

fun TextView.focus(){
    isEnabled = true
    requestFocus()
    if (!hasHardwareKeyboard(context)){
        showKeyboard(this)
    }
}

fun TextView.unFocus(){
    isEnabled = false
    clearFocus()
    if (!hasHardwareKeyboard(context)){
        hideKeyboard(this)
    }
}
