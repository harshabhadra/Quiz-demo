package com.quiz.app.domain

import com.google.gson.annotations.SerializedName

data class DomainOption(
    val id: String,
    val answer: String,
    var isCorrect: Boolean,
    var hasVoted :Boolean = false,
    var votes:Int
)