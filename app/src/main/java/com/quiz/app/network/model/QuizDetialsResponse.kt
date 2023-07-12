package com.quiz.app.network.model
import com.google.gson.annotations.SerializedName

data class QuizDetailsResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val `data`: QuizDetailsData
)

data class QuizDetailsData(
    @SerializedName("_id")
    val id: String,
    @SerializedName("host")
    val host: String,
    @SerializedName("category")
    val category: QuizCategory?=null,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("is_paid")
    val isPaid: Boolean,
    @SerializedName("description")
    val description: String,
    @SerializedName("is_live")
    val isLive: Boolean,
    @SerializedName("image")
    val image: String,
    @SerializedName("quiz_reminders")
    val quizReminders: QuizReminders?=null,
    @SerializedName("category_total")
    val categoryTotal: Int,
    @SerializedName("completed")
    val completed:Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("voting_category")
    val votingCategoryItemModel: List<VotingCategoryItemModel>?,
    @SerializedName("has_voted")
    val has_voted:Boolean,
    @SerializedName("total_votes")
    val total_votes:Int
)

data class QuizCategory(
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("active")
    val active: Boolean
)

data class QuizReminders(
    @SerializedName("_id")
    val id: String,
    @SerializedName("quiz")
    val quiz: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("status")
    val status: String
)