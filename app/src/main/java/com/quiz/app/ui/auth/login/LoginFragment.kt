package com.quiz.app.ui.auth.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.quiz.app.MoxieApplication.Companion.session
import com.quiz.app.ServiceLocator
import com.quiz.app.databinding.FragmentLoginBinding
import com.quiz.app.network.apiService
import com.quiz.app.network.model.NetworkLoginResponse
import com.quiz.app.ui.MainActivity
import com.quiz.app.utils.ConstUtils
import com.quiz.app.utils.SessionManager
import com.quiz.app.utils.hideKeyboard

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var emailNumber: String
    private lateinit var password: String

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val callbackManager = CallbackManager.Factory.create()
    private lateinit var loginButton: LoginButton

    private var googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e(TAG, "launcher result: ok")
                val data: Intent? = result.data
                data?.let {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(it)
                    handleSignInResult(task)
                } ?: Log.e(TAG, "launcher data is null")
            } else {
                Log.e(TAG, "launcher result: ${result.resultCode}")
            }
        }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
            Log.e(TAG, "sign up successful: ${account.email}")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        account?.let {
            val email = account.email
            Log.e(TAG, "email : $email")
            session(requireContext()).savePrefBool(SessionManager.IS_GOOGLE_LOG_IN, true)
            goToHome()
        } ?: Log.e(TAG, "Account data is null")
    }

    companion object {
        private const val TAG = "LoginFragment"
        private const val EMAIL = "email"

        @JvmStatic
        fun newInstance() =
            LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        loginButton = binding.loginButtonFb
        loginButton.setFragment(this)
        loginButton.setPermissions("email")
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.e(TAG, "Fb login cancel")
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "fb login error: ${error.message}")
            }

            override fun onSuccess(result: LoginResult) {
                Log.e(TAG, "fb login success: ${result.accessToken}")
                session(requireContext()).savePrefBool(SessionManager.IS_FACEBOOK_LOGIN, true)
                goToHome()
            }

        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val viewModelFactory = ServiceLocator.provideViewModelFactory(requireContext(), apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        updateUI(account)

        registerObservers()
        setClicks()

        arguments?.let {
            emailNumber = it.getString(ConstUtils.EMAIL, "")
            password = it.getString(ConstUtils.PASSWORD, "")
            Log.e(TAG, "From bundle: $emailNumber = $emailNumber, password: $password")
            if (emailNumber.isNotEmpty() && password.isNotEmpty()) {
                binding.editTextTextEmailAddress.setText(emailNumber)
                binding.editTextTextPassword.setText(password)
                binding.loginButton.performClick()
            }
        }

    }

    private fun registerObservers() {
        viewModel.loginLiveData.observe(viewLifecycleOwner) {
            it?.let { result: Result<NetworkLoginResponse> ->
                loading(false)
                if (result.isSuccess) {
                    if (binding.rememberMeCheckBox.isChecked) {
                        saveCredential(result.getOrNull())
                    }
                    goToHome(result.getOrNull())
                } else {
                    Log.e(TAG, "error: ${result.exceptionOrNull()?.message}")
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setClicks() {
        binding.loginButton.setOnClickListener(this)
        binding.forgotPassTv.setOnClickListener(this)
        binding.googleLogInButton.setOnClickListener(this)
        binding.fbLogInButton.setOnClickListener(this)
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.loginButton -> {
                hideKeyboard()
                loading(true)
                validateInputs()
            }
            binding.googleLogInButton -> {
                googleSignIn()
            }
            binding.fbLogInButton -> {
                LoginManager.getInstance().logOut()
                loginButton.performClick()
            }
        }
    }

    private fun validateInputs() {
        val emailNumber = binding.editTextTextEmailAddress.text.toString()
        val password = binding.editTextTextPassword.text.toString()

        if (emailNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Email or Mobile Number", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Password", Toast.LENGTH_SHORT).show()
            return
        }
        HashMap<String, String>().apply {
            put("email", emailNumber)
            put("password", password)
            viewModel.loginUser(this)
        }
    }

    private fun goToHome(networkLoginResponse: NetworkLoginResponse? = null) {
        Toast.makeText(requireContext(), "Welcome", Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(
                requireContext(),
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        requireActivity().finish()
    }

    private fun loading(isLoading: Boolean) {
        binding.loginButton.isVisible = isLoading.not()
        binding.loginProgressBar.isVisible = isLoading
    }

    private fun saveCredential(networkLoginResponse: NetworkLoginResponse?) {

        session(requireContext()).savePrefBool(SessionManager.IS_LOGIN, true)
        networkLoginResponse?.let {
            session(requireContext()).savePrefString(SessionManager.USER_TYPE, it.user.role)
            session(requireContext()).savePrefString(
                SessionManager.ACCESS_TOKEN,
                networkLoginResponse.tokens.access.token
            )
            session(requireContext()).savePrefString(
                SessionManager.REFRESH_TOKEN,
                networkLoginResponse.tokens.refresh.token
            )
            session(requireContext()).savePrefString(
                SessionManager.ACCESS_TOKEN_TIME,
                networkLoginResponse.tokens.access.expires
            )
            session(requireContext()).savePrefString(SessionManager.USER_ID, networkLoginResponse.user.id)
        }
    }
}