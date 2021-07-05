package com.alexz.messenger.app.ui.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.alexz.messenger.app.ui.activities.MainActivity
import com.alexz.messenger.app.ui.viewmodels.EditProfileViewModel
import com.alexz.messenger.app.ui.views.enableWithEditTexts
import com.alexz.messenger.app.ui.views.setTopMargin
import com.alexz.messenger.app.util.KeyboardUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.messenger.app.R
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers


class EditProfileFragment : MainActivity.EdgeToEdgeFragment() {

    companion object CREATOR {

        private const val EXTRA_NAME = "EXTRA_NAME"
        private const val EXTRA_USERNAME = "EXTRA_USERNAME"
        private const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        private const val EXTRA_NEW_USER = "EXTRA_NEW_USER"

        fun newBundle(name : String, userName : String, description : String, isNewUser : Boolean) = bundleOf(
                EXTRA_NAME to name,
                EXTRA_USERNAME to userName,
                EXTRA_DESCRIPTION to description,
                EXTRA_NEW_USER to isNewUser
        )
    }

    private val viewModel : EditProfileViewModel by viewModels()

    private val name: String by lazy { arguments?.getString(EXTRA_NAME).orEmpty() }
    private val username: String by lazy { arguments?.getString(EXTRA_USERNAME).orEmpty() }
    private val description: String by lazy { arguments?.getString(EXTRA_DESCRIPTION).orEmpty() }
    private val isNewUser: Boolean by lazy { arguments?.getBoolean(EXTRA_NEW_USER,false) ?:false }

