package com.quiz.app.network.model
import com.google.gson.annotations.SerializedName


data class NetworkCreateQuizReminder(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val `data`: NetworkReminderData
)

data class NetworkReminderData(
    @SerializedName("quiz")
    val quiz: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("status")
    val status: String,
    @SerializedName("id")
    val id: String
)