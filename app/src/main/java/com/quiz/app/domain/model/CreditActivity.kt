package com.quiz.app.domain.model

data class CreditActivity(
    val date: String,
    val creditList: List<CreditData>
)

data class CreditData(
    val title: String,
    val date: String,
    val category: String,
    val credit: Int,
    val amount: Double
)