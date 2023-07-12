package com.quiz.app.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quiz.app.R
import com.quiz.app.utils.FragConst
import com.quiz.app.utils.showFragment


class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        showFragment(FragConst.START_PAGE, addtoBack = false)
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.getOnBackPressedDispatcher().onBackPressed()
        } else {
            finish()
        }
    }
}