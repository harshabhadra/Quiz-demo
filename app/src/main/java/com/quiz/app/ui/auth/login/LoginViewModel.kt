package com.quiz.app.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quiz.app.data.auth.AuthRepository
import com.quiz.app.network.model.NetworkLoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository):ViewModel(){

    companion object{
        private const val TAG = "LoginViewModel"
    }

    private var _loginMutableLiveData = MutableLiveData<Result<NetworkLoginResponse>>()
    val loginLiveData:LiveData<Result<NetworkLoginResponse>> = _loginMutableLiveData

     fun loginUser(requestBody:Map<String,String>){
         viewModelScope.launch {
             _loginMutableLiveData.value = authRepository.loginUser(requestBody)
         }
     }
}