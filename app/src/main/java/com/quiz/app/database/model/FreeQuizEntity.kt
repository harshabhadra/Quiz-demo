package com.quiz.app.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quiz.app.domain.FreeQuizUiModel
import com.quiz.app.network.model.FreeQuiz

@Entity(tableName = "free_quiz_table")
data class FreeQuizEntity(
    val count: Int,
    val completed: Int,
    @PrimaryKey
    val category: String
)

fun List<FreeQuizEntity>.asUiModel(): Array<FreeQuizUiModel> {
    return map {
        FreeQuizUiModel(
            count = it.count,
            completed = it.completed,
            category = it.category
        )
    }.toTypedArray()
}