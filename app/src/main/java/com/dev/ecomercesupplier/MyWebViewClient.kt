package com.dev.ecomercesupplier

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class MyWebViewClient() : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        // TODO Auto-generated method stub
        //  multi_per.setVisibility(ProgressBar.GONE);
            view.loadUrl(url!!)
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
    }


}