    private val toolbar : Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val btnSave : FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.btn_profile_edit_save) }
    private val editName : EditText by lazy { findViewById<EditText>(R.id.et_profile_edit_name) }
    private val editUsername : EditText by lazy { findViewById<EditText>(R.id.et_profile_edit_username) }
    private val editDescription : EditText by lazy { findViewById<EditText>(R.id.et_profile_edit_description) }
    private val layoutUsername : TextInputLayout by lazy { findViewById<TextInputLayout>(R.id.layout_profile_edit_username) }
    private val layoutName : TextInputLayout by lazy { findViewById<TextInputLayout>(R.id.layout_profile_edit_name) }
    private val layoutDescription : TextInputLayout by lazy { findViewById<TextInputLayout>(R.id.layout_profile_edit_description) }
    private val scrollView : NestedScrollView by lazy { findViewById<NestedScrollView>(R.id.scroll_view) }

    private val colorRed : Int by lazy { ContextCompat.getColor(requireContext(),R.color.red) }
    private val colorGreen : Int by lazy { ContextCompat.getColor(requireContext(),R.color.green) }
    private val colorWhite : Int by lazy { ContextCompat.getColor(requireContext(),R.color.white) }
    private val colorGray : Int by lazy { ContextCompat.getColor(requireContext(),R.color.gray) }

    private val nameLenMin : Int by lazy { resources.getInteger(R.integer.profile_name_len_min) }
    private val usernameLenMin : Int by lazy { resources.getInteger(R.integer.profile_username_len_min) }
    private val fabAnimLen : Long by lazy { resources.getInteger(R.integer.anim_duration_short).toLong() }

    private var btnBlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(toolbar.apply { title = getString(R.string.profile) })
        if (!isNewUser){
            setHasOptionsMenu(true)
            setDisplayHomeAsUpEnabled(true)
        }


        setupEditName(editName)
        setupEditUsername(editUsername)
        setupEditDescription(editDescription)

        addOnBackPressedListener {
            exit()
            false
        }

        observeUsernameStatus(viewModel)

        setupButton(btnSave)
    }

    override fun onKeyboardVisibilityChanged(visible: Boolean) {
        if (visible) {
            (btnSave.layoutParams as RelativeLayout.LayoutParams).apply {
                removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                addRule(RelativeLayout.ALIGN_TOP, R.id.toolbar)
            }
            btnSave.requestLayout()
            btnSave.scaleX=0f
            btnSave.scaleY=0f
            btnSave.animate().scaleX(1f).scaleY(1f).setDuration(fabAnimLen).start()
        } else {
            (btnSave.layoutParams as RelativeLayout.LayoutParams).apply {
                removeRule(RelativeLayout.ALIGN_TOP)
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            btnSave.requestLayout()
        }
    }

    @SuppressLint("CheckResult")
    private fun onBtnClicked(v : View) {
        setBtnEnabled(false)
        editName.clearFocus()
        editUsername.clearFocus()
        editDescription.clearFocus()
        KeyboardUtils.hideKeyboard(btnSave)
        Completable.concatArray(
                viewModel.setName(editName.text.trim().toString()),
                viewModel.setUsername(editUsername.text.trim().toString()),
                viewModel.setDescription(editDescription.text.trim().toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                        },
                        { onError(it) }
                )
        exit()
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        toolbar.setTopMargin(statusBarSize)

//        scrollView.apply {
//            (layoutParams as ViewGroup.MarginLayoutParams).topMargin = navigationBarSize
//            requestLayout()
//        }

        btnSave.setTopMargin(
                resources.getDimension(R.dimen.fab_margin).toInt() + navigationBarSize)

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

    private fun setupEditName(editName : TextView){
        editName.apply {
            doOnTextChanged { _, _, _, _ ->
                when {
                    text.length < nameLenMin -> {
                        error = getString(R.string.eror_min_len,nameLenMin)
                        layoutName.boxStrokeColor = colorRed
                        layoutName.setStartIconTintList(ColorStateList.valueOf(colorRed))
                    }
                    text.any { !it.isLetter() && it != ' ' } -> {
                        error = getString(R.string.error_name_text)
                        layoutName.boxStrokeColor = colorRed
                        layoutName.setStartIconTintList(ColorStateList.valueOf(colorRed))
                    }
                    else -> {
                        layoutName.boxStrokeColor = colorWhite
                        layoutName.setStartIconTintList(ColorStateList.valueOf(colorGreen))
                    }
                }
            }
            text = name
        }
    }

    private fun setupEditUsername(editUsername : EditText){
        editUsername.apply {
            doOnTextChanged { _, _, _, _ ->
                when {
                    text.any { it !in 'a'..'z' union 'A'..'Z' union '0'..'9' && it != '_' } -> {
                        error = getString(R.string.error_username_text)
                        layoutUsername.boxStrokeColor = colorRed
                        layoutUsername.setStartIconTintList(ColorStateList.valueOf(colorRed))
                    }
                    text.length < usernameLenMin && text.isNotEmpty() -> {
                        error = getString(R.string.eror_min_len, usernameLenMin)
                        layoutUsername.boxStrokeColor = colorRed
                        layoutUsername.setStartIconTintList(ColorStateList.valueOf(colorRed))
                    }
                    text.isEmpty() -> {
                        layoutUsername.boxStrokeColor = colorGreen
                        layoutUsername.setStartIconTintList(ColorStateList.valueOf(colorGreen))
                    }
                    else -> {
                        btnBlocked = true
                        viewModel.checkUsername { text.toString() }
                        layoutUsername.boxStrokeColor = colorWhite
                        layoutUsername.setStartIconTintList(ColorStateList.valueOf(colorGray))
                    }
                }
            }
            setText(username)
        }
    }

    private fun setupEditDescription(editDescription: EditText) {
        editDescription.apply {
            doOnTextChanged { _, _, _, _ ->
                if(System.lineSeparator() in text){
                    error = getString(R.string.error_description_text)
                    layoutDescription.boxStrokeColor = colorRed
                    layoutDescription.setStartIconTintList(ColorStateList.valueOf(colorRed))
                } else {
                    layoutDescription.boxStrokeColor = colorWhite
                    layoutDescription.setStartIconTintList(ColorStateList.valueOf(colorGreen))
                }
            }
            setText(description)
        }
    }

    private fun setupButton(btnSave: FloatingActionButton) {
        btnSave.apply {
            enableWithEditTexts(R.color.black, R.color.gray, {
                isButtonCanBeEnabled
            }, editName,editUsername,editDescription)
            setOnClickListener(this@EditProfileFragment::onBtnClicked)
        }
    }

    private val isButtonCanBeEnabled : Boolean
    get() = editName.text.length >= nameLenMin &&
            editName.text.all { it.isLetter() || it == ' ' } &&
            ((editUsername.text.all { it in 'a'..'z' union 'A'..'Z' union '0'..'9' || it == '_' } &&
                    editUsername.text.length >= usernameLenMin ) || editUsername.text.isEmpty()) &&
            System.lineSeparator() !in editDescription.text && !btnBlocked &&
            viewModel.userNameAvailable.value == true

    private fun observeUsernameStatus(viewModel: EditProfileViewModel) {
        viewModel.userNameAvailable.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            btnBlocked = false
            if (editUsername.error == null ||
                    editUsername.error == getString(R.string.error_username_taken)) {
                if (!it) {
                    layoutUsername.boxStrokeColor = colorRed
                    layoutUsername.setStartIconTintList(ColorStateList.valueOf(colorRed))
                    editUsername.error = getString(R.string.error_username_taken)
                } else {
                    layoutUsername.boxStrokeColor = colorGreen
                    layoutUsername.setStartIconTintList(ColorStateList.valueOf(colorGreen))
                    editUsername.error = null
                    setBtnEnabled(isButtonCanBeEnabled)
                }
            }
        })
    }

    private fun setBtnEnabled(enabled : Boolean){
        btnSave.isEnabled = enabled
        btnSave.setBackgroundResource(if (enabled) R.color.black else R.color.gray)
    }


    private fun onError(t : Throwable){
        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
        setBtnEnabled(isButtonCanBeEnabled)
    }

    private fun exit() {
        parentFragmentManager.apply {
            if (backStackEntryCount>0)
                popBackStack()
            else {
                //(requireActivity().application as ChatApplication).updateUserListener()
                replace(R.id.fragment_host_main, BottomNavigationFragment(), BottomNavigationFragment.newBundle(R.id.fragment_profile)) {
                    disallowAddToBackStack()
                }
            }
        } }
}