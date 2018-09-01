package com.example.android.codelabs.navigation.recycler_view

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.example.android.codelabs.navigation.R
import com.squareup.picasso.Picasso

import java.util.ArrayList

/**
 * Created by msc10 on 16/02/2017.
 */

class AnimalGalleryAdapter(private val animalItems: ArrayList<AnimalItem>, private val animalItemClickListener: AnimalItemClickListener) : RecyclerView.Adapter<AnimalGalleryAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_animal_square, parent, false))
    }

    override fun getItemCount(): Int {
        return animalItems.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val animalItem = animalItems[position]

        Picasso.get()
                .load(animalItem.imageUrl)
                .into(holder.animalImageView)

        ViewCompat.setTransitionName(holder.animalImageView, animalItem.name)

        holder.itemView.setOnClickListener { animalItemClickListener.onAnimalItemClick(holder.adapterPosition, animalItem, holder.animalImageView) }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val animalImageView: ImageView = itemView.findViewById<View>(R.id.item_animal_square_image) as ImageView
    }
}
