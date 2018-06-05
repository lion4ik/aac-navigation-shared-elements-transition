package com.example.android.codelabs.navigation

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
class SharedElementsTransitionFragmentNavigator constructor(private val mContext: Context, private val mFragmentManager: FragmentManager, private val mContainerId: Int) :
        Navigator<FragmentNavigator.Destination>() {

    private var mBackStackCount: Int = 0
    private val mOnBackStackChangedListener: FragmentManager.OnBackStackChangedListener

    init {
        mBackStackCount = mFragmentManager.backStackEntryCount
        mOnBackStackChangedListener = FragmentManager.OnBackStackChangedListener {
            val newCount = mFragmentManager.backStackEntryCount
            val backStackEffect: Int = when {
                newCount < mBackStackCount -> Navigator.BACK_STACK_DESTINATION_POPPED
                newCount > mBackStackCount -> Navigator.BACK_STACK_DESTINATION_ADDED
                else -> Navigator.BACK_STACK_UNCHANGED
            }
            mBackStackCount = newCount

            var destId = 0
            val state = getState()
            if (state != null) {
                destId = state.mCurrentDestId
            }
            dispatchOnNavigatorNavigated(destId, backStackEffect)
        }
        mFragmentManager.addOnBackStackChangedListener(mOnBackStackChangedListener)
    }

    override fun popBackStack(): Boolean {
        return mFragmentManager.popBackStackImmediate()
    }

    override fun createDestination(): FragmentNavigator.Destination {
        return FragmentNavigator.Destination(this)
    }

    private fun getBackStackName(@IdRes destinationId: Int): String =
            try {
                mContext.resources.getResourceName(destinationId)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destinationId)
            }

    override fun navigate(destination: FragmentNavigator.Destination, args: Bundle?,
                          navOptions: NavOptions?) {
        val frag = destination.createFragment(args)
        val ft = mFragmentManager.beginTransaction()

        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        val currentFrag = mFragmentManager.findFragmentById(mContainerId)
        if (currentFrag is HasSharedElements) {
            val sharedElements = currentFrag.getSharedElements()
            ft.setReorderingAllowed(true)
            for ((key, value) in sharedElements){
                ft.addSharedElement(value, key)
            }
        }

        ft.replace(mContainerId, frag)

        val oldState = getState()
        if (oldState != null) {
            ft.remove(oldState)
        }

        @IdRes val destId = destination.id
        val newState = StateFragment()
        newState.mCurrentDestId = destId
        ft.add(newState, StateFragment.FRAGMENT_TAG)

        val initialNavigation = mFragmentManager.fragments.isEmpty()
        val isClearTask = navOptions != null && navOptions.shouldClearTask()
        // TODO Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && oldState != null
                && navOptions.shouldLaunchSingleTop()
                && oldState.mCurrentDestId == destId)
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
        mFragmentManager.executePendingTransactions()
    }

    private fun getState(): StateFragment? {
        return mFragmentManager.findFragmentByTag(StateFragment.FRAGMENT_TAG) as StateFragment?
    }


    /**
     * An internal fragment used by FragmentNavigator to track additional navigation state.
     *
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    class StateFragment : Fragment() {

        internal var mCurrentDestId: Int = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (savedInstanceState != null) {
                mCurrentDestId = savedInstanceState.getInt(KEY_CURRENT_DEST_ID)
            }
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putInt(KEY_CURRENT_DEST_ID, mCurrentDestId)
        }

        companion object {
            internal val FRAGMENT_TAG = "android-support-nav:FragmentNavigator.StateFragment"
            private val KEY_CURRENT_DEST_ID = "currentDestId"
        }
    }
}