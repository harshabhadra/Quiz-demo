package com.quiz.app.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R

class ItemAdapter() : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    private var itemList: List<String> = emptyList()

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTextView: TextView = itemView.findViewById(R.id.itemTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_sub_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemTextView.text = itemList[position]
    }

    override fun getItemCount() = itemList.size

    fun setItems(items: List<String>) {
        itemList = items
        notifyDataSetChanged()
    }
}
