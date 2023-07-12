package com.quiz.app.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quiz.app.domain.model.UpcomingQuizUiModel
import com.quiz.app.domain.model.VotingCategoryItem

@Entity(tableName = "upcoming_quiz_table")
data class UpcomingQuizEntity(
    @PrimaryKey
    val id: String,
    val host: String,
    val voting_category: List<VotingCategoryItemEntity>,
    val status: String,
    val category: String,
    val startDate: String,
    val isPaid: Boolean,
    val description: String,
    val is_live: Boolean,
    val image: String,
    val totalVotes: Int,
    val has_voted: Boolean
)

@Entity
data class VotingCategoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: String = "1",
    val category: String,
    var votes: Int,
)

fun List<UpcomingQuizEntity>.asUiModel(): Array<UpcomingQuizUiModel> {
    return map {
        UpcomingQuizUiModel(
            it.id,
            it.host,
            it.voting_category.asUiModel(),
            it.status,
            it.category ?: "",
            it.startDate,
            it.isPaid,
            it.description,
            it.is_live,
            it.image,
            it.totalVotes,
            it.has_voted
        )
    }.toTypedArray()
}

fun List<VotingCategoryItemEntity>.asUiModel(): List<VotingCategoryItem> {
    return map {
        VotingCategoryItem(
            id = it.id,
            category = it.category,
            votes = it.votes
        )
    }
}