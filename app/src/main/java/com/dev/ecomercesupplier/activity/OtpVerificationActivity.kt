package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.model.ModelForAccountType
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.rest.ApiUtils
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.activity_otp_verification.progressBar
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class OtpVerificationActivity : AppCompatActivity() {
    lateinit var ref: String
    lateinit var pin: String
    var user_id: String=""
    var doubleClick:Boolean=false
    var spannableString : SpannableString?= null
    private var accountList= java.util.ArrayList<ModelForAccountType>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        spannableString = SpannableString(getString(R.string.resend))
        spannableString!!.setSpan(UnderlineSpan(), 0, spannableString!!.length, 0)
        resend.setText(spannableString)
        setUpViews()
    }
    private fun setUpViews() {
        if(intent.extras != null){
            user_id = intent.extras!!.getString("user_id").toString()
            ref = intent.extras!!.getString("ref").toString()
            accountList = intent.getSerializableExtra("accountList") as ArrayList<ModelForAccountType>
        }

        if (ref=="1"){
            txt1.text = getString(R.string.sign_up)
            backImg.visibility = View.GONE
        }else{
            txt1.text = getString(R.string.forgotPassword)
            backImg.visibility = View.VISIBLE
        }

        btnVerify.isEnabled=false
        btnVerify.alpha=0.5f

        firstPinView.doOnTextChanged { charSeq, start, before, count ->
            if(charSeq!!.length==4){
                btnVerify.isEnabled=true
                btnVerify.alpha=1f
                SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this@OtpVerificationActivity, firstPinView)

            }
            else{
                btnVerify.isEnabled=false
                btnVerify.alpha=0.5f
            }
        }


        btnVerify.setOnClickListener {
            btnVerify.startAnimation(AlphaAnimation(1f, 0.5f))
            pin = firstPinView.text.toString()
            verifyAccount()

        }

        resend.setOnClickListener {
            resend.startAnimation(AlphaAnimation(1f, 0.5f))
            firstPinView.setText("")
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, resend)
            resendOtp()
        }
    }


    private fun verifyAccount() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "otp", "fcm_token", "device_type", "lang"),
            arrayOf(user_id, pin.trim({ it <= ' ' }),  SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.verifyAccount(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){

                            if(ref=="1"){
                                LogUtils.longToast(this@OtpVerificationActivity, getString(R.string.your_profile_has_been_sent_to_admin_for_verification))
                                val bundle = Bundle()
                                bundle.putSerializable("accountList", accountList)

                                startActivity(Intent(this@OtpVerificationActivity, ChooseCategoryActivity::class.java).putExtras(bundle))
                                finish()
                                /*val data = jsonObject.getJSONObject("data")
                                val account_type = data.getInt("account_type")
                                val package_id = data.getInt("package_id")
                                if(account_type==2){
                                    startActivity(Intent(this@OtpVerificationActivity, ChooseCategoriesActivity::class.java).putExtra("user_id", user_id))
                                }
                                else{
                                    if(package_id==0){
                                        startActivity(Intent(this@OtpVerificationActivity, PackagePlanActivity::class.java).putExtra("user_id", user_id))
                                    }
                                    else{
                                        startActivity(Intent(this@OtpVerificationActivity, ChooseCategoriesActivity::class.java).putExtra("user_id", user_id))
                                    }
                                }*/


                            }
                            /*else if(ref=="2"){
                                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, user_id.toInt())
                                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                                startActivity(Intent(this@OtpVerificationActivity, HomeActivity::class.java))
                                finish()
                                *//*val data = jsonObject.getJSONObject("data")
                                val account_type = data.getInt("account_type")
                                val package_id = data.getInt("package_id")
                                if(account_type==2){
                                    startActivity(Intent(this@OtpVerificationActivity, ChooseCategoriesActivity::class.java).putExtra("user_id", user_id))
                                }
                                else{
                                    if(package_id==0){
                                        startActivity(Intent(this@OtpVerificationActivity, PackagePlanActivity::class.java).putExtra("user_id", user_id))
                                    }
                                    else{
                                        startActivity(Intent(this@OtpVerificationActivity, ChooseCategoriesActivity::class.java).putExtra("user_id", user_id))
                                    }
                                }*//*


                            }*/
                            else{
                                startActivity(
                                    Intent(this@OtpVerificationActivity, ResetPasswordActivity::class.java)
                                    .putExtra("user_id", user_id))
                                /* val bundle=Bundle()
                                 bundle.putString("user_id", user_id)
                                 findNavController().navigate(R.id.action_otpVerificationFragment_to_resetPasswordFragment, bundle)*/
                            }
                            /*val data = jsonObject.getJSONObject("data")
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("id"))
                            val bundle=Bundle()
                            bundle.putString("ref", "2")
                            findNavController().navigate(R.id.action_forgotPasswordFragment_to_otpVerificationFragment, bundle)*/

                        }
                        else {
                            LogUtils.shortToast(this@OtpVerificationActivity, jsonObject.getString("message"))
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
                LogUtils.shortToast(this@OtpVerificationActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
    private fun resendOtp() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "fcm_token", "device_type", "lang"),
            arrayOf(user_id, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.resendOtp(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        LogUtils.shortToast(this@OtpVerificationActivity, jsonObject.getString("message"))

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
                LogUtils.shortToast(this@OtpVerificationActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    override fun onBackPressed() {
        if (ref=="1"){
            exitApp()
        }else{
            finish()
        }

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
            }, 300)
        }
    }
}