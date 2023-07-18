package com.quiz.app.ui.viewer

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.annotations.SerializedName
import com.quiz.app.R
import com.quiz.app.domain.DomainOption
import com.skydoves.progressview.ProgressView
import java.util.*
import kotlin.math.roundToInt

data class AnswerItem(
    val category: String?,
    @SerializedName("total_votes")
    var votes: Int?,
    @SerializedName("_id")
    val id: String?,
)

class AnswerAdapter(
    private val clickListener: OnAnswerItemClickListener
) :
    ListAdapter<DomainOption, AnswerAdapter.ViewHolder>(AnswerCategoryItemDiffCallback()) {

    private var totalAnswer = 0
    private var hasAnswerd = false
    private var completed = false
    private var submitIndex = -1

    companion object {
        private const val TAG = "VotingCategoryAdapter"
    }

    interface OnAnswerItemClickListener {
        fun onAnswerItemClick(DomainOption: DomainOption, position: Int)
        fun onCorrectAnswerClick(correct: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.options_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener, position)
    }

    fun submitAnswerDetails(totalAnswer: Int, hasAnswerd: Boolean, completed: Boolean = false) {
        this.totalAnswer = totalAnswer
        this.hasAnswerd = hasAnswerd
        this.completed = completed
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val optionTextview: TextView = itemView.findViewById(R.id.vote_cat_tv)
        private val voteCountTv: TextView = itemView.findViewById(R.id.no_of_vote_tv)
        private val categoryProgressView: ProgressView =
            itemView.findViewById(R.id.category_progress_view)

        fun bind(
            domainOption: DomainOption,
            clickListener: OnAnswerItemClickListener,
            position: Int
        ) {
            Log.e(TAG, "Answer category: ${domainOption.answer}")
            voteCountTv.isVisible = hasAnswerd
            optionTextview.text = domainOption.answer.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            voteCountTv.text = domainOption.votes.toString()

            if (domainOption.votes > 0) {
                val progress = (domainOption.votes.toFloat() / totalAnswer).times(100).roundToInt()
                categoryProgressView.progress = progress.toFloat()
                categoryProgressView.highlightView.color = Color.parseColor("#C2CDFF")//Blue
                categoryProgressView.highlightView.setBackgroundColor(
                    ContextCompat.getColor(
                        categoryProgressView.context,
                        R.color.yellow
                    )
                )
                optionTextview.setTextColor(Color.BLACK)
            }
            if (domainOption.hasVoted) {
                submitIndex = position
                val progress = (domainOption.votes.toFloat() / totalAnswer).times(100).roundToInt()
                categoryProgressView.progress = progress.toFloat()
                categoryProgressView.highlightView.color = Color.parseColor("#FFF8CC")//light yellow
                categoryProgressView.highlightView.setBackgroundColor(
                    ContextCompat.getColor(
                        categoryProgressView.context,
                        R.color.yellow
                    )
                )
                optionTextview.setTextColor(Color.WHITE)
            }
            if (domainOption.hasVoted && domainOption.votes > 0) {
                val progress = (domainOption.votes.toFloat() / totalAnswer).times(100).roundToInt()
                categoryProgressView.progress = progress.toFloat()
                categoryProgressView.highlightView.color = Color.parseColor("#1A3EEC")//Blue
                categoryProgressView.highlightView.setBackgroundColor(
                    ContextCompat.getColor(
                        categoryProgressView.context,
                        R.color.white
                    )
                )
                optionTextview.setTextColor(Color.WHITE)
            }

            if (completed) {
                if (position == submitIndex) {
                    clickListener.onCorrectAnswerClick(domainOption.hasVoted && domainOption.isCorrect)
                }
                categoryProgressView.progress = 100f
                categoryProgressView.highlightView.color =
                    if (domainOption.isCorrect) {
                        Color.parseColor("#3FB26D")
                    } else if (domainOption.hasVoted && domainOption.isCorrect.not()) {
                        Color.parseColor("#1A3EEC")
                    } else {
                        Color.parseColor(("#FF5959"))
                    }
                optionTextview.setTextColor(Color.WHITE)
            }
            if (domainOption.hasVoted.not() && completed.not()) {
                categoryProgressView.progress = 0f
                categoryProgressView.highlightView.color = Color.parseColor("#9E305F")
                optionTextview.setTextColor(Color.BLACK)
            }
            categoryProgressView.setOnClickListener {
                if (hasAnswerd.not()) clickListener.onAnswerItemClick(domainOption, position)
            }
        }
    }

    class AnswerCategoryItemDiffCallback : DiffUtil.ItemCallback<DomainOption>() {
        override fun areItemsTheSame(
            oldItem: DomainOption,
            newItem: DomainOption
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DomainOption,
            newItem: DomainOption
        ): Boolean {
            return oldItem == newItem
        }
    }
}