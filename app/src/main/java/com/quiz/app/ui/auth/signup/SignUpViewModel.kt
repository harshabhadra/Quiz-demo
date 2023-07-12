package com.quiz.app.ui.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quiz.app.data.auth.AuthRepository
import com.quiz.app.network.model.RegisterInput
import com.quiz.app.network.model.RegisterResponse
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) :ViewModel(){

    companion object{
        private const val TAG = "SignUpViewModel"
    }

    private var _singUpMutableLiveData = MutableLiveData<Pair<Result<RegisterResponse>,Map<String,String>>>()
    val signUpLiveData:LiveData<Pair<Result<RegisterResponse>,Map<String,String>>> = _singUpMutableLiveData

    fun registerUser(requestBody: Map<String,String>) {
        viewModelScope.launch {
            val response = authRepository.registerUser(requestBody)
            _singUpMutableLiveData.value = Pair(response,requestBody)
        }
    }
}