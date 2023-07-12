package com.quiz.app.ui.quiz

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.quiz.app.MoxieApplication
import com.quiz.app.ServiceLocator
import com.quiz.app.databinding.FragmentQuizBinding
import com.quiz.app.network.apiService
import com.quiz.app.ui.SplashActivity
import com.quiz.app.ui.quiz.details.QuizDetailsViewModel
import com.quiz.app.ui.viewer.ViewerActivity
import com.quiz.app.utils.ConstUtils
import com.quiz.app.utils.getRandomUid
import io.agora.rtc2.Constants

class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var viewModel: QuizDetailsViewModel
    private val uid = getRandomUid()

    companion object {
        @JvmStatic
        fun newInstance() =
            QuizFragment()

        private const val PERMISSION_REQ_ID = 22
        private const val TAG = "HostFragment"
        private val REQUESTED_PERMISSIONS = arrayOf<String>(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = ServiceLocator.provideViewModelFactory(requireContext(), apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[QuizDetailsViewModel::class.java]

        if (checkSelfPermission().not()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUESTED_PERMISSIONS,
                PERMISSION_REQ_ID
            )
        }
        binding.hostButton.setOnClickListener {
            viewModel.getRtcToken("test", ConstUtils.TYPE_HOST, uid)
        }

        binding.logOutButton.setOnClickListener {
            MoxieApplication.session(requireContext()).deleteSaveData()
            startActivity(Intent(requireActivity(), SplashActivity::class.java))
            requireActivity().finish()
        }

        viewModel.rtcTokenLiveData.observe(viewLifecycleOwner) {
            it?.let {
                Log.e(TAG,"token: ${it.data}")
                startActivity(
                    Intent(requireActivity(), ViewerActivity::class.java).apply {
                        putExtra(ConstUtils.CUSTOMER_TYPE, Constants.CLIENT_ROLE_BROADCASTER)
                        putExtra(ConstUtils.RTC_TOKEN, it.data)
                        putExtra(ConstUtils.UID, uid)
                    }
                )
            }
        }
    }

    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            requireContext(),
            REQUESTED_PERMISSIONS[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    REQUESTED_PERMISSIONS[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }
}