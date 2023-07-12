package com.quiz.app.utils

object NetworkUtils {

    const val VOL = "v1"
    const val QUIZ = "quizes"
    const val REGISTER_USER = "$VOL/auth/register"
    const val LOGIN_USER = "$VOL/auth/login"
    const val LOGOUT_USER = "$VOL/auth/logout"
    const val QUIZ_LIST = "$VOL/quizes"
    const val QUIZ_DETAILS = "$VOL/quizes"
    const val QUIZ_OVERVIEW = "$VOL/quizes/overview"
    const val GET_RTC_TOKEN = "$VOL/agora/rtc"
    const val CREATE_QUIZ_REMINDER = "$VOL/quiz_reminders"
    const val SUBMIT_VOTE = "$VOL/quizes"
    const val LEADERBOARD = "leaderboard"
    const val GAME_SUMMARY = "user/summary"

    const val SOCKET_GET_QUESTIONS = "user_quiz_live_question"
    const val SOCKET_GET_OPTIONS = "user_quiz_live_question_options"
    const val SOCKET_VIEWER_JOIN_LIVE = "user_join_live_quiz"
    const val SOCKET_VIEWER_LEAVE_LIVE = "user_leave_live_quiz"
    const val SOCKET_SUBMIT_ANSWER = "user_submit_live_quiz_answer"
    const val SOCKET_GET_LIVE_ANSWERS = "user_quiz_live_question_answer"
    const val SOCKET_QUESTION_END = "user_quiz_live_question_end"
    const val SOCKET_LIVE_STREAM_STATE_CHANGE = "user_quiz_live_change"
    const val SOCKET_CALCULATION_START = "user_quiz_live_calculation_start"
    const val SOCKET_CALCULATION_END = "user_quiz_live_calculation_end"

    fun createException(error: String?) = Exception(error ?: "")
}