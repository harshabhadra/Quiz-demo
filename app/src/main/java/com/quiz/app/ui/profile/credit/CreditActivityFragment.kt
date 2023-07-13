package com.quiz.app.ui.profile.credit

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quiz.app.R
import com.quiz.app.databinding.FragmentCreditActivityBinding
import com.quiz.app.domain.model.CreditActivity
import com.quiz.app.domain.model.CreditData

class CreditActivityFragment : Fragment() {

    companion object {
        fun newInstance() = CreditActivityFragment()
    }

    private lateinit var binding: FragmentCreditActivityBinding
    private lateinit var viewModel: CreditActivityViewModel
    private lateinit var creditActivityAdapter: CreditActivityAdapter

    // Create dummy data for CreditActivity and CreditData
    private val creditDataList1 = listOf(
        CreditData("Title 1", "2022-01-01", "Category 1", 10, 100.0),
        CreditData("Title 2", "2022-01-02", "Category 2", 20, 200.0),
        CreditData("Title 3", "2022-01-03", "Category 3", 30, 300.0)
    )
    private val creditDataList2 = listOf(
        CreditData("Title 4", "2022-02-01", "Category 1", 40, 400.0),
        CreditData("Title 5", "2022-02-02", "Category 2", 50, 500.0)
    )
    private val creditDataList3 = listOf(
        CreditData("Title 6", "2022-03-01", "Category 1", 60, 600.0),
        CreditData("Title 7", "2022-03-02", "Category 2", 70, 700.0),
        CreditData("Title 8", "2022-03-03", "Category 3", 80, 800.0),
        CreditData("Title 9", "2022-03-04", "Category 4", 90, 900.0)
    )

    private val creditActivityList = listOf(
        CreditActivity("January 2022", creditDataList1),
        CreditActivity("February 2022", creditDataList2),
        CreditActivity("March 2022", creditDataList3)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(CreditActivityViewModel::class.java)
        setUpAdapter()
    }

    private fun setUpAdapter() {
        creditActivityAdapter = CreditActivityAdapter()
        binding.mainCreditRecyclerView.adapter = creditActivityAdapter
        creditActivityAdapter.submitList(creditActivityList)
    }

}