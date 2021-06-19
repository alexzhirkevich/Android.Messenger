package com.alexz.messenger.app.ui.fragments

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.alexz.test.OnSystemInsetsChangedListener
import com.alexz.test.setWindowTransparency

fun FragmentManager.replace(containerId: Int, fragment: Fragment, tag: String) {
    var current = findFragmentByTag(tag)

    beginTransaction()
            .apply {
                //Hide the current fragment
                primaryNavigationFragment?.let { hide(it) }

                //Check if current fragment exists in fragmentManager
                if (current == null) {
                    current = fragment
                    add(containerId, current!!, tag)
                    hide(current!!)
                }
                show(current!!)
            }
            .setTransition(FragmentTransaction.TRANSIT_NONE)
            .setPrimaryNavigationFragment(current)
            //.setReorderingAllowed(true)
            .commitNow()
}

fun Fragment.addOnBackPressedListener(action : () -> Boolean) {
    requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isEnabled && action()) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })

}

fun Fragment.setToolbar(toolbar: Toolbar){
    (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
}

fun Fragment.setEdgeToEdge(insetsChangedListener: OnSystemInsetsChangedListener) {
    (activity as? AppCompatActivity)?.apply {
        setWindowTransparency(insetsChangedListener)
    }
}

