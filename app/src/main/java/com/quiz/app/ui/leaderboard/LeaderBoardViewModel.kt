package com.quiz.app.ui.leaderboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quiz.app.data.leaderBoard.LeaderBoardRepository
import com.quiz.app.network.ApiService
import com.quiz.app.network.model.NetworkGameSummary
import com.quiz.app.network.model.NetworkLeaderBoardResponse
import com.quiz.app.utils.NetworkUtils
import com.quiz.app.utils.SingleLiveEvent
import com.quiz.app.utils.SocketManager
import kotlinx.coroutines.launch

class LeaderBoardViewModel(
    private val apiService: ApiService,
    private val repository: LeaderBoardRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LeaderboardViewModel"
    }

    private val socketManager: SocketManager = SocketManager()

    private var _leaderBoardLiveData = SingleLiveEvent<NetworkLeaderBoardResponse>()
    val leaderBoardLiveData: LiveData<NetworkLeaderBoardResponse> = _leaderBoardLiveData

    private val calculationStartEndLiveData = MutableLiveData<String>()

    private var _gameSummaryMutableLiveData = SingleLiveEvent<NetworkGameSummary>()
    val gameSummaryLiveData: LiveData<NetworkGameSummary> = _gameSummaryMutableLiveData

    val participantsLiveData = repository.participantsList
    val pageInfoLiveData = repository.pageInfo

    init {
        viewModelScope.launch {
            repository.deleteAllParticipants()
        }
        socketManager.setEventListener(
            NetworkUtils.SOCKET_CALCULATION_END,
            object : SocketManager.EventListener {
                override fun onEventReceived(args: Array<Any>) {
                    val message = args[0].toString()
                    calculationStartEndLiveData.postValue(NetworkUtils.SOCKET_CALCULATION_END)
                    Log.e(TAG, "calculation end from socket: $message")
                }
            })
    }

    fun getCalculationLiveData(): LiveData<String> {
        return calculationStartEndLiveData
    }

    fun getGameSummary(quizId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getGameSummary(quizId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.e(TAG, "game summary response successful: $it")
                        _gameSummaryMutableLiveData.value = it
                    } ?: let {
                        Log.e(TAG, "game summary response body is null")
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "game summary response is unsuccessful: $error")
                }
            } catch (e: Exception) {
                Log.e(TAG, "failed to get game summary: ${e.message}")
            }
        }
    }

    fun getLeaderBoardData(quizId: String, queryDetails: Map<String, Any>) {

        viewModelScope.launch {
//            try {
//                Log.e(TAG, "getting leaderboard")
//                val response = apiService.getLeaderBoard(quizId, queryDetails)
//                if (response.isSuccessful) {
//                    response.body()?.let {
//                        Log.e(TAG, "leaderboard response: $it")
//                        _leaderBoardLiveData.value = it
//                    } ?: Log.e(TAG, "leaderboard body is null")
//                } else {
//                    val error = response.errorBody()?.string()
//                    Log.e(TAG, "leaderboard response unsuccessful: $error")
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Failed to get leaderboard: ${e.message}")
//            }
            repository.getLeaderBoard(quizId, queryDetails)
        }
    }
}