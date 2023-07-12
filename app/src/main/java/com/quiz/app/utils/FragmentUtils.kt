package com.quiz.app.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.quiz.app.R
import com.quiz.app.ui.auth.StartFragment
import com.quiz.app.ui.auth.login.LoginFragment
import com.quiz.app.ui.auth.signup.SignUpFragment
import com.quiz.app.ui.home.HomeFragment
import com.quiz.app.ui.quiz.QuizFragment
import com.quiz.app.ui.quiz.details.QuizDetailsFragment
import com.quiz.app.ui.leaderboard.LeaderBoardFragment

fun AppCompatActivity.showFragment(
    tag: String,
    bundle: Bundle? = null,
    addtoBack: Boolean = true,
    containerId: Int = R.id.start_container
) {
    try {
        supportFragmentManager.executePendingTransactions()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    val transaction = supportFragmentManager.beginTransaction()
    val newFragment = createNewFragmentForTag(tag)

    newFragment?.let {

        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            if (bundle != null) newFragment.arguments = bundle
            if (addtoBack) {
                transaction.replace(containerId, newFragment, tag).addToBackStack(tag)
                    .commit()
            } else {
                transaction.replace(containerId, newFragment, tag).addToBackStack(null)
                    .commit()
            }
        } else {
            transaction.replace(containerId, newFragment, tag).commit()
        }

    }
}

fun AppCompatActivity.removeFragment(tag: String){
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    fragment?.let {
        supportFragmentManager.beginTransaction().remove(fragment).commit()
    }
}

fun Fragment.showFragment(
    tag: String,
    bundle: Bundle? = null,
    addtoBack: Boolean = true,
    containerId: Int = R.id.start_container
) {
    try {
        requireActivity().supportFragmentManager.executePendingTransactions()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    val transaction = requireActivity().supportFragmentManager.beginTransaction()
    val newFragment = createNewFragmentForTag(tag)

    newFragment?.let {

        if (requireActivity().supportFragmentManager.findFragmentByTag(tag) == null) {
            if (bundle != null) newFragment.arguments = bundle
            if (addtoBack) {
                transaction.replace(containerId, newFragment, tag).addToBackStack(tag)
                    .commit()
            } else {
                transaction.replace(containerId, newFragment, tag).addToBackStack(null)
                    .commit()
            }
        } else {
            transaction.replace(containerId, newFragment, tag).commit()
        }

    }
}

private fun createNewFragmentForTag(tag: String): Fragment? {
    when (tag) {
        FragConst.START_PAGE -> return StartFragment.newInstance()
        FragConst.SIGN_UP -> return SignUpFragment.newInstance()
        FragConst.LOG_IN -> return LoginFragment.newInstance()
        FragConst.QUIZ -> return QuizFragment.newInstance()
        FragConst.HOME -> return HomeFragment.newInstance()
        FragConst.QUIZ_DETAILS -> return QuizDetailsFragment.newInstance()
        FragConst.LEADER_BOARD -> return LeaderBoardFragment.newInstance()
    }
    return null
}

object FragConst {
    const val START_PAGE = "start_page"
    const val SIGN_UP = "sign_up"
    const val LOG_IN = "log_in"
    const val QUIZ = "quiz"
    const val HOME = "home"
    const val QUIZ_DETAILS = "quiz_details"
    const val LEADER_BOARD = "leaderboard"
}