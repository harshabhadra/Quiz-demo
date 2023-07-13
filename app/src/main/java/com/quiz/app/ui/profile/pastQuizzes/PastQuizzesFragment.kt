package com.quiz.app.ui.profile.pastQuizzes

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quiz.app.R

class PastQuizzesFragment : Fragment() {

    companion object {
        fun newInstance() = PastQuizzesFragment()
    }

    private lateinit var viewModel: PastQuizzesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_past_quizzes, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PastQuizzesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}