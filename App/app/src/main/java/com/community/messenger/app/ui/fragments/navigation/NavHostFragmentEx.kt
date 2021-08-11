package com.community.messenger.app.ui.fragments.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.community.messenger.app.ui.fragments.replace
import com.community.messenger.app.R
import java.util.*

class NavHostFragmentEx : NavHostFragment(){

    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return FragmentNavigatorEx(requireContext(),childFragmentManager,getContainerId())
    }

    private fun getContainerId() : Int{
        val id = id
        return if (id != 0 && id != View.NO_ID) {
            id
        } else R.id.nav_host_fragment_container
    }
}

@Navigator.Name("fragment")
open class FragmentNavigatorEx(val mContext: Context, val  mFragmentManager: FragmentManager, val mContainerId: Int)  : Navigator<FragmentNavigator.Destination>() {
    val TAG = "FragmentNavigator"
    val KEY_BACK_STACK_IDS = "androidx-nav-fragment:navigator:backStackIds"


    private val mBackStack = ArrayDeque<Int>()

    override fun popBackStack(): Boolean {
        if (mBackStack.isEmpty()) {
            return false
        }
        if (mFragmentManager!!.isStateSaved) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already"
                    + " saved its state")
            return false
        }
        mFragmentManager.popBackStack(
                generateBackStackName(mBackStack.size, mBackStack.peekLast()),
                FragmentManager.POP_BACK_STACK_INCLUSIVE)
        mBackStack.removeLast()
        return true
    }

    override fun createDestination(): FragmentNavigator.Destination {
        return FragmentNavigator.Destination(this)
    }


    open fun instantiateFragment(context: Context,
                                 fragmentManager: FragmentManager,
                                 className: String, args: Bundle?): Fragment {
        return fragmentManager.fragmentFactory.instantiate(
                context.classLoader, className)
    }

    override fun navigate(destination: FragmentNavigator.Destination, args: Bundle?,
                      navOptions: NavOptions?, navigatorExtras: Navigator.Extras?): NavDestination? {
        if (mFragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already"
                    + " saved its state")
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = mContext.getPackageName() + className
        }
        val frag = instantiateFragment(mContext, mFragmentManager,
                className, args)
        frag.arguments = args


        // val ft = mFragmentManager.beginTransaction()
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
//            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)

        }

        var isAdded = false
        @IdRes val destId = destination.id

        mFragmentManager.replace(mContainerId, frag, args) {
            setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)

            val initialNavigation = mBackStack.isEmpty()
            // TODO Build first class singleTop behavior for fragments

            val isPopupToInclusive = navOptions?.isPopUpToInclusive == true
            val isSingleTopReplacement = navOptions?.shouldLaunchSingleTop() == true
            isAdded = when {

                isPopupToInclusive || initialNavigation->{
                    mBackStack.clear()
                    repeat(mFragmentManager.backStackEntryCount){
                        mFragmentManager.popBackStack()
                    }
                    true
                }

                isSingleTopReplacement -> {
                    // Single Top means we only want one instance on the back stack
                    if (mBackStack.size > 1) {
                        // If the Fragment to be replaced is on the FragmentManager's
                        // back stack, a simple replace() isn't enough so we
                        // remove it from the back stack and put our replacement
                        // on the back stack in its place
                        mFragmentManager.popBackStack(
                                generateBackStackName(mBackStack.size, mBackStack.peekLast()),
                                FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        addToBackStack(generateBackStackName(mBackStack.size, destId))
                    }
                    false
                }
                else -> {
                    addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
                    true
                }
            }
            if (navigatorExtras is FragmentNavigator.Extras) {
                for ((key, value) in navigatorExtras.sharedElements) {
                    addSharedElement(key, value)
                }
            }
            setReorderingAllowed(true)
            // The commit succeeded, update our view of the world
        }
        // ft.replace(mContainerId, frag)
        //ft.setPrimaryNavigationFragment(frag)

        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    override fun onSaveState(): Bundle? {
        val b = Bundle()
        val backStack = IntArray(mBackStack.size)
        var index = 0
        for (id in mBackStack) {
            backStack[index++] = id
        }
        b.putIntArray(KEY_BACK_STACK_IDS, backStack)
        return b
    }

    override fun onRestoreState(savedState: Bundle) {
        val backStack = savedState.getIntArray(KEY_BACK_STACK_IDS)
        if (backStack != null) {
            mBackStack.clear()
            for (destId in backStack) {
                mBackStack.add(destId)
            }
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }
}