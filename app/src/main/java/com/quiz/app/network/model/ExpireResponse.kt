package com.quiz.app.network.model

data class ErrorResponse(
    val code:Int,
    val message:String,
    val data:String
)