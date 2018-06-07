package com.github.lion4ik.arch.sharedelements

import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment

class NavHostSharedElementsTransitionFragment : NavHostFragment() {

    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return context?.let { SharedElementsTransitionFragmentNavigator(it, childFragmentManager, id) } ?: throw IllegalArgumentException("Context is null!")
    }
}