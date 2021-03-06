package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.rest.ApiUtils
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_reset_password.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var user_id: String
    var password: String = ""
    var confirmPassword: String = ""
    var doubleClick:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
    }
    private fun setUpViews() {
        if(intent.extras != null){
            user_id = intent.extras!!.getString("user_id").toString()
        }
            edtConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val pass = edtPassword.text.toString()

                    if(!TextUtils.isEmpty(pass)){
                        if(!pass.equals(charSeq.toString(), false)){
                            edtConfirmPassword.error=getString(R.string.password_doesnt_match_with_confirm_password)
                        }
                        else{
                            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this@ResetPasswordActivity, edtConfirmPassword)
                        }
                    }
                    else{
                        edtPassword.error=getString(R.string.please_first_enter_your_password)
                    }

                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })

        btnSubmit.setOnClickListener {
            btnSubmit.startAnimation(AlphaAnimation(1f, 0.5f))
            validateAndReset()
            /* val navOptions = NavOptions.Builder().setPopUpTo(R.id.my_nav_graph, true).build()
             findNavController().navigate(R.id.action_resetPasswordFragment_to_homeFragment, null, navOptions)
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 requireActivity().window.setDecorFitsSystemWindows(true)
             } else {
 //                    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                 requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
             }*/
        }
    }

    private fun validateAndReset() {
        password= edtPassword.text.toString()
        confirmPassword= edtConfirmPassword.text.toString()

        if (TextUtils.isEmpty(password)) {
            edtPassword.requestFocus()
            edtPassword.error=getString(R.string.please_enter_your_new_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(password)) {
            edtPassword.requestFocus()
            edtPassword.error=getString(R.string.password_length_valid)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_length_valid))
        }
        else if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.requestFocus()
            edtConfirmPassword.error=getString(R.string.please_verify_your_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_verify_your_password))
        }
        /* else if (confirmPassword.length < 6) {
              edtConfirmPassword.error=getString(R.string.verify_password_length_valid)
 //            LogUtils.shortToast(requireContext(), getString(R.string.verify_password_length_valid))

         }*/
        else if (!confirmPassword.equals(password)) {
            edtConfirmPassword.requestFocus()
            edtConfirmPassword.error=getString(R.string.password_doesnt_match_with_confirm_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_doesnt_match_with_verify_password))
        }
        else{
            resetPassword()
        }

    }


    private fun resetPassword() {

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "password", "confirm_password", "fcm_token", "device_type", "lang"),
            arrayOf(user_id, password, confirmPassword, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.resetPassword(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        LogUtils.shortToast(this@ResetPasswordActivity, jsonObject.getString("message"))
                        if(jsonObject.getInt("response")==1){
                            /*SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, user_id)
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)*/
                            startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                            finishAffinity()
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
                LogUtils.shortToast(this@ResetPasswordActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

   /* override fun onBackPressed() {
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
            }, 300)
        }
    }*/
}