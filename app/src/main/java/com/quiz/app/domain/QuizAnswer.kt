package com.quiz.app.domain

data class QuizAnswer(
    val question: String,
    val answer: String,
    val isAnswered: Boolean,
    val timeTaken: String,
    val id: String? = "",
)