package com.oddlyspaced.prjkt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.oddlyspaced.prjkt.R
import com.oddlyspaced.prjkt.databinding.ItemShapeBinding
import com.oddlyspaced.prjkt.modal.ShapeItem

class ShapeSelectAdapter(private val list: ArrayList<ShapeItem>): RecyclerView.Adapter<ShapeSelectAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = ItemShapeBinding.bind(itemView).cardItemSingle
        val icon: ImageView = ItemShapeBinding.bind(itemView).imgShape
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val layout = LayoutInflater.from(context).inflate(R.layout.item_shape, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.icon.setImageDrawable(ContextCompat.getDrawable(context, item.icon))
        if (item.isActive) {
            holder.card.strokeColor = ContextCompat.getColor(context, R.color.blue)
        }
        else {
            holder.card.strokeColor = ContextCompat.getColor(context, R.color.background_light)
        }
        holder.card.setOnClickListener {
            item.onClick()
            activateSingle(position)
            notifyDataSetChanged()
        }
    }

    private fun activateSingle(position: Int) {
        list.forEach {
            it.isActive = false
        }
        list[position].isActive = true
    }

}