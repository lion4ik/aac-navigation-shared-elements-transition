package com.example.android.codelabs.navigation


import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SharedElementDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val transition = TransitionInflater.from(context)
                .inflateTransition(R.transition.image_transition)
        sharedElementEnterTransition = transition
        return inflater.inflate(R.layout.fragment_shared_elements_details, container, false)
    }
}
