package com.example.android.codelabs.navigation.recycler_view


import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.android.codelabs.navigation.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class RecyclerViewSharedElementsDetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler_view_shared_elements_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animalItem = arguments!!.getParcelable<AnimalItem>(EXTRA_ANIMAL_ITEM)
        val transitionName = arguments!!.getString(EXTRA_TRANSITION_NAME)

        val detailTextView = view.findViewById<View>(R.id.animal_detail_text) as TextView
        detailTextView.text = animalItem!!.detail

        val imageView = view.findViewById<View>(R.id.animal_detail_image_view) as ImageView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.transitionName = transitionName
        }

        Picasso.get()
                .load(animalItem.imageUrl)
                .noFade()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        startPostponedEnterTransition()
                    }

                    override fun onError(e: Exception?) {
                        startPostponedEnterTransition()
                    }
                })
    }

    companion object {

        const val EXTRA_ANIMAL_ITEM = "animal_item"
        const val EXTRA_TRANSITION_NAME = "transition_name"
    }
}