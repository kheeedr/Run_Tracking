package com.khedr.runtracking.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.ItemWelcomeBinding

class WelcomeAdapter : RecyclerView.Adapter<WelcomeAdapter.WelcomeVH>() {

    private val imagesList = mutableListOf(
        R.drawable.img_river_runner,
        R.drawable.img_mountain_runner,
        R.drawable.img_beach_runner,
    )

    override fun onBindViewHolder(holder: WelcomeVH, position: Int) {
        holder.b.ivBackWelcome.setImageResource(imagesList[position])
    }

    override fun getItemCount() = 3

    inner class WelcomeVH(val b: ItemWelcomeBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WelcomeVH {
        return WelcomeVH(
            ItemWelcomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}