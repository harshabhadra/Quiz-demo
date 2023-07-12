package com.quiz.app.network.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.quiz.app.database.model.UpcomingQuizEntity
import com.quiz.app.database.model.VotingCategoryItemEntity
import com.quiz.app.utils.getRandomId
import kotlinx.parcelize.Parcelize

data class QuizListResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val `data`: QuizListData
)

data class QuizListData(
    @SerializedName("results")
    val results: List<QuizResult>
)

@Parcelize
data class QuizResult(
    @SerializedName("host")
    val host: String,
    @SerializedName("voting_category")
    val voting_category: List<VotingCategoryItemModel>?,
    @SerializedName("status")
    val status: String,
    @SerializedName("category")
    val category: String? = "",
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("is_paid")
    val isPaid: Boolean,
    @SerializedName("description")
    val description: String,
    @SerializedName("is_live")
    val is_live: Boolean,
    @SerializedName("image")
    val image: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("total_votes")
    val totalVotes: Int,
    @SerializedName("has_voted")
    val has_voted: Boolean
) : Parcelable

@Parcelize
data class VotingCategoryItemModel(
    @SerializedName("name")
    val category: String?,
    @SerializedName("total_votes")
    var votes: Int?,
    @SerializedName("_id")
    val id: String?,
) : Parcelable

fun List<QuizResult>.asEntity(): Array<UpcomingQuizEntity> {
    return map {
        UpcomingQuizEntity(
            it.id,
            it.host,
            it.voting_category?.asEntity() ?: emptyList(),
            it.status,
            it.category ?: "",
            it.startDate,
            it.isPaid,
            it.description,
            it.is_live,
            it.image,
            it.totalVotes,
            it.has_voted
        )
    }.toTypedArray()
}

fun List<VotingCategoryItemModel>.asEntity(): List<VotingCategoryItemEntity> {
    return map {
        VotingCategoryItemEntity(
            id = it.id ?: getRandomId("vote"),
            category = it.category ?: "",
            votes = it.votes ?: 0
        )
    }
}
