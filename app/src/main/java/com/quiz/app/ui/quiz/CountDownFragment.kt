package com.quiz.app.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.quiz.app.databinding.FragmentCountDownBinding
import com.quiz.app.ui.EmojiAdapter
import com.quiz.app.ui.viewer.ViewerActivity
import com.quiz.app.utils.ConstUtils
import com.quiz.app.utils.getEmojiList
import io.agora.rtc2.Constants


class CountDownFragment : Fragment() {

    private lateinit var binding: FragmentCountDownBinding
    private lateinit var rtcToken: String
    private var uid: Int = -1
    private var isLive = false
    private lateinit var emojiAdapter: EmojiAdapter
    private var counter: CountDownTimer? = null

    companion object {
        private const val TAG = "CountDownFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {
            counter?.cancel()
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCountDownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rtcToken = requireArguments().getString(ConstUtils.RTC_TOKEN, "")
        uid = requireArguments().getInt(ConstUtils.UID)
        isLive = requireArguments().getBoolean("isLive")

        emojiAdapter = EmojiAdapter(getEmojiList())
        binding.emojiRecyclerview.adapter = emojiAdapter

        binding.counterGroup.isVisible = isLive.not()
        binding.timerGroup.isVisible = isLive
        if (isLive.not()) startCounter() else startTimer()
    }

    private fun startCounter() {
        counter = object : CountDownTimer(5000, 1000) {
            override fun onTick(p0: Long) {
                try {
                    val anim = AlphaAnimation(1.0f, 0.0f)
                    anim.duration = 950
                    anim.repeatCount = 1
                    anim.repeatMode = Animation.REVERSE
                    binding.counterTv.startAnimation(anim)
                    binding.counterTv.text = "${1 + (p0 / 1000)}"
                } catch (e: Exception) {
                }
            }

            override fun onFinish() {
                startActivity(
                    Intent(requireActivity(), ViewerActivity::class.java).apply {
                        putExtra(ConstUtils.CUSTOMER_TYPE, Constants.CLIENT_ROLE_AUDIENCE)
                        putExtra(ConstUtils.RTC_TOKEN, rtcToken)
                        putExtra(ConstUtils.UID, uid)
                    }
                )
            }
        }
        counter?.start()
    }

    private fun startTimer() {
        counter = object : CountDownTimer(30000, 1000) {
            override fun onTick(p0: Long) {
                binding.timerTv.text = "Game Starting: 0:${1 + (p0 / 1000)}"
            }

            override fun onFinish() {
                findNavController().navigateUp()
            }
        }
        counter?.start()
    }


}