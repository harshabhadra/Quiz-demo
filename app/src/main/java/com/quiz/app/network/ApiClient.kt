package com.quiz.app.network

import com.quiz.app.MoxieApplication
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
//http://192.168.1.38:3000/
const val BASE_URL = "http://192.168.1.38:3000/"//http://quizz.eba-r5ciifhq.ap-south-1.elasticbeanstalk.com/

object ApiClient {

    fun getApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private val okHttpClient: OkHttpClient
        get() {
            try {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                val httpClient = OkHttpClient.Builder()
                httpClient.interceptors().add(httpLoggingInterceptor)
                httpClient.interceptors().add(MoxieInterceptor(MoxieApplication.getAppContext()))
                httpClient.readTimeout(180, TimeUnit.SECONDS)
                httpClient.connectTimeout(180, TimeUnit.SECONDS)
                return httpClient.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
}