package com.quiz.app.domain.model

data class UpcomingQuizUiModel(
    val id: String,
    val host: String,
    val voting_category: List<VotingCategoryItem>,
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

data class VotingCategoryItem(
    val id: String,
    val category: String,
    var votes: Int,
)
