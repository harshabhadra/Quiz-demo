package com.quiz.app.network.model
import com.google.gson.annotations.SerializedName
import com.quiz.app.domain.QuizAnswer


data class NetworkGameSummary(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val `data`: List<NetworkGameSummaryData>
)

data class NetworkGameSummaryData(
    @SerializedName("_id")
    val id: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("user_answer")
    val networkUserAnswer: NetworkUserAnswer
)

data class NetworkUserAnswer(
    @SerializedName("_id")
    val id: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("is_correct")
    val isCorrect: Boolean,
    @SerializedName("text")
    val text: String
)

fun List<NetworkGameSummaryData>.asQuizAnswerList():List<QuizAnswer>{
    return map{
        QuizAnswer(
            it.text,
            it.networkUserAnswer.text,
            it.networkUserAnswer.isCorrect,
            it.networkUserAnswer.duration.toString(),
            it.id
        )
    }
}