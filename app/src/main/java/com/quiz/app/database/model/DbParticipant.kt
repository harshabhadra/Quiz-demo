package com.quiz.app.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quiz.app.domain.Participant

@Entity(tableName = "participant_table")
data class DbParticipant(
    @PrimaryKey
    val rank: Int,
    val profile_pic: String,
    val name: String,
    val timeTaken: Double,
    val total_question: Int,
    val correct_answer: Int,
    val userId: String
)

fun List<DbParticipant>.asParticipantList(): List<Participant> {
    return map {
        Participant(
            rank = it.rank,
            profile_pic = "",
            name = it.name,
            timeTaken = it.timeTaken,
            total_question = 10,
            correct_answer = it.correct_answer,
            userId = it.userId
        )
    }
}