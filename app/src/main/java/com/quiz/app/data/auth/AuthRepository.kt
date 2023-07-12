package com.quiz.app.data.auth

import com.quiz.app.network.model.NetworkLoginResponse
import com.quiz.app.network.model.RegisterResponse

class AuthRepository(
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    suspend fun registerUser(requestBody: Map<String,String>): Result<RegisterResponse> {
        return authRemoteDataSource.registerUser(requestBody)
    }

    suspend fun loginUser(requestBody:Map<String,String>):Result<NetworkLoginResponse>{
        return authRemoteDataSource.loginUser(requestBody)
    }
}