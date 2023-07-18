package com.quiz.app.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R

data class ProfileItem(val title: String, val items: List<String>)
class ProfileAdapter(private val clickListener: OnProfileItemClickListener) :
    ListAdapter<ProfileItem, ProfileAdapter.ProfileViewHolder>(ProfileItemDiffCallback()) {

    interface OnProfileItemClickListener{
        fun onProfileItemClick(section:String)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_item_layout, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemAdapter.OnProfileSubItemClickListener {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsRecyclerView)
        private val itemAdapter = ItemAdapter(this)

        fun bind(profileItem: ProfileItem) {
            title.text = profileItem.title
            title.isVisible = profileItem.title.isNotEmpty()
            itemAdapter.setItems(profileItem.items)
            itemsRecyclerView.adapter = itemAdapter
        }

        override fun onProfileSubItemClick(value: String) {
            clickListener.onProfileItemClick(value)
        }
    }

    class ProfileItemDiffCallback : DiffUtil.ItemCallback<ProfileItem>() {
        override fun areItemsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean {
            return oldItem == newItem
        }
    }
}
