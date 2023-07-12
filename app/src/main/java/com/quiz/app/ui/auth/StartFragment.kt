package com.quiz.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.quiz.app.databinding.FragmentStartBinding
import com.quiz.app.utils.FragConst
import com.quiz.app.utils.showFragment


class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = StartFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startSignUpButton.setOnClickListener {
            (requireActivity() as AuthActivity).showFragment(FragConst.SIGN_UP, addtoBack = false)
        }

        binding.startLogInButton.setOnClickListener {
            (requireActivity() as AuthActivity).showFragment(FragConst.LOG_IN, addtoBack = false)
        }
        binding.textView6.setOnClickListener {
            (requireActivity() as AuthActivity).showFragment(FragConst.LOG_IN, addtoBack = false)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}