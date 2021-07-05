package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun FragmentManager.replace(
        containerId: Int,
        fragment: Fragment,
        args : Bundle?=null,
        also : FragmentTransaction.()->Unit = {}) {
    val tag = fragment.javaClass.name
    var current = findFragmentByTag(tag)

    beginTransaction()
            .apply {
                primaryNavigationFragment?.let {
                    hide(it)
                }

                if (current == null) {
                    current = fragment
                    add(containerId, current!!, tag)
                    hide(current!!)
                }
                show(current!!)
                args?.let { current!!.arguments = it }
            }
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .setReorderingAllowed(true)
            .apply {
                also(this)
                if (isAddToBackStackAllowed) addToBackStack(tag)
            }
            .setPrimaryNavigationFragment(current)
            .commit()
}

fun Fragment.setDisplayHomeAsUpEnabled(boolean: Boolean){
    (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
}

fun Fragment.addOnBackPressedListener(action : () -> Boolean) : Removable {

    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isEnabled) {
                if (!action()) {
                    return
                }
            }
            isEnabled = false
            requireActivity().onBackPressed()
        }
    }

    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    return object : Removable {
        override fun remove() {
            callback.remove()
        }
    }
}

interface Removable {
    fun remove()
}

fun Fragment.setToolbar(toolbar: Toolbar){
    (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
}

fun <T: View> Fragment.findViewById(@IdRes id : Int) : T
    = requireView().findViewById(id)


