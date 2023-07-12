package com.quiz.app.ui.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R
import com.quiz.app.domain.QuizAnswer

class QuizAnswerAdapter :
    ListAdapter<QuizAnswer, QuizAnswerAdapter.QuizAnswerViewHolder>(QuizAnswerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizAnswerViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.quiz_answer_item, parent, false)
        return QuizAnswerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuizAnswerViewHolder, position: Int) {
        val quizAnswer = getItem(position)
        holder.bind(quizAnswer, position)
    }

    inner class QuizAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionNoTextView: TextView = itemView.findViewById(R.id.question_no_text_view)
        private val questionTextView: TextView = itemView.findViewById(R.id.question_text_view)
        private val answerTextView: TextView = itemView.findViewById(R.id.answer_text_view)
        private val timeTakenTextView: TextView = itemView.findViewById(R.id.time_taken_text_view)
        private val notAnsweredImageView:ImageView = itemView.findViewById(R.id.not_answered_iv)

        fun bind(quizAnswer: QuizAnswer, position: Int) {
            // Customize the UI based on the quiz answer if needed
            questionNoTextView.text = "Q.${position.plus(1)}"
            questionTextView.text = quizAnswer.question
            answerTextView.text = quizAnswer.answer
            notAnsweredImageView.isVisible = quizAnswer.isAnswered.not()

            timeTakenTextView.text ="${quizAnswer.timeTaken} sec"
        }
    }

    class QuizAnswerDiffCallback : DiffUtil.ItemCallback<QuizAnswer>() {
        override fun areItemsTheSame(oldItem: QuizAnswer, newItem: QuizAnswer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QuizAnswer, newItem: QuizAnswer): Boolean {
            return oldItem == newItem
        }
    }
}
