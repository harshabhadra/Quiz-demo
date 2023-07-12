package com.quiz.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quiz.app.data.quizz.QuizRepositoryImpl
import com.quiz.app.domain.FreeQuizUiModel
import com.quiz.app.domain.model.UpcomingQuizUiModel
import com.quiz.app.network.model.FreeQuiz
import com.quiz.app.network.model.QuizResult
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val quizRepositoryImpl: QuizRepositoryImpl) : ViewModel() {

    private var _upcomingQuizUiModelStateFlow = MutableStateFlow<Result<List<UpcomingQuizUiModel>>>(
        Result.success(
            emptyList()
        )
    )
    val upcomingQuizzesUiModel: StateFlow<Result<List<UpcomingQuizUiModel>>> = _upcomingQuizUiModelStateFlow

    private var _freeQuizStateFlow = MutableStateFlow<Result<List<FreeQuizUiModel>>>(
        Result.success(
            emptyList()
        )
    )
    val freeQuizzes: StateFlow<Result<List<FreeQuizUiModel>>> = _freeQuizStateFlow

    init {
        viewModelScope.launch {
            getUpComingQuizzes()
            getFreeQuizzes()
        }
    }

    private fun getUpComingQuizzes() {
        viewModelScope.launch {
            quizRepositoryImpl.getUpcomingQuizList().collectLatest {
                _upcomingQuizUiModelStateFlow.value = it
            }
        }
    }

    suspend fun refreshUpcomingQuizzes() = quizRepositoryImpl.refreshUpcomingQuizzes()
    suspend fun refreshFreeQuizzes() = quizRepositoryImpl.refreshFreeQuizzes()

    private fun getFreeQuizzes(){
        viewModelScope.launch {
            quizRepositoryImpl.getFreeQuizList().collectLatest {
                _freeQuizStateFlow.value = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}