package com.quiz.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.quiz.app.R
import com.quiz.app.domain.FreeQuizUiModel
import com.quiz.app.network.model.FreeQuiz
import com.quiz.app.utils.getRandomColor

class FreeQuizListAdapter :
    ListAdapter<FreeQuizUiModel, FreeQuizListAdapter.FreeQuizListViewHolder>(FreeQuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeQuizListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.free_quiz_list_item, parent, false)
        return FreeQuizListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FreeQuizListViewHolder, position: Int) {
        val FreeQuiz = getItem(position)
        holder.bind(FreeQuiz)
    }

    inner class FreeQuizListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryTv)
        private val availableQuizTextView: TextView = itemView.findViewById(R.id.availableTv)
        private val catCard: MaterialCardView = itemView.findViewById(R.id.quizCategoryLayout)

        fun bind(FreeQuiz: FreeQuizUiModel) {
            catCard.setCardBackgroundColor(getRandomColor())
            categoryNameTextView.text = FreeQuiz.category
            availableQuizTextView.text =
                "Available Quiz: ${FreeQuiz.count} . Completed: ${FreeQuiz.completed}"
        }
    }

    class FreeQuizDiffCallback : DiffUtil.ItemCallback<FreeQuizUiModel>() {
        override fun areItemsTheSame(oldItem: FreeQuizUiModel, newItem: FreeQuizUiModel): Boolean {
            return oldItem.category == newItem.category
        }

        override fun areContentsTheSame(oldItem: FreeQuizUiModel, newItem: FreeQuizUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
