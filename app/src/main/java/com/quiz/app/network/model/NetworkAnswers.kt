package com.quiz.app.network.model
import com.google.gson.annotations.SerializedName


data class NetworkAnswers(
    @SerializedName("totalAnswers")
    val networkTotalAnswers: List<NetworkTotalAnswer>
)

data class NetworkTotalAnswer(
    @SerializedName("_id")
    val id: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("is_correct")
    val isCorrect: Boolean,
    @SerializedName("total_answers")
    val totalAnswers: Int
)