package com.quiz.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.quiz.app.MoxieApplication
import com.quiz.app.MoxieApplication.Companion.session
import com.quiz.app.ServiceLocator
import com.quiz.app.databinding.FragmentProfileBinding
import com.quiz.app.network.apiService
import com.quiz.app.ui.SplashActivity
import com.quiz.app.utils.SessionManager

class ProfileFragment : Fragment(), View.OnClickListener,
    ProfileAdapter.OnProfileItemClickListener {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModelFactory =
            ServiceLocator.provideViewModelFactory(requireContext(), apiService = apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        setClicks()
        val profileItems = listOf(
            ProfileItem("Account", listOf("Personal Information", "Credit Activity")),
            ProfileItem("Content & Activity", listOf("Push notifications", "Past quizzes")),
            ProfileItem("Support", listOf("Provide feedback", "Frequently asked questions")),
            ProfileItem("About", listOf("Terms of service", " Privacy policy"))
        )
        val adapter = ProfileAdapter(this)
        binding.profileRecyclerView.adapter = adapter
        adapter.submitList(profileItems)

    }

    private fun setClicks() {
        binding.logOutButton.setOnClickListener(this)
        binding.profileBackIv.setOnClickListener(this)
    }

    private fun checkLoginType() {
        val isGoogleLogin =
            MoxieApplication.session(requireContext()).getPrefBool(SessionManager.IS_GOOGLE_LOG_IN)
        val isFbLogin =
            MoxieApplication.session(requireContext()).getPrefBool(SessionManager.IS_FACEBOOK_LOGIN)
        if (isGoogleLogin) googleSingOut()
        else {
            if (isFbLogin) fbSignOut()
            logout()
        }
    }

    private fun fbSignOut() {
        LoginManager.getInstance().logOut()
    }

    private fun logout() {
        val refreshToken =
            session(requireContext()).getPrefString(SessionManager.REFRESH_TOKEN) ?: ""
//        MoxieApplication.session(requireContext()).deleteSaveData()
        viewModel.logout(refreshToken)
//        startActivity(Intent(requireActivity(), SplashActivity::class.java))
//        requireActivity().finishAffinity()
    }

    private fun googleSingOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity(), OnCompleteListener<Void?> {
                logout()
            })
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.logOutButton -> checkLoginType()
            binding.profileBackIv -> findNavController().navigateUp()
        }
    }

    override fun onProfileItemClick(section: String) {
        Toast.makeText(requireContext(), "$section", Toast.LENGTH_SHORT).show()
        when (section) {
            "Credit Activity" -> goToNextScreen(ProfileFragmentDirections.actionProfileFragmentToCreditActivityFragment())
            "Past quizzes" -> goToNextScreen(ProfileFragmentDirections.actionProfileFragmentToPastQuizzesFragment())
            "Terms of service", "Privacy policy" -> goToNextScreen(ProfileFragmentDirections.actionProfileFragmentToLegalInfoFragment())
        }
    }

    private fun goToNextScreen(direction: NavDirections) {
        findNavController().navigate(direction)
    }
}