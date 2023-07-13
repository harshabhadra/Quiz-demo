package com.quiz.app.ui.profile.about

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quiz.app.R

class LegalInfoFragment : Fragment() {

    companion object {
        fun newInstance() = LegalInfoFragment()
    }

    private lateinit var viewModel: LegalInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_legal_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LegalInfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}