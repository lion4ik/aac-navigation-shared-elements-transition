package com.github.lion4ik.arch.sharedelements

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.RestrictTo
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("fragment")
class SharedElementsTransitionFragmentNavigator constructor(private val context: Context, private val fragmentManager: FragmentManager, private val containerId: Int) :
        Navigator<FragmentNavigator.Destination>() {

    private var backStackCount: Int = 0
    private val onBackStackChangedListener: FragmentManager.OnBackStackChangedListener

    companion object {
        const val NO_FRAGMENT_ANIMATION = 0
        const val FRAGMENT_ANIMATION_NOT_SET = -1
    }

    init {
        backStackCount = fragmentManager.backStackEntryCount
        onBackStackChangedListener = FragmentManager.OnBackStackChangedListener {
            val newCount = fragmentManager.backStackEntryCount
            val backStackEffect: Int = when {
                newCount < backStackCount -> Navigator.BACK_STACK_DESTINATION_POPPED
                newCount > backStackCount -> Navigator.BACK_STACK_DESTINATION_ADDED
                else -> Navigator.BACK_STACK_UNCHANGED
            }
            backStackCount = newCount

            val state = getState()
            val destId = state?.currentDestId ?: 0
            dispatchOnNavigatorNavigated(destId, backStackEffect)
        }
        fragmentManager.addOnBackStackChangedListener(onBackStackChangedListener)
    }

    override fun popBackStack(): Boolean = fragmentManager.popBackStackImmediate()
    override fun createDestination(): FragmentNavigator.Destination = FragmentNavigator.Destination(this)

    private fun getBackStackName(@IdRes destinationId: Int): String =
            try {
                context.resources.getResourceName(destinationId)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destinationId)
            }

    override fun navigate(destination: FragmentNavigator.Destination, args: Bundle?,
                          navOptions: NavOptions?) {
        val frag = destination.createFragment(args)
        val ft = fragmentManager.beginTransaction()

        navOptions?.let {
            val enterAnim = if (it.enterAnim == FRAGMENT_ANIMATION_NOT_SET) NO_FRAGMENT_ANIMATION else it.enterAnim
            val exitAnim = if (it.exitAnim == FRAGMENT_ANIMATION_NOT_SET) NO_FRAGMENT_ANIMATION else it.exitAnim
            val popEnterAnim = if (it.popEnterAnim == FRAGMENT_ANIMATION_NOT_SET) NO_FRAGMENT_ANIMATION else it.popEnterAnim
            val popExitAnim = if (it.popExitAnim == FRAGMENT_ANIMATION_NOT_SET) NO_FRAGMENT_ANIMATION else it.popExitAnim
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        val currentFrag = fragmentManager.findFragmentById(containerId)
        if (currentFrag is HasSharedElements) {
            val sharedElements = currentFrag.getSharedElements()
            ft.setReorderingAllowed(currentFrag.hasReorderingAllowed())
            for ((key, value) in sharedElements) {
                ft.addSharedElement(value, key)
            }
        }

        ft.replace(containerId, frag)

        val oldState = getState()
        oldState?.let { ft.remove(it) }

        @IdRes val destId = destination.id
        val newState = StateFragment()
        newState.currentDestId = destId
        ft.add(newState, StateFragment.FRAGMENT_TAG)

        val initialNavigation = fragmentManager.fragments.isEmpty()
        val isClearTask = navOptions?.shouldClearTask() ?: false
        // TODO Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && oldState != null
                && navOptions.shouldLaunchSingleTop()
                && oldState.currentDestId == destId)
        if (!initialNavigation && !isClearTask && !isSingleTopReplacement) {
            ft.addToBackStack(getBackStackName(destId))
        } else {
            ft.runOnCommit {
                dispatchOnNavigatorNavigated(destId, if (isSingleTopReplacement)
                    Navigator.BACK_STACK_UNCHANGED
                else
                    Navigator.BACK_STACK_DESTINATION_ADDED)
            }
        }
        ft.commit()
        fragmentManager.executePendingTransactions()
    }

    private fun getState(): StateFragment? = fragmentManager.findFragmentByTag(StateFragment.FRAGMENT_TAG) as StateFragment?

    /**
     * An internal fragment used by FragmentNavigator to track additional navigation state.
     *
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    class StateFragment : Fragment() {
        internal var currentDestId: Int = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            savedInstanceState?.let { currentDestId = savedInstanceState.getInt(KEY_CURRENT_DEST_ID) }
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putInt(KEY_CURRENT_DEST_ID, currentDestId)
        }

        companion object {
            internal val FRAGMENT_TAG = "android-support-nav:FragmentNavigator.StateFragment"
            private val KEY_CURRENT_DEST_ID = "currentDestId"
        }
    }
}