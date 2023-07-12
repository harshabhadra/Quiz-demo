package com.quiz.app.network.model
import com.google.gson.annotations.SerializedName


data class RegisterInput(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)