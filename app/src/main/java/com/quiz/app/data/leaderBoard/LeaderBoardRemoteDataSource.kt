package com.quiz.app.data.leaderBoard

import android.util.Log
import com.quiz.app.data.model.Result
import com.quiz.app.network.ApiService
import com.quiz.app.network.model.NetworkLeaderBoardResult
import com.quiz.app.network.model.asEntity
import com.quiz.app.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LeaderBoardRemoteDataSource(
    private val apiService: ApiService,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    companion object {
        private const val TAG = "LeaderBoardRemoteDatasource"
    }

    suspend fun getLeaderBoardData(
        quizId: String,
        queryDetails: Map<String, Any>
    ): Result<Pair<List<NetworkLeaderBoardResult>, Pair<Int, Int>>> {
        return withContext(coroutineDispatcher) {
            try {
                Log.e(TAG, "getting leaderboard")
                val response = apiService.getLeaderBoard(quizId, queryDetails)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "leaderboard response: $it")
                        val participants = it.data.data.data
                        val pagePair = Pair(it.data.data.page, it.data.data.total_pages)
                        Result.Success(Pair(participants, pagePair))
                    } ?: let {
                        Log.e(TAG, "leaderboard body is null")
                        Result.Error(NetworkUtils.createException("null body"))
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "leaderboard response unsuccessful: $error")
                    Result.Error(NetworkUtils.createException(error))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get leaderboard: ${e.message}")
                Result.Error(e)
            }
        }
    }
}