package com.quiz.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quiz.app.network.ApiService
import kotlinx.coroutines.launch

class ProfileViewModel(private val apiService: ApiService) : ViewModel() {

    fun logout(refreshToken: String) {
        viewModelScope.launch {
            apiService.logout(refreshToken)
        }
    }
}