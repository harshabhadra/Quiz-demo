package com.quiz.app.ui.leaderboard

import GameSummaryPagerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quiz.app.R
import com.quiz.app.ViewModelFactory
import com.quiz.app.database.model.asParticipantList
import com.quiz.app.databinding.FragmentLeaderBoardBinding
import com.quiz.app.domain.Participant
import com.quiz.app.domain.QuizAnswer
import com.quiz.app.network.apiService
import com.quiz.app.network.model.asQuizAnswerList
import com.quiz.app.ui.leaderboard.gamesummary.QuizAnswerFragment
import com.quiz.app.ui.leaderboard.gamesummary.SummaryFragment
import kotlinx.coroutines.Dispatchers

class LeaderBoardFragment : Fragment() {

    companion object {
        fun newInstance() = LeaderBoardFragment()
        private const val TAG = "LeaderBoardFragment"
    }

    private lateinit var viewModel: LeaderBoardViewModel
    private lateinit var binding: FragmentLeaderBoardBinding
    private lateinit var quizId: String
    private lateinit var leaderBoardAdapter: LeaderBoardAdapter
    private lateinit var gameSummaryPagerAdapter: GameSummaryPagerAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var currentPage = 0
    private var totalPages = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaderBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dispatcher = Dispatchers.IO
        val viewModelFactory = ViewModelFactory(requireContext(), apiService, dispatcher)
        viewModel = ViewModelProvider(this, viewModelFactory)[LeaderBoardViewModel::class.java]
        val bundle = this.requireArguments()
        bundle.let {
            quizId = it.getString("quiz_id") ?: ""
        }
        Log.e(TAG, "quiz id: $quizId")
        setUpAdapter()
        HashMap<String, Any>().apply {
            put("user_rank", true)
            viewModel.getLeaderBoardData(quizId, this)
        }
        viewModel.getGameSummary(quizId)
        showGameSummary()
        registerObservers()
        addScrollListener()
    }

    private fun addScrollListener() {
        binding.leaderBoardRecyclerview.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                Log.e(TAG, "dy: $dy, dx: $dx")
                Log.e(
                    TAG,
                    "visible item count: $visibleItemCount, totalItem: $totalItemCount, first item position: $firstVisibleItemPosition"
                )
                if (dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    Log.e(TAG, "end of list")
                    // End of list reached in the forward direction (e.g., scrolling down)
                    if (currentPage < totalPages) {
                        currentPage++
                        // Fetch new data for the next page and add it to the adapter
                        getLeaderBoard(currentPage)
                    }
                } else if (dy < 0 && firstVisibleItemPosition == 0) {
                    Log.e(TAG, "top of list")
                    // Beginning of list reached in the reverse direction (e.g., scrolling up)
                    if (currentPage > 1) {
                        currentPage--
                        // Fetch new data for the previous page and add it to the adapter
                        getLeaderBoard(currentPage)
                    }
                }
            }
        })
    }

    private fun getLeaderBoard(page: Int) {
        if (page > 0) {
            HashMap<String, Any>().apply {
                put("page", page)
                put("limit", 10)
                viewModel.getLeaderBoardData(quizId, this)
            }
        }
    }

    private fun setUpAdapter() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        leaderBoardAdapter = LeaderBoardAdapter()
        binding.leaderBoardRecyclerview.layoutManager = layoutManager
        binding.leaderBoardRecyclerview.adapter = leaderBoardAdapter
    }

    private fun registerObservers() {
        viewModel.leaderBoardLiveData.observe(viewLifecycleOwner) {
            it?.let {
                Log.e(TAG, "leader board response: $it")
                currentPage = if (currentPage == 0) it.data.data.page else 0
                totalPages = it.data.data.total_pages
            }
        }

        viewModel.pageInfoLiveData.observe(viewLifecycleOwner) {
            it?.let {
                Log.e(TAG, "page info response from repo: $it")
                currentPage = if (currentPage == 0) it.first else currentPage
                totalPages = it.second
            }
        }
        viewModel.participantsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    Log.e(TAG, "participants data from db: $it")
                    updateUi(it.asParticipantList(), 6)
                }
            }
        }

        viewModel.gameSummaryLiveData.observe(viewLifecycleOwner) {
            it?.let {
                setUpResultAdapter(it.data.asQuizAnswerList())
            }
        }
    }

    private fun updateUi(boardDataList: List<Participant>, totalQuestions: Int) {
        binding.leaderboardLayout.visibility = View.VISIBLE
        leaderBoardAdapter.submitTotalQuestions(totalQuestions)
        leaderBoardAdapter.submitList(boardDataList)
        leaderBoardAdapter.notifyDataSetChanged()
    }


    private fun showGameSummary() {
        var isShowing = false
        val bottomSheetBehavior =
            BottomSheetBehavior.from(binding.gameSummaryBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setPeekHeight(200, true)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.e(TAG, "state: $newState")
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.e(TAG, "slide offset: $slideOffset")
                binding.gameSummaryBottomSheet.background = ContextCompat.getDrawable(
                    requireContext(),
                    if (slideOffset > 0.8) R.drawable.dark_puple_top_rounde_bg else R.drawable.purple_rounded_bg
                )
            }
        })
        binding.gameSummaryTv.setOnClickListener {
            isShowing = isShowing.not()
            if (isShowing.not()) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun setUpResultAdapter(answerList: List<QuizAnswer>) {
        gameSummaryPagerAdapter = GameSummaryPagerAdapter(requireActivity())
        gameSummaryPagerAdapter.addFragment(QuizAnswerFragment(answerList))
        gameSummaryPagerAdapter.addFragment(QuizAnswerFragment(answerList))
        gameSummaryPagerAdapter.addFragment(SummaryFragment())
        binding.resultViewpager.adapter = gameSummaryPagerAdapter
        setupViewPagerIndicator()
    }

    private fun setupViewPagerIndicator() {
        val indicatorContainer: LinearLayout =
            requireActivity().findViewById(R.id.indicatorContainer)
        val viewPager: ViewPager2 = binding.resultViewpager

        // Create an indicator for each page in the ViewPager2
        val pageCount = viewPager.adapter?.itemCount ?: 0
        for (i in 0 until pageCount) {
            val indicator = ImageView(requireContext())
            indicator.setImageResource(R.drawable.indicator_dot)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(8, 0, 8, 0)
            indicator.layoutParams = layoutParams
            indicatorContainer.addView(indicator)
        }

        // Set a ViewPager2.OnPageChangeCallback to update the indicator when the page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Update the indicator to reflect the current page
                for (i in 0 until pageCount) {
                    val indicator = indicatorContainer.getChildAt(i) as ImageView
                    indicator.setImageResource(
                        if (i == position) R.drawable.indicator_dot_selected else R.drawable.indicator_dot
                    )
                }
            }
        })
    }

}