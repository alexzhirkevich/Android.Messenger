package com.community.messenger.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun FragmentManager.
        replace(
        containerId: Int,
        fragment: Fragment,
        args : Bundle?=null,
        also : FragmentTransaction.()->Unit = {}) : Boolean{

    val tag = fragment.javaClass.name + args.hashCode()
    var current = findFragmentByTag(tag)

//    if (current == fragment)
//        return false

    val isAdded = current == null
    beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .setReorderingAllowed(true)
            .apply {
                also(this)

                primaryNavigationFragment?.let {
                    hide(it)
                }

                if (current == null) {
                    current = fragment
                    current!!.arguments = args
                    add(containerId, fragment, tag)
                    hide(fragment)
                }else
                    current!!.arguments = args
                show(current!!)
            }

            .apply {
                if (isAddToBackStackAllowed) addToBackStack(tag)
            }
            .setPrimaryNavigationFragment(current)
            .commit()
    return isAdded
}

fun Fragment.setDisplayHomeAsUpEnabled(boolean: Boolean){
    (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
}

fun Fragment.addOnBackPressedListener(action : (Removable) -> Boolean) : Removable {
    var callback: OnBackPressedCallback? = null

    val removable = object :Removable{
        override fun remove() {
            callback?.remove()

        }
    }

    callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isEnabled && isVisible && !isHidden) {
                if (!action(removable)) {
                    return
                }
            }
            isEnabled = false
            removable.remove()
            requireActivity().onBackPressed()
        }
    }


    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    return removable
}

interface Removable {
    fun remove()
}

fun Fragment.setToolbar(toolbar: Toolbar){
    (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
}

fun <T: View> Fragment.findViewById(@IdRes id : Int) : T
    = requireView().findViewById(id)


