package com.quiz.app.network.model

import com.google.gson.annotations.SerializedName
import com.quiz.app.database.model.FreeQuizEntity
import com.quiz.app.utils.getRandomId

data class QuizOverviewResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val `data`: QuizOverviewData
)

data class QuizOverviewData(
    @SerializedName("free")
    val free: List<FreeQuiz>
)

data class FreeQuiz(
    @SerializedName("count")
    val count: Int,
    @SerializedName("completed")
    val completed: Int,
    @SerializedName("category")
    val category: String
)

fun List<FreeQuiz>.asEntity(): Array<FreeQuizEntity> {
    return map {
        FreeQuizEntity(
            count = it.count,
            completed = it.completed,
            category = it.category
        )
    }.toTypedArray()
}