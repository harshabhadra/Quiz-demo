package com.quiz.app.domain

data class Participant(
    val rank: Int,
    val profile_pic: String,
    val name: String,
    val timeTaken: Double,
    val total_question: Int,
    val correct_answer: Int,
    val userId: String
)