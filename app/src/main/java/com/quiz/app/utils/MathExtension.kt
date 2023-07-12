package com.quiz.app.utils

import kotlin.random.Random

fun getRandomId(initial: String): String {
    val random = Random(1000)
    return "${initial}_${System.currentTimeMillis()}_${random.nextInt()}"
}