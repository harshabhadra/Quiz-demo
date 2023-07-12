package com.quiz.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quiz.app.network.ApiService
import com.quiz.app.ui.auth.login.LoginViewModel
import com.quiz.app.ui.auth.signup.SignUpViewModel
import com.quiz.app.ui.home.HomeViewModel
import com.quiz.app.ui.profile.ProfileViewModel
import com.quiz.app.ui.quiz.details.QuizDetailsViewModel
import com.quiz.app.ui.viewer.ViewerViewModel
import com.quiz.app.ui.leaderboard.LeaderBoardViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ViewModelFactory(
    private val context: Context,
    private val apiService: ApiService,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(SignUpViewModel::class.java) -> {
                    SignUpViewModel(ServiceLocator.provideAuthRepository(context, apiService))
                }

                isAssignableFrom(LoginViewModel::class.java) -> {
                    LoginViewModel(ServiceLocator.provideAuthRepository(context, apiService))
                }

                isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(
                        ServiceLocator.provideQuizRepository(
                            apiService,
                            context,
                            coroutineDispatcher
                        )
                    )
                }

                isAssignableFrom(QuizDetailsViewModel::class.java) -> {
                    QuizDetailsViewModel(
                        apiService,
                        ServiceLocator.provideQuizRepository(
                            apiService,
                            context,
                            coroutineDispatcher
                        )
                    )
                }

                isAssignableFrom(ProfileViewModel::class.java) -> {
                    ProfileViewModel(apiService)
                }

                isAssignableFrom(ViewerViewModel::class.java) -> {
                    ViewerViewModel(apiService)
                }

                isAssignableFrom(LeaderBoardViewModel::class.java) -> {
                    LeaderBoardViewModel(
                        apiService,
                        ServiceLocator.provideLeaderBoardRepository(context, apiService, coroutineDispatcher)
                    )
                }

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }
}