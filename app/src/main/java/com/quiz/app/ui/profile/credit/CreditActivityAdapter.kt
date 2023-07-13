package com.quiz.app.ui.profile.credit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R
import com.quiz.app.domain.model.CreditActivity

class CreditActivityAdapter :
    ListAdapter<CreditActivity, CreditActivityAdapter.CreditActivityViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditActivityViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.credit_activity_item, parent, false)
        return CreditActivityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CreditActivityViewHolder, position: Int) {
        val creditActivity = getItem(position)
        holder.bind(creditActivity)
    }

    inner class CreditActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val monthYearTextView: TextView = itemView.findViewById(R.id.month_year_text_view)
        private val creditRecyclerView: RecyclerView = itemView.findViewById(R.id.credit_recycler_view)
        private val creditAdapter = CreditDataAdapter()

        init {
            creditRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            creditRecyclerView.adapter = creditAdapter
        }

        fun bind(creditActivity: CreditActivity) {
            monthYearTextView.text = creditActivity.date
            creditAdapter.submitList(creditActivity.creditList)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CreditActivity>() {
        override fun areItemsTheSame(oldItem: CreditActivity, newItem: CreditActivity): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CreditActivity, newItem: CreditActivity): Boolean {
            return oldItem == newItem
        }
    }
}
