package com.example.android.codelabs.navigation


import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SettingsImageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val transition = TransitionInflater.from(context)
                .inflateTransition(R.transition.image_transition)
        sharedElementEnterTransition = transition
        return inflater.inflate(R.layout.fragment_settings_image, container, false)
    }
}
