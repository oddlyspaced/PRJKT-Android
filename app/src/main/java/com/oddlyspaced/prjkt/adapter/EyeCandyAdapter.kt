package com.oddlyspaced.prjkt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.oddlyspaced.prjkt.R
import com.oddlyspaced.prjkt.databinding.ItemEyeCandyBinding

class EyeCandyAdapter(private val list: ArrayList<Int>): RecyclerView.Adapter<EyeCandyAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = ItemEyeCandyBinding.bind(itemView).imgEyeCandy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val layout = LayoutInflater.from(context).inflate(R.layout.item_eye_candy, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.img.setImageResource(item)
        holder.img.setColorFilter("#111111".toColorInt())
    }

}