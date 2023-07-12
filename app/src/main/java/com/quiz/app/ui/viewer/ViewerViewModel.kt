package com.quiz.app.ui.viewer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quiz.app.network.ApiService
import com.quiz.app.network.model.*
import com.quiz.app.utils.GsonUtils.getAnswersData
import com.quiz.app.utils.GsonUtils.getQuestionData
import com.quiz.app.utils.GsonUtils.getStreamStateData
import com.quiz.app.utils.NetworkUtils
import com.quiz.app.utils.SingleLiveEvent
import com.quiz.app.utils.SocketManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class ViewerViewModel(private val apiService: ApiService) : ViewModel(), SocketManager.ConnectionListener {

    private val socketManager: SocketManager = SocketManager()

    private val questionLiveData = MutableLiveData<Question?>()
    private val questionEndLiveData = MutableLiveData<Question?>()
    private val answerLiveData = MutableLiveData<List<NetworkTotalAnswer>>()
    private val streamStateLiveData = MutableLiveData<StreamState?>()
    private val calculationStartEndLiveData = MutableLiveData<String>()
    init {
        socketManager.setConnectionListener(this)
        socketManager.setEventListener(
            NetworkUtils.SOCKET_GET_QUESTIONS,
            object : SocketManager.EventListener {
                override fun onEventReceived(args: Array<Any>) {
                    val response = args[0].toString()
                    Log.e(TAG, "question start from socket: $response")
                    val questionData = getQuestionData(response)
                    questionLiveData.postValue(questionData)
                }
            })

        socketManager.setEventListener(
            NetworkUtils.SOCKET_GET_OPTIONS,
            object : SocketManager.EventListener {
                override fun onEventReceived(args: Array<Any>) {
                    val response = args[0].toString()
                    Log.e(TAG, "question options from socket: $response")
                    val questionData = getQuestionData(response)
                    questionLiveData.postValue(questionData)
                }
            })

        socketManager.setEventListener(NetworkUtils.SOCKET_GET_LIVE_ANSWERS,
            object : SocketManager.EventListener {
                override fun onEventReceived(args: Array<Any>) {
                    Log.e(TAG, "answer received in viewmodel")
                    val response = args[0].toString()
                    Log.e(TAG, "live answers from socket: $response")
                    val answers = getAnswersData(response)
                    answerLiveData.postValue(answers?.networkTotalAnswers)
                }
            })

        socketManager.setEventListener(NetworkUtils.SOCKET_QUESTION_END,
            object : SocketManager.EventListener {
                override fun onEventReceived(args: Array<Any>) {
                    Log.e(TAG, "answer received in viewmodel")
                    val response = args[0].toString()
                    Log.e(TAG, "question end response: $response")
                    val questionData = getQuestionData(response)
                    questionEndLiveData.postValue(questionData)
                }
            })

        socketManager.setEventListener(NetworkUtils.SOCKET_LIVE_STREAM_STATE_CHANGE,
            object : SocketManager.EventListener {
                override fun onEventReceived(args: Array<Any>) {
                    Log.e(TAG, "live stream state change from socket: $args")
                    val message = args[0].toString()
                    val streamStateData = getStreamStateData(message)
                    streamStateLiveData.postValue(streamStateData)
                    Log.e(TAG, "live stream state change socket: $message")
                }
            })

        socketManager.setEventListener(NetworkUtils.SOCKET_CALCULATION_START,
            object : SocketManager.EventListener {
                override fun onEventReceived(args: Array<Any>) {
                    val message = args[0].toString()
                    calculationStartEndLiveData.postValue(NetworkUtils.SOCKET_CALCULATION_START)
                    Log.e(TAG, "calculation start from socket: $message")
                }
            })
        socketManager.setEventListener(
            NetworkUtils.SOCKET_CALCULATION_END,
            object : SocketManager.EventListener {
                override fun onEventReceived(args: Array<Any>) {
                    val message = args[0].toString()
                    calculationStartEndLiveData.postValue(NetworkUtils.SOCKET_CALCULATION_END)
                    Log.e(TAG, "calculation end from socket: $message")
                }
            })


        // Connect to the Socket.IO server
        socketManager.connect()
    }

    fun deleteQuiz(){
        viewModelScope.launch {
            apiService.deleteQuizDetails()
        }
    }
    fun getSocketQuestionLiveData(): LiveData<Question?> {
        return questionLiveData
    }

    fun getSocketQuestionEndLiveData(): LiveData<Question?> {
        return questionEndLiveData
    }

    fun getSocketAnswerLiveData(): LiveData<List<NetworkTotalAnswer>> {
        return answerLiveData
    }

    fun getStreamStateLiveData():LiveData<StreamState?>{
        return streamStateLiveData
    }

    fun getCalculationLiveData():LiveData<String>{
        return calculationStartEndLiveData
    }


    fun sendMessage(message: JSONObject, channelName: String) {
        socketManager.sendMessage(message, channelName)
    }


    override fun onCleared() {
        socketManager.disconnect()
    }

    companion object {
        private const val TAG = "ViewerViewModel"
    }

    override fun onConnected() {
        Log.e(TAG, "socket connected")
    }

    override fun onDisconnected() {
        Log.e(TAG, "socket disconnected")
    }

    override fun onConnectError(args: Array<Any>) {
        Log.e(TAG, "error connecting socket: ${args[0]}")
    }
}