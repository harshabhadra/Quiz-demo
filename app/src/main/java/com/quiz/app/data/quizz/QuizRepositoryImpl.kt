package com.quiz.app.data.quizz

import android.util.Log
import com.quiz.app.domain.FreeQuizUiModel
import com.quiz.app.domain.model.UpcomingQuizUiModel
import com.quiz.app.network.model.QuizDetailsResponse
import com.quiz.app.network.model.QuizListResponse
import com.quiz.app.network.model.QuizOverviewResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex

interface QuizRepository {

    suspend fun getUpComingQuizzes(): Result<Flow<List<UpcomingQuizUiModel>>>

    suspend fun getQuizOverView(): Result<QuizOverviewResponse>
    suspend fun getQuizDetails(id: String): Result<QuizDetailsResponse>
}

class QuizRepositoryImpl(
    private val quizRemoteDataSource: QuizRemoteDataSource,
    private val quizLocalDataSource: QuizLocalDataSource
) {
    companion object {
        private const val TAG = "QuizRepository"
    }

    private val latestNewsMutex = Mutex()
    var upcomingQuizUiModelFlow:Flow<Result<List<UpcomingQuizUiModel>>>? = null

    suspend fun refreshUpcomingQuizzes() {
        val networkQuizzes = quizRemoteDataSource.getUpcomingQuiz()
        if (networkQuizzes.isSuccess) {
            val quizData: QuizListResponse? = networkQuizzes.getOrElse {
                Log.e(TAG, "failed to get upcoming quiz from remote data source: ${it.message}")
                null
            }
            quizData?.let {
                if (it.data.results.isNotEmpty()) {
                    quizLocalDataSource.addUpcomingQuizzes(it.data.results)
                }
            }
        }
    }

    suspend fun refreshFreeQuizzes() {
        val networkQuizzes = quizRemoteDataSource.getQuizOverview()
        if (networkQuizzes.isSuccess) {
            val freeQuizResponse: QuizOverviewResponse? = networkQuizzes.getOrElse {
                Log.e(TAG, "failed to get free quiz from remote data source: ${it.message}")
                null
            }
            freeQuizResponse?.let {
                if (it.data.free.isNotEmpty()) {
                    quizLocalDataSource.addFreeQuizzes(it.data.free)
                }
            }
        }
    }

    suspend fun getUpcomingQuizList(): Flow<Result<List<UpcomingQuizUiModel>>> = quizLocalDataSource.getUpcomingQuizzes()

    suspend fun getFreeQuizList():Flow<Result<List<FreeQuizUiModel>>> = quizLocalDataSource.getFreeQuizzes()

    suspend fun getQuizOverview(): Result<QuizOverviewResponse> {
        return quizRemoteDataSource.getQuizOverview()
    }

    suspend fun getQuizDetails(id: String): Result<QuizDetailsResponse> {
        return quizRemoteDataSource.getQuizDetails(id)
    }
}