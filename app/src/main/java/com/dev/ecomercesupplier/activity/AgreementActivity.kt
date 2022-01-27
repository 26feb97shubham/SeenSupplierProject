package com.dev.ecomercesupplier.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.ecomercesupplier.MyWebViewClient
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility.Companion.checkedPosition
import com.dev.ecomercesupplier.model.ModelForAccountType
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_agreement.*
import kotlinx.android.synthetic.main.activity_register_1.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList

class AgreementActivity : AppCompatActivity() {
    private var url : String ?= null
    private var acc_type_id : String ?= null
    private var userId : String ?= null
    private var lang : String ?= null
    private var accountList= ArrayList<ModelForAccountType>()
    var doubleClick:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)
        setUpView()
    }

    private fun setUpView() {
        if (intent!=null){
            if (intent.extras!=null){
                accountList = intent.getSerializableExtra("accountList") as ArrayList<ModelForAccountType>
            }
        }

        url = accountList[checkedPosition].url
        acc_type_id = accountList[checkedPosition].id.toString()
        Log.e("accid", ""+acc_type_id)

        userId = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString()
        lang = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")

        Log.e("userId", ""+userId)
        Log.e("lang", ""+lang)

        web_view_agreement.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar_accept_agreement.visibility= View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar_accept_agreement.visibility= View.VISIBLE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
                view!!.loadUrl(url!!)
            }
        }
        web_view_agreement.settings.javaScriptEnabled = true
        web_view_agreement.settings.setSupportZoom(true)
        web_view_agreement.getSettings().setBuiltInZoomControls(true)
        web_view_agreement.getSettings().setUseWideViewPort(true)
        web_view_agreement.getSettings().setLoadWithOverviewMode(false)
        web_view_agreement.setBackgroundColor(Color.TRANSPARENT)
        web_view_agreement.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        web_view_agreement.loadUrl(url!!)

        mtv_decline.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("accountList", accountList)
            startActivity(Intent(this, ChooseCategoryActivity::class.java).putExtras(bundle))
        }

        mtv_accept.setOnClickListener {
            acceptAggrement()
        }
    }

    private fun acceptAggrement() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar_accept_agreement.visibility= View.VISIBLE
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "account_type", "lang"),
            arrayOf(userId.toString(),
                acc_type_id.toString(),
                lang.toString()))

        val call = apiInterface.updateCategory(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar_accept_agreement.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response")==1){
                            startActivity(Intent(this@AgreementActivity, PackagePlanActivity::class.java)
                                .putExtra("user_id", userId))
                        }else{
                            LogUtils.shortToast(this@AgreementActivity, "No Response")
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
                LogUtils.shortToast(this@AgreementActivity, getString(R.string.check_internet))
                progressBar_accept_agreement.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    override fun onBackPressed() {
        exitApp()
    }
    private fun exitApp() {
        val toast = Toast.makeText(
            this,
            getString(R.string.please_click_back_again_to_exist),
            Toast.LENGTH_SHORT
        )


        if(doubleClick){
            finishAffinity()
            doubleClick=false
        }
        else{

            doubleClick=true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                toast.show()
                doubleClick=false
            }, 500)
        }
    }
}