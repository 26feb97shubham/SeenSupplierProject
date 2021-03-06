package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.model.ModelForAccountType
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.rest.ApiUtils
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.backImg
import kotlinx.android.synthetic.main.activity_forgot_password.edtPhone
import kotlinx.android.synthetic.main.activity_forgot_password.progressBar
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var phone: String
    private var selectCountryCode = ""
    private var countryCodes=ArrayList<String>()
    var cCodeList= arrayListOf<String>()
    private var accountList= java.util.ArrayList<ModelForAccountType>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
//        getCountires()
        setUpViews()
    }
    private fun setUpViews() {
        edtPhone.doOnTextChanged { charSeq, start, before, count ->
            if(charSeq!!.length>7){
                imgTick.visibility= View.VISIBLE

            }
            else{
                imgTick.visibility= View.GONE
            }
        }

        backImg.setOnClickListener {
            backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, backImg)
            onBackPressed()
        }

        btnSend.setOnClickListener {
            btnSend.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnSend)
            validateAndForgot()
        }
    }
    private fun validateAndForgot() {
        phone = edtPhone.text.toString()
        if (TextUtils.isEmpty(phone)) {
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.please_enter_your_phone_number)

        }
        else if ((phone.length < 7 || phone.length > 15)) {
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.mob_num_length_valid)
        }
        else {
            forgotPassword()
        }
    }

    private fun forgotPassword() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("mobile", "country_code", "fcm_token", "device_type", "lang"),
            arrayOf(phone.trim({ it <= ' ' }),  "+971", SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))



        val call = apiInterface.forgotPassword(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
//                            LogUtils.shortToast(this@ForgotPasswordActivity, jsonObject.getString("message"))
                            val data = jsonObject.getJSONObject("data")
//                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            /*val bundle=Bundle()
                            bundle.putString("ref", "2")
                            bundle.putString("user_id", data.getInt("user_id").toString())
                            findNavController().navigate(R.id.action_forgotPasswordFragment_to_otpVerificationFragment, bundle)*/

                            val user_id = data.getInt("user_id").toString()
                            val gson = Gson()
                            val json = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.accountList, "")
                            if (json.isEmpty()){
                                LogUtils.e("err", "eror")
                            }else{
                                val type: Type = object : TypeToken<List<ModelForAccountType?>?>() {}.type
                                accountList = gson.fromJson(json, type)
                            }
                            val bundle = Bundle()
                            bundle.putSerializable("accountList", accountList)
                            bundle.putString("ref", "2")
                            bundle.putString("user_id", user_id)

                            startActivity(
                                Intent(this@ForgotPasswordActivity, OtpVerificationActivity::class.java)
                                    .putExtras(bundle))

                        }
                        else if (jsonObject.getInt("response") == 2){
                            LogUtils.shortToast(this@ForgotPasswordActivity, jsonObject.getString("message"))
                            onBackPressed()

                        }
                        else {
                            LogUtils.shortToast(this@ForgotPasswordActivity, jsonObject.getString("message"))
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
                LogUtils.shortToast(this@ForgotPasswordActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    private fun showCountryCodeList() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("")
        builder.setItems(cCodeList.toArray(arrayOfNulls<String>(cCodeList.size))) { dialogInterface, i ->
            txtCountryCode_forgot.text=countryCodes[i]
        }


        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val layoutParams= WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        val dialogWindowWidth = (displayWidth * 0.6f).toInt()
        val dialogWindowHeight = (displayHeight * 0.6f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        dialog.window!!.attributes = layoutParams
    }



    private fun getCountires() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))
        val call = apiInterface.getCountries(builder!!.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val countries = jsonObject.getJSONArray("countries")
                        countryCodes.clear()
                        for (i in 0 until countries.length()) {
                            val jsonObj = countries.getJSONObject(i)
                            countryCodes.add(jsonObj.getString("country_code"))
                            cCodeList.add(jsonObj.getString("country_name") + " ("+jsonObj.getString("country_code")+")")
                        }
                        txtCountryCode_forgot.text=countryCodes[0]

                        Log.d("countries", countryCodes.toString())

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
                LogUtils.shortToast(this@ForgotPasswordActivity, getString(R.string.check_internet))
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
}