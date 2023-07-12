package com.quiz.app.data.quizz

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.quiz.app.network.ApiService
import com.quiz.app.network.model.QuizDetailsResponse
import com.quiz.app.network.model.QuizListResponse
import com.quiz.app.network.model.QuizOverviewResponse
import com.quiz.app.ui.quiz.details.QuizDetailsViewModel
import com.quiz.app.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizRemoteDataSource(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    companion object {
        private const val TAG = "QuizRemoteDataSource"
    }

    suspend fun getUpcomingQuiz(): Result<QuizListResponse> {
        return withContext(dispatcher) {
            try {
                val response = apiService.getUpcomingQuizList(true)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "quiz list response successful: $it")
                        Result.success(it)
                    } ?: let {
                        Log.e(TAG, "response body is null")
                        Result.failure(Throwable("quiz list response is null"))
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "quiz list response is unsuccessful: $error")
                    Result.failure(Throwable(error))
                }
            } catch (e: Exception) {
                Log.e(TAG, "failed to get quiz list: ${e.message}")
                Result.failure(Throwable(e.message))
            }
        }
    }

    suspend fun getQuizOverview(): Result<QuizOverviewResponse> {
        return withContext(dispatcher) {
            try {
                val response = apiService.getQuizOverview(true)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "quiz overview response successful: $it")
                        Result.success(it)
                    } ?: let {
                        Log.e(TAG, "quiz over view response body is null")
                        Result.failure(Throwable("quiz overview response is null"))
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "quiz overview response is unsuccessful: $error")
                    Result.failure(Throwable(error))
                }
            } catch (e: Exception) {
                Log.e(TAG, "failed to get quiz overview: ${e.message}")
                Result.failure(Throwable(e.message))
            }
        }
    }

    suspend fun getQuizDetails(id: String): Result<QuizDetailsResponse> {
        return withContext(dispatcher) {
            try {
                val response = apiService.getQuizDetails(id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "quiz details response successful: $it")
                        Result.success(it)
                    } ?: let {
                        Log.e(TAG, "response body is null")
                        Result.failure(Throwable("Quiz details response is null"))
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "quiz details response is unsuccessful: $error")
                    Result.failure(Throwable(error))
                }
            } catch (e: Exception) {
                Log.e(TAG, "failed to get quiz details: ${e.message}")
                Result.failure(Throwable(e.message))
            }
        }
    }

    suspend fun createQuizReminder(requestBody: Map<String, String>): com.quiz.app.data.model.Result<Boolean> {
        return withContext(dispatcher) {
            try {
                val response = apiService.createQuizReminder(requestBody)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "create reminder response: $it")
                    } ?: Log.e(TAG, "create reminder body is null")
                    com.quiz.app.data.model.Result.Success(response.body() != null)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "create reminder response unsuccessful: $error")
                    com.quiz.app.data.model.Result.Error(NetworkUtils.createException("null body"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create reminder: ${e.message}")
                com.quiz.app.data.model.Result.Error(e)
            }
        }
    }

    suspend fun submitQuizVote(
        quizId: String,
        catId: String
    ): com.quiz.app.data.model.Result<Boolean> {
        return withContext(dispatcher) {
            try {
                val response = apiService.submitVote(quizId, catId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "create reminder response: $it")
                    } ?: Log.e(TAG, "create reminder body is null")
                    com.quiz.app.data.model.Result.Success(response.body() != null)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "create reminder response unsuccessful: $error")
                    com.quiz.app.data.model.Result.Error(NetworkUtils.createException(error))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create reminder: ${e.message}")
                com.quiz.app.data.model.Result.Error(e)
            }
        }
    }
}
