package com.quiz.app.ui.quiz.vote

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R
import com.quiz.app.network.model.VotingCategoryItemModel
import com.skydoves.progressview.ProgressView
import java.util.*
import kotlin.math.roundToInt

class VotingCategoryAdapter(
    private val clickListener: OnVotingItemClickListener
) :
    ListAdapter<VotingCategoryItemModel, VotingCategoryAdapter.ViewHolder>(VotingCategoryItemDiffCallback()) {

    private var totalVote = 0
    private var hasVoted = false

    companion object {
        private const val TAG = "VotingCategoryAdapter"
    }

    interface OnVotingItemClickListener {
        fun onVoteItemClick(votingCategoryItemModel: VotingCategoryItemModel, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vote_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener,position)
    }

    fun submitVoteDetails(totalVote: Int, hasVoted: Boolean) {
        this.totalVote = totalVote
        this.hasVoted = hasVoted
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTextView: TextView = itemView.findViewById(R.id.vote_cat_tv)
        private val voteCountTv: TextView = itemView.findViewById(R.id.no_of_vote_tv)
        private val categoryProgressView: ProgressView =
            itemView.findViewById(R.id.category_progress_view)

        fun bind(votingCategoryItemModel: VotingCategoryItemModel, clickListener: OnVotingItemClickListener, position: Int) {
            Log.e(TAG, "vote category: ${votingCategoryItemModel.category}")
            categoryTextView.text = votingCategoryItemModel.category?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            voteCountTv.text = votingCategoryItemModel.votes.toString()
            if (hasVoted) {
                val progress =
                    (votingCategoryItemModel.votes!!.toFloat() / totalVote).times(100).roundToInt()
                Log.e(
                    TAG,
                    "for: ${votingCategoryItemModel.category}Vote: ${votingCategoryItemModel.votes}, Progress: $progress"
                )
                categoryProgressView.progress = progress.toFloat()
            }
            categoryProgressView.setOnClickListener {
                if (hasVoted.not()) clickListener.onVoteItemClick(votingCategoryItemModel, position)
            }
        }
    }

    class VotingCategoryItemDiffCallback : DiffUtil.ItemCallback<VotingCategoryItemModel>() {
        override fun areItemsTheSame(
            oldItem: VotingCategoryItemModel,
            newItem: VotingCategoryItemModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: VotingCategoryItemModel,
            newItem: VotingCategoryItemModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
