package com.quiz.app

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.quiz.app.data.auth.AuthRemoteDataSource
import com.quiz.app.data.auth.AuthRepository
import com.quiz.app.data.leaderBoard.LeaderBoardRemoteDataSource
import com.quiz.app.data.leaderBoard.LeaderBoardRepository
import com.quiz.app.data.quizz.QuizLocalDataSource
import com.quiz.app.data.quizz.QuizRemoteDataSource
import com.quiz.app.data.quizz.QuizRepositoryImpl
import com.quiz.app.database.MoxieDatabase
import com.quiz.app.network.ApiClient
import com.quiz.app.network.ApiService
import com.quiz.app.utils.NetworkConnectivityObserver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

//object ServiceLocator {
//
//    var authRepository: AuthRepository? = null
//    var quizRepositoryImpl: QuizRepositoryImpl? = null
//    var leaderBoardRepository: LeaderBoardRepository? = null
//    private var database: MoxieDatabase? = null
//
//    fun provideViewModelFactory(
//        context: Context,
//        apiService: ApiService,
//        coroutineDispatcher: CoroutineDispatcher=Dispatchers.IO
//    ): ViewModelProvider.NewInstanceFactory {
//        return ViewModelFactory(context, apiService,coroutineDispatcher)
//    }
//
//    private fun createDataBase(context: Context): MoxieDatabase {
//        return database ?: MoxieDatabase.getDatabase(context)
//    }
//
//    fun provideApiService(): ApiService {
//        return ApiClient.getApiClient().create(ApiService::class.java)
//    }
//
//    fun provideAuthRepository(context: Context, apiService: ApiService): AuthRepository {
//
//        synchronized(this) {
//            return authRepository ?: createAuthRepository(context, apiService)
//        }
//    }
//
//
//    fun provideQuizRepository(apiService: ApiService,context: Context,coroutineDispatcher: CoroutineDispatcher): QuizRepositoryImpl {
//        synchronized(this) {
//            return quizRepositoryImpl ?: createQuizRepository(apiService,context, coroutineDispatcher)
//        }
//    }
//
//    fun provideLeaderBoardRepository(
//        context: Context,
//        apiService: ApiService,
//    ): LeaderBoardRepository {
//        synchronized(this) {
//            return leaderBoardRepository ?: createLeaderBoardRepository(
//                apiService,
//                context
//            )
//        }
//    }
//
//    private fun createAuthRepository(context: Context, apiService: ApiService): AuthRepository {
//        return AuthRepository(
//            AuthRemoteDataSource(apiService)
//        )
//    }
//
//    private fun createQuizRepository(
//        apiService: ApiService,
//        context: Context,
//        coroutineDispatcher: CoroutineDispatcher
//    ): QuizRepositoryImpl {
//        return QuizRepositoryImpl(
//            QuizRemoteDataSource(apiService),
//            createQuizLocalDataSource(context, coroutineDispatcher)
//        )
//    }
//
//    private fun createLeaderBoardRepository(
//        apiService: ApiService,
//        context: Context
//    ): LeaderBoardRepository {
//        return LeaderBoardRepository(createDataBase(context), apiService)
//    }
//
//    private fun createQuizLocalDataSource(
//        context: Context,
//        coroutineDispatcher: CoroutineDispatcher
//    ): QuizLocalDataSource {
//        val database = database ?: createDataBase(context)
//        return QuizLocalDataSource(database.upcomingQuizDao,database.freeQuizDao, coroutineDispatcher)
//    }
//}

object ServiceLocator {
    private lateinit var authRepository: AuthRepository
    private lateinit var quizRepositoryImpl: QuizRepositoryImpl
    private lateinit var leaderBoardRepository: LeaderBoardRepository
    private var database: MoxieDatabase? = null

    fun provideViewModelFactory(
        context: Context,
        apiService: ApiService,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): ViewModelProvider.NewInstanceFactory {
        return ViewModelFactory(context, apiService, coroutineDispatcher)
    }

    private fun createDataBase(context: Context): MoxieDatabase {
        return database.takeIf { it != null } ?: MoxieDatabase.getDatabase(context).also {
            database = it
        }
    }

    fun provideApiService(): ApiService {
        return ApiClient.getApiClient().create(ApiService::class.java)
    }

    fun provideNetworkConnectivityObserver(context: Context):NetworkConnectivityObserver{
        return NetworkConnectivityObserver(context)
    }

    fun provideAuthRepository(context: Context, apiService: ApiService): AuthRepository {
        synchronized(this) {
            if (!::authRepository.isInitialized) {
                authRepository = createAuthRepository(context,apiService)
            }
        }
        return authRepository
    }

    fun provideQuizRepository(
        apiService: ApiService,
        context: Context,
        coroutineDispatcher: CoroutineDispatcher
    ): QuizRepositoryImpl {
        synchronized(this) {
            if (!::quizRepositoryImpl.isInitialized) {
                quizRepositoryImpl = createQuizRepository(apiService, context, coroutineDispatcher)
            }
        }
        return quizRepositoryImpl
    }

    fun provideLeaderBoardRepository(
        context: Context,
        apiService: ApiService,
        coroutineDispatcher: CoroutineDispatcher
    ): LeaderBoardRepository {
        synchronized(this) {
            if (!::leaderBoardRepository.isInitialized) {
                leaderBoardRepository = createLeaderBoardRepository(apiService, context, coroutineDispatcher)
            }
        }
        return leaderBoardRepository
    }

    private fun createAuthRepository(context: Context, apiService: ApiService): AuthRepository {
        return AuthRepository(AuthRemoteDataSource(apiService))
    }

    private fun createQuizRepository(
        apiService: ApiService,
        context: Context,
        coroutineDispatcher: CoroutineDispatcher
    ): QuizRepositoryImpl {
        return QuizRepositoryImpl(
            QuizRemoteDataSource(apiService),
            createQuizLocalDataSource(context, coroutineDispatcher)
        )
    }

    private fun createLeaderBoardRepository(
        apiService: ApiService,
        context: Context,
        coroutineDispatcher: CoroutineDispatcher
    ): LeaderBoardRepository {
        return LeaderBoardRepository(createDataBase(context),
            createLeaderBoardRemoteDataSource(apiService,coroutineDispatcher) )
    }

    private fun createQuizLocalDataSource(
        context: Context,
        coroutineDispatcher: CoroutineDispatcher
    ): QuizLocalDataSource {
        val database = createDataBase(context)
        return QuizLocalDataSource(
            database.upcomingQuizDao,
            database.freeQuizDao,
            coroutineDispatcher
        )
    }

    private fun createLeaderBoardRemoteDataSource(apiService: ApiService,coroutineDispatcher: CoroutineDispatcher):LeaderBoardRemoteDataSource{
        return LeaderBoardRemoteDataSource(apiService, coroutineDispatcher)
    }
}
