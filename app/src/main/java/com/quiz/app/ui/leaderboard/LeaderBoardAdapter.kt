package com.quiz.app.ui.leaderboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.MoxieApplication.Companion.sessionManager
import com.quiz.app.R
import com.quiz.app.databinding.LeaderBoardListItemBinding
import com.quiz.app.domain.Participant
import com.quiz.app.utils.SessionManager



class LeaderBoardAdapter :
    ListAdapter<Participant, LeaderBoardAdapter.ParticipantViewHolder>(ParticipantDiffCallback()) {

    var total_question = 0

    fun submitTotalQuestions(value: Int) {
        total_question = value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LeaderBoardListItemBinding.inflate(inflater, parent, false)
        return ParticipantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = getItem(position)
        holder.bind(participant)
    }

    inner class ParticipantViewHolder(private val binding: LeaderBoardListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(participant: Participant) {
            binding.apply {
                scoreTv.text = participant.rank.toString()
                participantNameTv.text = participant.name
                timeTakenTv.text = "${participant.timeTaken} sec"
                answerTv.text = "${participant.correct_answer}/$total_question"
                val userId = sessionManager?.getPrefString(SessionManager.USER_ID) ?: ""

                scoreTv.setTextColor(
                    if (participant.userId == userId) ContextCompat.getColor(
                        scoreTv.context,
                        R.color.dark_purple
                    ) else Color.WHITE
                )
                participantNameTv.setTextColor(
                    if (participant.userId == userId) ContextCompat.getColor(
                        participantNameTv.context,
                        R.color.dark_purple
                    ) else Color.WHITE
                )
                timeTakenTv.setTextColor(
                    if (participant.userId == userId) ContextCompat.getColor(
                        timeTakenTv.context,
                        R.color.dark_purple
                    ) else Color.WHITE
                )
                answerTv.setTextColor(
                    if (participant.userId == userId) ContextCompat.getColor(
                        answerTv.context,
                        R.color.dark_purple
                    ) else Color.WHITE
                )
                if (participant.userId == userId) {
                    leaderBoardContainer.background = ContextCompat.getDrawable(
                        binding.leaderBoardContainer.context,
                        R.drawable.while_rounded_bg
                    )
                } else {
                    leaderBoardContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.leaderBoardContainer.context,
                            R.color.dark_purple
                        )
                    )
                }
            }
        }
    }

    class ParticipantDiffCallback : DiffUtil.ItemCallback<Participant>() {
        override fun areItemsTheSame(oldItem: Participant, newItem: Participant): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Participant, newItem: Participant): Boolean {
            return oldItem == newItem
        }
    }
}

