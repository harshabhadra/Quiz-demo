package com.quiz.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.quiz.app.MoxieApplication
import com.quiz.app.R
import com.quiz.app.utils.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userType = MoxieApplication.session(this).getPrefString(SessionManager.USER_TYPE) ?: ""
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(if (userType == "host") R.id.quizFragment else R.id.homeFragment)
        navHostFragment.navController.graph = graph
    }
}