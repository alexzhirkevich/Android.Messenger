package com.community.messenger.app.ui.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.views.enableWithEditTexts
import com.community.messenger.app.ui.views.setBottomMargin
import com.community.messenger.app.ui.views.setTopMargin
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.community.messenger.app.R

abstract class InputFragment<Binding : ViewBinding>()
    : MainActivity.EdgeToEdgeFragment<Binding>(){

    val toolbar : Toolbar by lazy { findViewById(R.id.toolbar) }
    val fab : FloatingActionButton by lazy { findViewById(R.id.fab) }

    val successColor : Int by lazy { ContextCompat.getColor(requireContext(),R.color.blue) }
    val errorColor : Int by lazy { ContextCompat.getColor(requireContext(),R.color.red) }
    val neutralColor : Int by lazy { ContextCompat.getColor(requireContext(),R.color.white) }

    open var isBtnBlocked = false

    open val isFabCanBeEnabled : Boolean
        get() = !isBtnBlocked

    abstract val inputs : Collection<EditText>

    private val fabAnimLen : Long by lazy { resources.getInteger(R.integer.anim_duration_short).toLong() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(toolbar)
        setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        fab.apply {
            setOnClickListener(this@InputFragment::onBtnClicked)
            enableWithEditTexts(R.color.white,R.color.gray,{isFabCanBeEnabled},*inputs.toTypedArray())
        }
        addOnBackPressedListener {
            it.remove()
            exit()
            false
        }
    }

    override fun onKeyboardVisibilityChanged(visible: Boolean) {
        fab.apply {
            if (visible) {
                (layoutParams as RelativeLayout.LayoutParams).apply {
                    removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    addRule(RelativeLayout.ALIGN_TOP, R.id.toolbar)
                }
                requestLayout()
                scaleX = 0f
                scaleY = 0f
                animate().scaleX(1f).scaleY(1f).setDuration(fabAnimLen).start()
            } else {
                (layoutParams as RelativeLayout.LayoutParams).apply {
                    removeRule(RelativeLayout.ALIGN_TOP)
                    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                }
                requestLayout()
            }
        }
    }

    open fun onBtnClicked(v : View) {

    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)
        toolbar.setTopMargin(statusBarSize)
        fab.setTopMargin(resources.getDimension(R.dimen.fab_margin).toInt() + statusBarSize)
        fab.setBottomMargin(resources.getDimension(R.dimen.fab_margin).toInt() + navigationBarSize)
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


    open fun exit(){
        try {
            com.community.messenger.common.util.KeyboardUtils.hideKeyboard(requireView())
        } finally {
            parentFragmentManager.popBackStack()
        }
    }

}