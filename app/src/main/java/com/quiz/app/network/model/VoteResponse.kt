package com.quiz.app.network.model
import com.google.gson.annotations.SerializedName


data class VoteResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val `data`: VoteData
)

data class VoteData(
    @SerializedName("quiz")
    val quiz: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: String
)