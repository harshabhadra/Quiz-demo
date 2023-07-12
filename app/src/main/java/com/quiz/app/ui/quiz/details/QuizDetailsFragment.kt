package com.quiz.app.ui.quiz.details

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.quiz.app.MoxieApplication.Companion.session
import com.quiz.app.R
import com.quiz.app.ServiceLocator
import com.quiz.app.databinding.FragmentQuizDetailsBinding
import com.quiz.app.network.apiService
import com.quiz.app.network.model.QuizDetailsData
import com.quiz.app.ui.custom.TimePickerFragment
import com.quiz.app.ui.home.HomeFragment
import com.quiz.app.ui.viewer.ViewerActivity
import com.quiz.app.utils.*
import io.agora.rtc2.Constants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class QuizDetailsFragment : Fragment(), MenuProvider, View.OnClickListener,
    TimePickerFragment.OnTimePickListener {

    companion object {
        fun newInstance() = QuizDetailsFragment()
        private const val TAG = "QuizDetailsFragment"
    }

    private lateinit var viewModel: QuizDetailsViewModel
    private lateinit var binding: FragmentQuizDetailsBinding
    private val uid = getRandomUid()
    private var isLive = false
    private lateinit var id: String
    private lateinit var quizDate: String
    private var hour = ""
    private var min = ""
    private lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModelFactory = ServiceLocator.provideViewModelFactory(requireContext(), apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[QuizDetailsViewModel::class.java]
        networkConnectivityObserver =
            ServiceLocator.provideNetworkConnectivityObserver(requireContext())
        id = requireArguments().getString("quiz_id") ?: ""
        Log.e(TAG, "quiz id: $id")
        checkNetworkAndCallApi()
//        id.let {
//            viewModel.getQuizDetails(id)
//        }
        setClicks()
        registerObservers()

    }

    private fun checkNetworkAndCallApi() {
        lifecycleScope.launch {
            networkConnectivityObserver.observe()
                .collectLatest { value: ConnectivityObserver.NetworkStatus ->
                    Log.e(TAG, "network status: $value")
                    if (value == ConnectivityObserver.NetworkStatus.Available) {
                        Toast.makeText(requireContext(), "Back Online", Toast.LENGTH_SHORT).show()
                        viewModel.apply {
                            if (id.isNotEmpty()) getQuizDetails(id)
                        }
                    }
                    if (value == ConnectivityObserver.NetworkStatus.Unavailable || value == ConnectivityObserver.NetworkStatus.Lost) {
                        Toast.makeText(requireContext(), "You are offline", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun registerObservers() {
        viewModel.rtcTokenLiveData.observe(viewLifecycleOwner) {
            it?.let {
                startActivity(
                    Intent(requireActivity(), ViewerActivity::class.java).apply {
                        putExtra(ConstUtils.CUSTOMER_TYPE, Constants.CLIENT_ROLE_AUDIENCE)
                        putExtra(ConstUtils.RTC_TOKEN, it.data)
                        putExtra(ConstUtils.UID, uid)
                        putExtra(ConstUtils.QUIZ_ID, id)
                    }
                )
//                findNavController().navigate(
//                    QuizDetailsFragmentDirections.actionQuizDetailsFragmentToCountDownFragment(
//                        it.data,
//                        Constants.CLIENT_ROLE_AUDIENCE,
//                        uid,
//                        isLive
//                    )
//                )

            }
        }

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

        viewModel.reminderLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.reminderButton.setBackgroundColor(Color.parseColor("#E3E3E3"))
                binding.reminderButton.setCompoundDrawables(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.charm_tick),
                    null
                )
                val selectTime = getTimeFromMillis(quizDate.getFormatMillis())
                binding.reminderButton.text = "Reminder set at $selectTime"
                binding.reminderButton.isEnabled = false
            }
        }
    }

    private fun populateUi(quizData: QuizDetailsData) {

        isLive = quizData.isLive
        quizDate = quizData.startDate
        val quizDateinMilli = quizDate.getFormatMillis()
        val today = getTimeFromMillis(System.currentTimeMillis(), "EEEE")
        val quizDateInDay = getTimeFromMillis(quizDateinMilli, "EEEE h:mm a")
        val quiztime = getTimeFromMillis(quizDateinMilli, "h:mm a")
        binding.toolbarTv.text = if (quizDate.contains(today)) "Today $quiztime" else quizDateInDay
        binding.quizDesTv.text = quizData.description
        binding.reminderButton.text = quizData.quizReminders?.let {
            getString(R.string.go_live)
        } ?: getString(R.string.set_reminder)
        binding.detailsCat.categoryTv.text = quizData.category?.name

    }

    private fun setClicks() {
        binding.upButton.setOnClickListener(this)
        binding.reminderButton.setOnClickListener(this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
            return true
        }
        return false
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.upButton -> {
                findNavController().navigateUp()
            }

            binding.reminderButton -> {
                if (binding.reminderButton.text.toString()
                        .lowercase() == getString(R.string.set_reminder).lowercase()
                ) {
                    TimePickerFragment(this).show(
                        requireActivity().supportFragmentManager,
                        "timePicker"
                    )
                    return
                }
                viewModel.getRtcToken("test", ConstUtils.TYPE_VIEWER, uid)

            }
        }
    }

    override fun onTimePick(hour: Int, min: Int) {
        val userId = session(requireContext()).getPrefString(SessionManager.USER_ID) ?: ""
        this.hour = hour.toString()
        this.min = min.toString()
        HashMap<String, String>().apply {
            put("quiz", id)
            put("user", userId)
            put("date", quizDate)
            viewModel.createQuizReminder(this)
        }

    }


}