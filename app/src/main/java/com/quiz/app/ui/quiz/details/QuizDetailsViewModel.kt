package com.quiz.app.ui.quiz.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quiz.app.data.quizz.QuizRepositoryImpl
import com.quiz.app.network.ApiService
import com.quiz.app.network.model.QuizDetailsData
import com.quiz.app.network.model.NetworkReminderData
import com.quiz.app.network.model.RtcTokenResponse
import com.quiz.app.network.model.VoteResponse
import com.quiz.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class QuizDetailsViewModel(
    private val apiService: ApiService,
    private val quizRepositoryImpl: QuizRepositoryImpl
) : ViewModel() {

    companion object {
        private const val TAG = "QuizDetailsViewModel"
    }

    private var _rtcTokenMutableLiveData = SingleLiveEvent<RtcTokenResponse>()
    val rtcTokenLiveData: LiveData<RtcTokenResponse> = _rtcTokenMutableLiveData

    private var _reminderMutableLiveData = SingleLiveEvent<NetworkReminderData>()
    val reminderLiveData: LiveData<NetworkReminderData> = _reminderMutableLiveData

    private var _quizDetailsMutableLiveData = SingleLiveEvent<Result<QuizDetailsData?>>()
    val quizDetailsLiveData: LiveData<Result<QuizDetailsData?>> = _quizDetailsMutableLiveData

    private var _voteMutableLiveData = SingleLiveEvent<VoteResponse>()
    val voteLiveData:LiveData<VoteResponse> = _voteMutableLiveData

    fun getQuizDetails(id: String) {
        viewModelScope.launch {
            val response = quizRepositoryImpl.getQuizDetails(id)
            if (response.isSuccess) {
                _quizDetailsMutableLiveData.value = Result.success(response.getOrNull()?.data)
            } else {
                _quizDetailsMutableLiveData.value =
                    Result.failure(Throwable(response.exceptionOrNull()))
            }
        }
    }

    fun getRtcToken(channelName: String, role: String, uid: Int) {

        Log.e(TAG, "input: $channelName: $channelName, role: $role, uid: $uid")
        viewModelScope.launch {
            try {
                Log.e(TAG, "getting rtc")
                val response = apiService.getRtcToken(channelName, role, "uid", uid)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "rtc token response: $it")
                        _rtcTokenMutableLiveData.value = it
                    } ?: Log.e(TAG, "rtc token body is null")
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "rtc token response unsuccessful: $error")
                }
            } catch (e: Exception) {

                Log.e(TAG, "Failed to get rtc token: ${e.message}")
            }
        }
    }

    fun createQuizReminder(requestBody: Map<String, String>) {
        viewModelScope.launch {
            try {
                val response = apiService.createQuizReminder(requestBody)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "create reminder response: $it")
                        _reminderMutableLiveData.value = it.data
                    } ?: Log.e(TAG, "create reminder body is null")
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "create reminder response unsuccessful: $error")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create reminder: ${e.message}")
            }
        }
    }

    fun submitVote(quizId: String, catId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.submitVote(quizId,catId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "create reminder response: $it")
                        _voteMutableLiveData.value = it
                        getQuizDetails(quizId)
                    } ?: Log.e(TAG, "create reminder body is null")
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "create reminder response unsuccessful: $error")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create reminder: ${e.message}")
            }
        }
    }
}