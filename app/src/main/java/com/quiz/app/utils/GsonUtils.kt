package com.quiz.app.utils

import android.util.Log
import com.google.gson.Gson
import com.quiz.app.network.model.NetworkAnswers
import com.quiz.app.network.model.Question
import com.quiz.app.network.model.StreamState
import org.json.JSONException

object GsonUtils {
    val gson = Gson()
    const val TAG = "GsonUtils"

    fun getQuestionData(response: String): Question? {
        return try {
            val quizQuestion = gson.fromJson(response, Question::class.java)
            quizQuestion
        } catch (e: Exception) {
            Log.e(TAG, "Failed to convert data: ${e.message}")
            null
        }
    }

    fun getAnswersData(response: String): NetworkAnswers? {
        return try {
            val networkAnswers = gson.fromJson(response, NetworkAnswers::class.java)
            networkAnswers
        } catch (e: Exception) {
            Log.e(TAG, "failed to parse votes json: ${e.message}")
            null
        }
    }

    fun getStreamStateData(response: String):StreamState?{
       return try {
           val streamState = gson.fromJson(response, StreamState::class.java)
           streamState
       }catch (e:JSONException){
           Log.e(TAG,"failed to parse stream state json: ${e.message}")
           null
       }
    }
}