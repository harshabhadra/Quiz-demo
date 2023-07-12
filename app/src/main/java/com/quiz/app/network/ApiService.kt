package com.quiz.app.network

import com.quiz.app.network.model.*
import com.quiz.app.utils.NetworkUtils
import retrofit2.Response
import retrofit2.http.*

val apiService: ApiService = ApiClient.getApiClient().create(ApiService::class.java)

interface ApiService {

    @POST(NetworkUtils.REGISTER_USER)
    @FormUrlEncoded
    suspend fun registerUser(@FieldMap requestBody: Map<String, String>): Response<RegisterResponse>

    @POST(NetworkUtils.LOGIN_USER)
    suspend fun loginUser(@Body requestBody: Map<String, String>): Response<NetworkLoginResponse>

    @POST(NetworkUtils.LOGOUT_USER)
    @FormUrlEncoded
    suspend fun logout(@Field("refresh_token") refresh_token: String): Response<Unit>

    @GET(NetworkUtils.QUIZ_LIST)
    suspend fun getUpcomingQuizList(@Query("upcoming") isFree: Boolean): Response<QuizListResponse>

    @GET(NetworkUtils.QUIZ_OVERVIEW)
    suspend fun getQuizOverview(@Query("free") isFree: Boolean): Response<QuizOverviewResponse>

    @GET("${NetworkUtils.QUIZ_DETAILS}/{quiz_id}")
    suspend fun getQuizDetails(@Path("quiz_id") quizId: String): Response<QuizDetailsResponse>

    @GET("${NetworkUtils.GET_RTC_TOKEN}/{channel}/{role}/{token_type}/{uid}")
    suspend fun getRtcToken(
        @Path("channel") channel: String,
        @Path("role") role: String,
        @Path("token_type") tokenType: String,
        @Path("uid") uid: Int,
    ): Response<RtcTokenResponse>

    @POST(NetworkUtils.CREATE_QUIZ_REMINDER)
    @FormUrlEncoded
    suspend fun createQuizReminder(@FieldMap requestBody: Map<String, String>): Response<NetworkCreateQuizReminder>

    @POST("${NetworkUtils.SUBMIT_VOTE}/{quiz_id}/votes/category/{category_id}")
    suspend fun submitVote(
        @Path("quiz_id") quiz_id: String,
        @Path("category_id") catId: String
    ): Response<VoteResponse>

    @GET("${NetworkUtils.VOL}/${NetworkUtils.QUIZ}/{quiz_id}/${NetworkUtils.LEADERBOARD}")
    suspend fun getLeaderBoard(
        @Path("quiz_id") quizId: String,
        @QueryMap requestBody: @JvmSuppressWildcards Map<String, Any>
    ): Response<NetworkLeaderBoardResponse>

    @GET("${NetworkUtils.VOL}/${NetworkUtils.QUIZ}/{quiz_id}/${NetworkUtils.GAME_SUMMARY}")
    suspend fun getGameSummary(
        @Path("quiz_id")quizId: String
    ):Response<NetworkGameSummary>

    @DELETE("${NetworkUtils.VOL}/${NetworkUtils.QUIZ}/temp")
    suspend fun deleteQuizDetails()
}