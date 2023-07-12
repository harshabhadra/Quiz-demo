package com.quiz.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.quiz.app.R
import com.quiz.app.ServiceLocator
import com.quiz.app.databinding.FragmentHomeBinding
import com.quiz.app.domain.FreeQuizUiModel
import com.quiz.app.domain.model.UpcomingQuizUiModel
import com.quiz.app.utils.ConnectivityObserver
import com.quiz.app.utils.NetworkConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), UpComingQuizlistAdapter.OnQuizItemClickListener {

    private lateinit var upComingQuizlistAdapter: UpComingQuizlistAdapter
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var viewModel: HomeViewModel
    private val overviewAdapter = FreeQuizListAdapter()
    private lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coroutineDispatcher = Dispatchers.IO
        val viewModelFactory = ServiceLocator.provideViewModelFactory(
            requireContext(),
            ServiceLocator.provideApiService(),
            coroutineDispatcher
        )
        networkConnectivityObserver =
            ServiceLocator.provideNetworkConnectivityObserver(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        checkNetworkAndCallApi()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        upComingQuizlistAdapter = UpComingQuizlistAdapter(this)
        binding.upcomingRecyclerView.adapter = upComingQuizlistAdapter

        binding.freeQuizRecyclerView.adapter = overviewAdapter
        binding.profileButton.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
        registerObservers()
    }

    private fun checkNetworkAndCallApi() {
        lifecycleScope.launch {
            networkConnectivityObserver.observe()
                .collectLatest { value: ConnectivityObserver.NetworkStatus ->
                    Log.e(TAG,"network status: $value")
                    if (value == ConnectivityObserver.NetworkStatus.Available) {
                        viewModel.apply {
                            refreshUpcomingQuizzes()
                            refreshFreeQuizzes()
                        }
                    }
                }
        }
    }

    private fun registerObservers() {
        //Observing upcoming quizzes
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.upcomingQuizzesUiModel.collectLatest { result ->
                    if (result.isSuccess) {
                        val data: List<UpcomingQuizUiModel>? = result.getOrNull()
                        data?.let { quizList ->
                            Log.e(TAG, "no of quiz: ${quizList.size}")
                            upComingQuizlistAdapter.submitList(quizList)
                        } ?: Log.e(TAG, "quiz list is null")
                    } else {
                        val error = result.exceptionOrNull()
                        Log.e(TAG, "Failed to get data in ui: $error")
                    }
                }
            }
        }

        //Observing free quiz update
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.freeQuizzes.collectLatest { result ->
                    if (result.isSuccess) {
                        val data: List<FreeQuizUiModel>? = result.getOrNull()
                        data?.let { quizList ->
                            overviewAdapter.submitList(quizList)
                        } ?: Log.e(TAG, "overview quiz list is null")
                    } else {
                        val error = result.exceptionOrNull()
                        Log.e(TAG, "Failed to get data in ui: $error")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
        private const val TAG = "HomeFragment"
    }

    override fun onQuizItemClick(quizDetails: UpcomingQuizUiModel, isVoting: Boolean) {
        findNavController().navigate(
            if (isVoting) {
                HomeFragmentDirections.actionHomeFragmentToVotingFragment(quizDetails.id)
            } else HomeFragmentDirections.actionHomeFragmentToQuizDetailsFragment(quizDetails.id)
        )
    }
}