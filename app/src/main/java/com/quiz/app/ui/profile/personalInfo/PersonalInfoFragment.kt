package com.quiz.app.ui.profile.personalInfo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quiz.app.R

class PersonalInfoFragment : Fragment() {

    companion object {
        fun newInstance() = PersonalInfoFragment()
    }

    private lateinit var viewModel: PersonalInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PersonalInfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}