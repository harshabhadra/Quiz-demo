package com.quiz.app.ui.profile.pastQuizzes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quiz.app.R
import com.quiz.app.domain.model.PastQuiz

class PastQuizListAdapter : ListAdapter<PastQuiz, PastQuizListAdapter.MyViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.past_quiz_item_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.thumbnailImageView)
        private val dateTextView: TextView = itemView.findViewById(R.id.quizDateTv)
        private val rankTextView: TextView = itemView.findViewById(R.id.rank_tv)

        fun bind(item: PastQuiz) {
            // Load image using your preferred image loading library
//            Glide.with(itemView.context).load(item.image).into(imageView)

            dateTextView.text = item.date
            rankTextView.text = "Ranked ${item.rank.toString()}"
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PastQuiz>() {
        override fun areItemsTheSame(oldItem: PastQuiz, newItem: PastQuiz): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PastQuiz, newItem: PastQuiz): Boolean {
            return oldItem.image == newItem.image &&
                    oldItem.date == newItem.date &&
                    oldItem.rank == newItem.rank
        }
    }
}
