package com.quiz.app.ui.auth.signup

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
import androidx.lifecycle.Observer
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
import com.quiz.app.databinding.FragmentSignUpBinding
import com.quiz.app.network.apiService
import com.quiz.app.ui.MainActivity
import com.quiz.app.utils.*


class SignUpFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSignUpBinding
    private var isAgree = false
    private lateinit var viewModel: SignUpViewModel
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

    companion object {
        @JvmStatic
        fun newInstance() =
            SignUpFragment()

        private const val TAG = "SignUpFragment"
        private const val RC_SIGN_IN = 123
        private const val EMAIL = "email"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        loginButton = binding.loginButton
        loginButton.setFragment(this)
        loginButton.setPermissions(EMAIL)
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

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val viewModelFactory = ServiceLocator.provideViewModelFactory(requireContext(), apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[SignUpViewModel::class.java]

        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        updateUI(account)

        setClicks()
        registerObservers()
    }

    private fun registerObservers() {

        viewModel.signUpLiveData.observe(viewLifecycleOwner, Observer {
            it?.let { resultPair ->
                resultPair.first.let { result ->
                    loading(false)
                    if (result.isSuccess) {
                        Toast.makeText(requireContext(), "Account Created", Toast.LENGTH_SHORT)
                            .show()
                        showFragment(FragConst.LOG_IN, Bundle().apply {
                            putString(ConstUtils.EMAIL, resultPair.second["email"])
                            putString(ConstUtils.PASSWORD, resultPair.second["password"])
                        }, false)
                    } else {
                        Log.e(TAG, "error: ${result.exceptionOrNull()?.message}")
                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun setClicks() {
        binding.createAccountButton.setOnClickListener(this)
        binding.googleSignUpButton.setOnClickListener(this)
        binding.fbSignUpButton.setOnClickListener(this)
        binding.iAgreeCheckbox.setOnCheckedChangeListener { _, b ->
            isAgree = b
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.createAccountButton -> {
                validateInputs()
            }
            binding.googleSignUpButton -> {
                googleSignIn()
            }
            binding.fbSignUpButton -> {
                LoginManager.getInstance().logOut()
                fbSignin()
            }
        }
    }

    private fun fbSignin() {
        loginButton.performClick()
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            data?.let {
                Log.e(TAG, "google account data is not null")
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(it)
                handleSignInResult(task)
            } ?: Log.e(TAG, "launcher data is null")
        }
    }

    private fun validateInputs() {
        val email = binding.editTextTextEmailAddress.text.toString()
        val countryCode = binding.countryPicker.textView_selectedCountry.text.toString()
        var mobileNumber = binding.editTextMobileNumber.text.toString()
        val password = binding.editTextTextPassword.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Email Address", Toast.LENGTH_SHORT).show()
            return
        }
        if (isValidEmail(email).not()) {
            Toast.makeText(requireContext(), "Enter Valid Email Address", Toast.LENGTH_SHORT).show()
            return
        }

        if (mobileNumber.isEmpty() || mobileNumber.length < 10) {
            Toast.makeText(requireContext(), "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Password", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.iAgreeCheckbox.isChecked.not()) {
            Toast.makeText(
                requireContext(),
                "Please Read and Agree to terms and condition",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        mobileNumber = "$countryCode $mobileNumber"
        Log.e(TAG, "number: $mobileNumber")
        createAccount(email, mobileNumber, password)
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        account?.let {
            val email = account.email
            Log.e(TAG, "email : $email")
            session(requireContext()).savePrefBool(SessionManager.IS_GOOGLE_LOG_IN, true)
            goToHome()
        } ?: Log.e(TAG, "Account data is null")
    }

    private fun goToHome() {
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
        binding.createAccountButton.isVisible = isLoading.not()
        binding.signUpProgressBar.isVisible = isLoading
    }

    private fun createAccount(email: String, mobileNumber: String, password: String) {
        loading(true)
        hideKeyboard()
//        val registerInput = RegisterInput("Test", email, password)
        HashMap<String, String>().apply {
            put("email", email)
            put("password", password)
            put("name", "Test")
            put("mobile", mobileNumber)
            viewModel.registerUser(this)
        }
    }

}