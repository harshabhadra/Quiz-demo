package com.quiz.app.ui.profile.notifications

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quiz.app.R

class PushNotificationsFragment : Fragment() {

    companion object {
        fun newInstance() = PushNotificationsFragment()
    }

    private lateinit var viewModel: PushNotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_push_notifications, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PushNotificationsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}