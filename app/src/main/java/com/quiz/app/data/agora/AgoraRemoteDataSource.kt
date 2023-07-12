package com.quiz.app.data.agora

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.quiz.app.data.model.Result
import com.quiz.app.network.ApiService
import com.quiz.app.network.model.RtcTokenResponse
import com.quiz.app.ui.quiz.details.QuizDetailsViewModel
import com.quiz.app.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgoraRemoteDataSource(
    private val apiService: ApiService,
    private val coroutineDispatcher: CoroutineDispatcher
) {

    companion object {
        private const val TAG = "AgoraRemoteDataSource"
    }

    suspend fun getRtcToken(channelName: String, role: String, uid: Int): Result<RtcTokenResponse> {

        Log.e(TAG, "input: $channelName: $channelName, role: $role, uid: $uid")
        return withContext(coroutineDispatcher) {
            try {
                Log.e(TAG, "getting rtc")
                val response = apiService.getRtcToken(channelName, role, "uid", uid)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "rtc token response: $it")
                        Result.Success(it)
                    } ?: let {
                        Log.e(TAG, "rtc token body is null")
                        Result.Error(NetworkUtils.createException("empty body"))
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "rtc token response unsuccessful: $error")
                    Result.Error(NetworkUtils.createException(error))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get rtc token: ${e.message}")
                Result.Error(e)
            }
        }
    }
}