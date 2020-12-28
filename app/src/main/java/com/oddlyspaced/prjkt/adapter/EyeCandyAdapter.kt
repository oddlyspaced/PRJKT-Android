package com.oddlyspaced.prjkt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.oddlyspaced.prjkt.R
import com.oddlyspaced.prjkt.databinding.ItemEyeCandyBinding
import com.oddlyspaced.prjkt.databinding.ItemShapeBinding
import com.oddlyspaced.prjkt.modal.ShapeItem

class EyeCandyAdapter(private val list: ArrayList<String>): RecyclerView.Adapter<EyeCandyAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = ItemEyeCandyBinding.bind(itemView).txEyeCandy
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
        holder.text.text = item
    }

}