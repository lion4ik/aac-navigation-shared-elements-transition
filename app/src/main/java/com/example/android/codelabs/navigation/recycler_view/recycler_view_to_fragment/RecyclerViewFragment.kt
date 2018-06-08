package com.example.android.codelabs.navigation.recycler_view.recycler_view_to_fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation

import com.example.android.codelabs.navigation.R
import com.example.android.codelabs.navigation.recycler_view.AnimalGalleryAdapter
import com.example.android.codelabs.navigation.recycler_view.AnimalItem
import com.example.android.codelabs.navigation.recycler_view.AnimalItemClickListener
import com.example.android.codelabs.navigation.recycler_view.Utils
import com.github.lion4ik.arch.sharedelements.HasSharedElements


class RecyclerViewFragment : Fragment(), AnimalItemClickListener, HasSharedElements {

    private val sharedElements: MutableMap<String, View> = mutableMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animalGalleryAdapter = AnimalGalleryAdapter(Utils.generateAnimalItems(context!!), this)
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        val gridLayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = animalGalleryAdapter
    }

    override fun onAnimalItemClick(pos: Int, animalItem: AnimalItem, sharedImageView: ImageView) {
        sharedElements.clear()
        sharedElements[ViewCompat.getTransitionName(sharedImageView)] = sharedImageView
        Navigation.findNavController(sharedImageView).navigate(R.id.animal_details_fragment, Bundle().apply {
            putParcelable(AnimalDetailFragment.EXTRA_ANIMAL_ITEM, animalItem)
            putString(AnimalDetailFragment.EXTRA_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView))
        })
    }

    override fun getSharedElements(): Map<String, View> = sharedElements

    override fun hasReorderingAllowed(): Boolean = false
}