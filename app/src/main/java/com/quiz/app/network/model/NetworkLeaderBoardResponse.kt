package com.quiz.app.network.model

import com.google.gson.annotations.SerializedName
import com.quiz.app.database.model.DbParticipant
import com.quiz.app.domain.Participant


data class NetworkLeaderBoardResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val `data`: NetworkLeaderBoardData
)

data class NetworkLeaderBoardData(
    @SerializedName("leaderboard")
    val `data`: NetworkLeaderBoard,
    @SerializedName("total_questions")
    val total_questions: Int
)

data class NetworkLeaderBoard(
    @SerializedName("results")
    val `data`: List<NetworkLeaderBoardResult>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("total_pages")
    val total_pages: Int
)

data class NetworkLeaderBoardResult(
    @SerializedName("quiz")
    val quiz: String,
    @SerializedName("user")
    val networkUser: NetworkUser,
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("correct_answers")
    val correctAnswers: Int,
    @SerializedName("total_duration")
    val totalDuration: Double,
    @SerializedName("_id")
    val id: String
)

data class NetworkUser(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: String
)

fun List<NetworkLeaderBoardResult>.asParticipantList(): List<Participant> {
    return map {
        Participant(
            rank = it.rank,
            profile_pic = "",
            name = it.networkUser.name,
            timeTaken = it.totalDuration,
            total_question = 10,
            correct_answer = it.correctAnswers,
            userId = it.networkUser.id
        )
    }
}

fun List<NetworkLeaderBoardResult>.asEntity(): Array<DbParticipant> {
    return map {
        DbParticipant(
            rank = it.rank,
            profile_pic = "",
            name = it.networkUser.name,
            timeTaken = it.totalDuration,
            total_question = 10,
            correct_answer = it.correctAnswers,
            userId = it.networkUser.id
        )
    }.toTypedArray()
}
