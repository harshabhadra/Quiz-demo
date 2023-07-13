package com.quiz.app.ui.profile.credit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R
import com.quiz.app.domain.model.CreditData

class CreditDataAdapter :
    ListAdapter<CreditData, CreditDataAdapter.CreditDataViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditDataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.credit_data_item, parent, false)
        return CreditDataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CreditDataViewHolder, position: Int) {
        val creditData = getItem(position)
        holder.bind(creditData)
    }

    inner class CreditDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_text_view)
        private val creditTextView: TextView = itemView.findViewById(R.id.credit_text_view)
        private val amountTextView: TextView = itemView.findViewById(R.id.amount_text_view)

        fun bind(creditData: CreditData) {
            titleTextView.text = creditData.title
            dateTextView.text = creditData.date
            creditTextView.text = "(${creditData.credit} credits)"
            amountTextView.text = "$${creditData.amount}"
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CreditData>() {
        override fun areItemsTheSame(oldItem: CreditData, newItem: CreditData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CreditData, newItem: CreditData): Boolean {
            return oldItem.title == newItem.title && oldItem.date == newItem.date &&
                    oldItem.category == newItem.category && oldItem.credit == newItem.credit &&
                    oldItem.amount == newItem.amount
        }
    }
}
