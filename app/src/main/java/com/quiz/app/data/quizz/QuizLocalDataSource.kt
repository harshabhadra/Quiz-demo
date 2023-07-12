package com.quiz.app.data.quizz

import com.quiz.app.database.dao.FreeQuizDao
import com.quiz.app.database.dao.UpcomingQuizDao
import com.quiz.app.database.model.asUiModel
import com.quiz.app.domain.FreeQuizUiModel
import com.quiz.app.domain.model.UpcomingQuizUiModel
import com.quiz.app.network.model.FreeQuiz
import com.quiz.app.network.model.QuizResult
import com.quiz.app.network.model.asEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

class QuizLocalDataSource(
    private val upcomingQuizDao: UpcomingQuizDao,
    private val freeQuizDao: FreeQuizDao,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getUpcomingQuizzes(): Flow<Result<List<UpcomingQuizUiModel>>> {
        return withContext(coroutineDispatcher) {
            upcomingQuizDao.getUpcomingQuiz()
                .transform {
                    emit(Result.success(it.asUiModel().toList()))
                }.catch {
                    emit(Result.failure(Throwable("")))
                }
        }
    }

    suspend fun addUpcomingQuizzes(quizList: List<QuizResult>) = withContext(coroutineDispatcher) {
        upcomingQuizDao.insertUpcomingQuiz(*quizList.asEntity())
    }

    suspend fun updateUpcomingQuizzes(quizList: List<QuizResult>) =
        withContext(coroutineDispatcher) {
            upcomingQuizDao.updateUpcomingQuiz(*quizList.asEntity())
        }

    suspend fun deleteUpcomingQuizzes() = withContext(coroutineDispatcher) {
        upcomingQuizDao.deleteUpcomingQuiz()
    }

    //----------------------------------------- Free Quizzes --------------------------------------------------

    suspend fun getFreeQuizzes(): Flow<Result<List<FreeQuizUiModel>>> {
        return withContext(coroutineDispatcher) {
            freeQuizDao.geFreeQuizzes()
                .transform {
                    emit(Result.success(it.asUiModel().toList()))
                }.catch {
                    emit(Result.failure(Throwable("")))
                }
        }
    }

    suspend fun addFreeQuizzes(quizList: List<FreeQuiz>) = withContext(coroutineDispatcher) {
        freeQuizDao.insertFreeQuizzes(*quizList.asEntity())
    }

    suspend fun updateFreeQuizzes(quizList: List<FreeQuiz>) =
        withContext(coroutineDispatcher) {
            freeQuizDao.updateFreeQuizzes(*quizList.asEntity())
        }

    suspend fun deleteFreeQuizzes() = withContext(coroutineDispatcher) {
        freeQuizDao.deleteFreeQuizzes()
    }
}