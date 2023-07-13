package com.quiz.app.ui.profile.pastQuizzes

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quiz.app.R
import com.quiz.app.databinding.FragmentPastQuizzesBinding
import com.quiz.app.domain.model.PastQuiz

class PastQuizzesFragment : Fragment() {

    companion object {
        fun newInstance() = PastQuizzesFragment()
    }
    private val dataList = listOf(
        PastQuiz("https://example.com/image1.jpg", "2022-05-01", 1),
        PastQuiz("https://example.com/image2.jpg", "2022-05-02", 2),
        PastQuiz("https://example.com/image3.jpg", "2022-05-03", 3),
        // Add more items as needed
    )
    private lateinit var viewModel: PastQuizzesViewModel
    private lateinit var binding:FragmentPastQuizzesBinding
    private lateinit var pastQuizListAdapter: PastQuizListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPastQuizzesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PastQuizzesViewModel::class.java)
        setUpAdapter()
    }

    private fun setUpAdapter() {
        pastQuizListAdapter = PastQuizListAdapter()
        binding.pastQuizRecyclerview.adapter = pastQuizListAdapter
        pastQuizListAdapter.submitList(dataList)
    }

}