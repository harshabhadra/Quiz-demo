package com.quiz.app.network

import android.content.Context
import android.content.Intent
import android.util.Log
import com.quiz.app.MoxieApplication.Companion.sessionManager
import com.quiz.app.ui.ErrorActivity
import com.quiz.app.ui.SplashActivity
import com.quiz.app.utils.SessionManager
import com.quiz.app.utils.getDateFromString
import com.quiz.app.utils.getTimeFromMillis
import okhttp3.Interceptor
import okhttp3.Response

class MoxieInterceptor(private val context: Context) : Interceptor {

    companion object {
        private const val TAG = "MoxieInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = sessionManager!!.getPrefString(SessionManager.ACCESS_TOKEN)
        Log.e(TAG, "request url: ${request.url}, body: ${request.body}")
        Log.e(TAG, "token: $token")
        val expireDate = sessionManager!!.getPrefString(SessionManager.ACCESS_TOKEN_TIME)
        val currentTime = getDateFromString(
            getTimeFromMillis(
                System.currentTimeMillis(),
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            )
        )
//        val expireTime = getDateFromString(expireDate!!)
//        Log.e(TAG, "current time: $currentTime, expire time: $expireTime")
        if (sessionManager!!.getPrefBool(SessionManager.IS_LOGIN)) {
            request = request.newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $token"
                )
                .build()
        }

        val response = chain.proceed(request)
        Log.e(TAG, "response code: ${response.code}, body: ${response.body}")
        if (response.code in 200..299) {
            if (request.url.toString().contains("/v1/auth/logout")) {
                sessionManager?.deleteSaveData()
                context.startActivity(
                    Intent(context, SplashActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            return response
        } else {
            context.startActivity(Intent(context, ErrorActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
        return response

    }
}
