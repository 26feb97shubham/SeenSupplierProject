package com.dev.ecomercesupplier.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.activity_web_view.backImg
import kotlinx.android.synthetic.main.activity_web_view.progressBar

class WebViewActivity : AppCompatActivity() {
    var webUrl:String="https://www.privacypolicyonline.com/live.php?token=eVmpQy9Mb6H4HRFi7mderDrhJKaa6Bz6"
    var title:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
    }

    private fun setUpViews() {
        if(intent.extras != null){
            title=intent.getStringExtra("title").toString()
        }
        txtTitle.text=title
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) { //Make the bar disappear after URL is loaded, and changes string to Loading...
                progressBar.visibility= View.VISIBLE
                if(progress>=80){
                    progressBar.visibility= View.GONE
                }

            }
        }
        webView.settings.javaScriptEnabled=true
        webView.settings.allowContentAccess=true
//        webView.settings.builtInZoomControls=true
        webView.settings.loadWithOverviewMode=true
        webView.settings.useWideViewPort=true
        webView.settings.loadsImagesAutomatically=true
        webView.loadUrl(webUrl)

        backImg.setOnClickListener {
            backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, backImg)
            onBackPressed()
        }
    }
}