package com.quiz.app.ui.leaderboard.gamesummary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R
import com.quiz.app.domain.QuizAnswer
import com.quiz.app.ui.leaderboard.QuizAnswerAdapter

class QuizAnswerFragment(private val answers:List<QuizAnswer>) : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var quizAnswerAdapter: QuizAnswerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz_answer, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Set up the RecyclerView and adapter
        quizAnswerAdapter = QuizAnswerAdapter()
        recyclerView.adapter = quizAnswerAdapter

        // Create dummy data for the adapter
        val quizAnswerList = getDummyQuizAnswers()
        quizAnswerAdapter.submitList(answers)

        return view
    }

    private fun getDummyQuizAnswers(): List<QuizAnswer> {
        // Create and return a list of dummy QuizAnswer objects
        return listOf(
            QuizAnswer("Question 1", "Answer 1", true, "10 seconds"),
            QuizAnswer( "Question 2", "Answer 2", true, "15 seconds"),
            QuizAnswer( "Question 3", "Answer 3", false, ""),
            QuizAnswer( "Question 4", "Answer 4", true, "8 seconds")
        )
    }
}
