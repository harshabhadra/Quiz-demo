package com.quiz.app.network.model
import com.google.gson.annotations.SerializedName


data class StreamState(
    @SerializedName("quiz")
    val quiz: String,
    @SerializedName("status")
    val status: String
)