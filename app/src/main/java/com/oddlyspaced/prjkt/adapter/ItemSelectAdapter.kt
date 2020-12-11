package com.oddlyspaced.prjkt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.oddlyspaced.prjkt.R
import com.oddlyspaced.prjkt.databinding.ItemOnlyIconBinding

// Adapter for the library list view on About page
class ItemSelectAdapter(private val list: ArrayList<Boolean>): RecyclerView.Adapter<ItemSelectAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = ItemOnlyIconBinding.bind(itemView).cardItemSingle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val layout = LayoutInflater.from(context).inflate(R.layout.item_only_icon, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        if (item) {
            holder.card.strokeColor = "#317AFF".toColorInt()
        }
    }

}