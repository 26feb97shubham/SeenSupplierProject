package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.rest.ApiUtils
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edtPhone
import kotlinx.android.synthetic.main.activity_login.progressBar
import kotlinx.android.synthetic.main.activity_sign_up.*

import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    var remembered:Boolean=false
    var phone: String = ""
    var password: String = ""
    var doubleClick:Boolean=false
    var spannableString : SpannableString?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        spannableString = SpannableString("SIGNUP")
        spannableString!!.setSpan(UnderlineSpan(), 0, spannableString!!.length, 0)
        tv_signup.setText(spannableString)
        setUpViews()
    }
    private fun setUpViews() {
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsRemembered, false]){
            remembered=true
            chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checked_small, 0, 0, 0)
//            chkRememberMe.isChecked=true
            phone=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.Phone, ""]
            password=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.Password, ""]
            edtPhone.setText(phone)
            edtPass.setText(password)
        }

        /*chkRememberMe.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                remembered = isChecked

            }

        })*/
        chkRememberMe.setOnClickListener {
            if(remembered){
                remembered=false
                if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en") {
                    chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ellipse, 0, 0, 0)
                }
                else{
                   chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ellipse, 0)
                }

            }
            else{
                remembered=true
                if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en") {
                   chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checked_small, 0, 0, 0)
                }
                else{
                   chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checked_small, 0)
                }
            }
        }

//        backImg.setOnClickListener {
//            backImg.startAnimation(AlphaAnimation(1f, 0.5f))
//            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, backImg)
//            onBackPressed()
//        }
        txtForgotPass.setOnClickListener {
            txtForgotPass.startAnimation(AlphaAnimation(1f, 0.5f))
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        btnLogin.setOnClickListener {
            btnLogin.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnLogin)
            validateAndLogin()

            /* val navOptions = NavOptions.Builder().setPopUpTo(R.id.my_nav_graph, true).build()
             findNavController().navigate(R.id.action_loginFragment_to_homeFragment, null, navOptions)
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 window.setDecorFitsSystemWindows(true)
             } else {
 //                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                 window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
             }*/
        }

        tv_signup.setOnClickListener{
            tv_signup.startAnimation(AlphaAnimation(1f, 0.5f))
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


    private fun validateAndLogin() {
        phone = edtPhone.text.toString()
        password= edtPass.text.toString()


        if (TextUtils.isEmpty(phone)) {
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.please_enter_your_phone_number)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_mob_number))

        }
        else if ((phone.length < 7 || phone.length > 15)) {
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.mob_num_length_valid)
//            LogUtils.shortToast(requireContext(), getString(R.string.mob_num_length_valid))
        }

        else if (TextUtils.isEmpty(password)) {
            edtPass.requestFocus()
            edtPass.error=getString(R.string.please_enter_your_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(password)) {
            edtPass.requestFocus()
            edtPass.error=getString(R.string.invalid_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_length_valid))
        }

        else {
            if(remembered){
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsRemembered, true)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Phone, phone)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Password, password)
            }
            else{
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsRemembered, false)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Phone, "")
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Password, "")
            }
            getLogin()
        }
    }

    private fun getLogin() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("password", "fcm_token", "device_type", "device_id", "mobile", "lang"),
            arrayOf(password.trim({ it <= ' ' }),
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], phone.trim({ it <= ' ' }), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.login(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            val data = jsonObject.getJSONObject("data")
                            val account_type = data.getInt("account_type")
                            val package_id = data.getInt("package_id")
                            val category_array = data.getString("category_array")
                            if(account_type==2){
                                if(TextUtils.isEmpty(category_array)){
                                    startActivity(Intent(this@LoginActivity, ChooseCategoriesActivity::class.java).putExtra("user_id", data.getInt("user_id").toString()))
                                }
                                else{
                                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                }

                            }
                            else{
                                if(package_id==0){
                                    startActivity(Intent(this@LoginActivity, PackagePlanActivity::class.java).putExtra("user_id", data.getInt("user_id").toString()))
                                }
                                else{
                                    if(TextUtils.isEmpty(category_array)){
                                        startActivity(Intent(this@LoginActivity, ChooseCategoriesActivity::class.java).putExtra("user_id", data.getInt("user_id").toString()))
                                    }
                                    else{
                                        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                                        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                    }
                                }
                            }

                      /*      SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))*/

                        }
                        else if (jsonObject.getInt("response") == 2){
                            val data = jsonObject.getJSONObject("data")
                            LogUtils.shortToast(this@LoginActivity, jsonObject.getString("message"))
                            /*  val bundle=Bundle()
                              bundle.putString("ref", "1")
                              bundle.putString("user_id", data.getInt("user_id").toString())

  //                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                              findNavController().navigate(R.id.action_loginFragment_to_otpVerificationFragment, bundle)*/
                            startActivity(
                                Intent(this@LoginActivity, OtpVerificationActivity::class.java).putExtra("ref", "2")
                                .putExtra("user_id", data.getInt("user_id").toString()))

                        }
                        else {
                            LogUtils.longToast(this@LoginActivity, jsonObject.getString("message"))
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
                LogUtils.shortToast(this@LoginActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    override fun onBackPressed() {
//        startActivity(Intent(this, ChooseLoginSignUpActivity::class.java))
//        finish()
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
    }
}