package com.dev.ecomercesupplier.activity

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.text.HtmlCompat
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.extras.MyWebViewClient
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*
import kotlinx.android.synthetic.main.fragment_cms.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class TermsAndConditionsActivity : AppCompatActivity() {
    var content = ""
    var title = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        getTermsConditions()

    }
    private fun setUpViews() {
        if(intent.extras != null){
            title=intent.getStringExtra("title").toString()
        }
//        txtTitle.text=title

        other_frag_backImg.setOnClickListener {
            other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            onBackPressed()
        }

        webViewActivity.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar_tnc.visibility= View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar_tnc.visibility= View.VISIBLE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
                view!!.loadUrl(url!!)
            }
        }
        webViewActivity.settings.javaScriptEnabled = true
        webViewActivity.settings.setSupportZoom(true)
        webViewActivity.getSettings().setBuiltInZoomControls(true)
        //Enable Multitouch if supported by ROM
        webViewActivity.getSettings().setUseWideViewPort(true)
        webViewActivity.getSettings().setLoadWithOverviewMode(false)
        webViewActivity.setBackgroundColor(Color.TRANSPARENT)
        webViewActivity.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
    }

    private fun getTermsConditions() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar_tnc.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getTermsConditions(builder.build())

        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar_tnc.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            val data = jsonObject.getJSONObject("data")
                            webViewActivity.loadUrl(data.getString("url"))

                        }
                        else {
                            LogUtils.shortToast(this@TermsAndConditionsActivity, jsonObject.getString("message"))
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(this@TermsAndConditionsActivity, getString(R.string.check_internet))
                progressBar_tnc.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

}