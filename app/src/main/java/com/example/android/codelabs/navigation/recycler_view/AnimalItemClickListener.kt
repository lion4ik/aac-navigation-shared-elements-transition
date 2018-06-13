package com.example.android.codelabs.navigation.recycler_view

import android.widget.ImageView

/**
 * Created by msc10 on 19/02/2017.
 */

interface AnimalItemClickListener {
    fun onAnimalItemClick(pos: Int, animalItem: AnimalItem, shareImageView: ImageView)
}