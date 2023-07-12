package com.quiz.app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.quiz.app.MoxieApplication
import com.quiz.app.MoxieApplication.Companion.session
import com.quiz.app.R
import com.quiz.app.ui.auth.AuthActivity
import com.quiz.app.utils.SessionManager


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "SplashActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)

        val account = GoogleSignIn.getLastSignedInAccount(this)

        val accessToken = AccessToken.getCurrentAccessToken()
        Log.e(TAG,"access token: $accessToken")
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        Handler(Looper.getMainLooper()).postDelayed({
            val isLogin = session(this).getPrefBool(SessionManager.IS_LOGIN)
            val isGoogleLogin = session(this).getPrefBool(SessionManager.IS_GOOGLE_LOG_IN)
            val isFacebookLogin = session(this).getPrefBool(SessionManager.IS_FACEBOOK_LOGIN)
            startActivity(
                Intent(
                    this,
                    if (isLogin || (isGoogleLogin && account != null) || (isFacebookLogin && isLoggedIn)) MainActivity::class.java else AuthActivity::class.java
                )
            )
            finish()
        }, 3000)
    }
}