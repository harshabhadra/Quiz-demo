package com.quiz.app.ui.profile.about

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.quiz.app.R
import com.quiz.app.databinding.FragmentLegalInfoBinding

class LegalInfoFragment : Fragment() {

    companion object {
        fun newInstance() = LegalInfoFragment()
        private const val TAG = "LegalInfoFragment"
    }

    private lateinit var viewModel: LegalInfoViewModel
    private lateinit var binding: FragmentLegalInfoBinding
    private lateinit var myWebView: WebView
    private lateinit var avLoading: ProgressBar
    private lateinit var url:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLegalInfoBinding.inflate(inflater, container, false)
        myWebView = binding.adoutWebView
        avLoading = binding.webViewProgress
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LegalInfoViewModel::class.java)

        url = requireArguments().getString("url")?:""
        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = WebViewClient()
        myWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.e("LearnAppFragment", "progress: $newProgress")
                avLoading.isVisible = newProgress < 100
            }

            override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
                Log.e(
                    TAG, "$message -- From line " +
                            "$lineNumber of $sourceID"
                )
            }
        }
        myWebView.loadUrl(url)
    }

}