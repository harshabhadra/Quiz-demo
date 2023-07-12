package com.quiz.app.domain

import androidx.room.PrimaryKey

data class FreeQuizUiModel(
    val count: Int,
    val completed: Int,
    val category: String
)
