package com.quiz.app.network.model

import com.google.gson.annotations.SerializedName
import com.quiz.app.domain.DomainOption

data class Question(
    @SerializedName("question")
    val question: QuestionData,
    @SerializedName("question_index")
    val question_index:Int?,
    @SerializedName("total_questions")
    val totalQuestions:Int?
)

data class QuestionData(
    @SerializedName("_id")
    val id: String,
    @SerializedName("text")
    val question: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("options")
    val options: List<Option>?
)

data class Option(
    @SerializedName("_id")
    val id: String,
    @SerializedName("text")
    val answer: String,
    @SerializedName("is_correct")
    var isCorrect: Boolean?=false
)

fun List<Option>.asDomainOptionList(): List<DomainOption> {
    return map {
        DomainOption(
            it.id,
            it.answer,
            it.isCorrect == true,
            false,
            0
        )
    }
}