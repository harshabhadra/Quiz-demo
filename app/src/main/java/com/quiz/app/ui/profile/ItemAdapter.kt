package com.quiz.app.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R

class ItemAdapter(private val clickListener: OnProfileSubItemClickListener) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    private var itemList: List<String> = emptyList()

    interface OnProfileSubItemClickListener {
        fun onProfileSubItemClick(value: String)
    }

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
        holder.itemTextView.setTextColor(
            ContextCompat.getColor(
                holder.itemTextView.context,
                if (itemList[position] == "Log out") R.color.glow_red else R.color.black
            )
        )
        holder.itemTextView.setOnClickListener {
            clickListener.onProfileSubItemClick(itemList[position])
        }
    }

    override fun getItemCount() = itemList.size

    fun setItems(items: List<String>) {
        itemList = items
        notifyDataSetChanged()
    }
}
