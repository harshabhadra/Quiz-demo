package com.quiz.app.ui.quiz.vote

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quiz.app.R
import com.quiz.app.ServiceLocator
import com.quiz.app.databinding.FragmentVotingBinding
import com.quiz.app.network.apiService
import com.quiz.app.network.model.QuizDetailsData
import com.quiz.app.network.model.VotingCategoryItemModel
import com.quiz.app.ui.quiz.details.QuizDetailsViewModel

class VotingFragment : Fragment(), VotingCategoryAdapter.OnVotingItemClickListener {

    companion object {
        fun newInstance() = VotingFragment()
        private const val TAG = "VotingFragment"
    }

    private lateinit var binding: FragmentVotingBinding
    private lateinit var viewModel: QuizDetailsViewModel
    private lateinit var votingCategoryAdapter: VotingCategoryAdapter
    private var id: String = ""
    private var isVoted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVotingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModelFactory = ServiceLocator.provideViewModelFactory(requireContext(), apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[QuizDetailsViewModel::class.java]
        votingCategoryAdapter = VotingCategoryAdapter(this)
        binding.recyclerView.adapter = votingCategoryAdapter

        id = requireArguments().getString("quiz_id") ?: ""
        Log.e(TAG, "quiz id: $id")
        id.let {
            viewModel.getQuizDetails(id)
        }
        registerObserver()
        setClicks()
    }

    private fun setClicks() {
        binding.voteSubmitButton.setOnClickListener {
            if (isVoted) findNavController().navigateUp() else Toast.makeText(
                requireContext(),
                "Please select a category to vote",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun registerObserver() {
        viewModel.quizDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let { result: Result<QuizDetailsData?> ->

                if (result.isSuccess) {
                    val response = result.getOrNull()
                    response?.let { quizData ->
                        populateUi(quizData)
                    }
                } else Toast.makeText(
                    requireContext(),
                    "Failed to retrieve quiz",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    private fun populateUi(quizResult: QuizDetailsData) {
        val votingCategories = quizResult.votingCategoryItemModel
        isVoted = quizResult.has_voted
        votingCategoryAdapter.submitVoteDetails(quizResult.total_votes, quizResult.has_voted)
        votingCategoryAdapter.submitList(votingCategories)
        binding.voteSubmitButton.text =
            if (quizResult.has_voted) "Close" else getString(R.string.submit_vote)
        binding.voteLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onVoteItemClick(votingCategoryItemModel: VotingCategoryItemModel, position:Int) {
        binding.voteLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        viewModel.submitVote(id, votingCategoryItemModel.id!!)
    }
}