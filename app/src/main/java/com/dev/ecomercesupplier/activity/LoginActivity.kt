package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.model.ModelForAccountType
import com.dev.ecomercesupplier.model.ModelForSpinner
import com.dev.ecomercesupplier.model.ServeCountries
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.rest.ApiUtils
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edtPhone
import kotlinx.android.synthetic.main.activity_login.progressBar
import kotlinx.android.synthetic.main.activity_register_1.*
import kotlinx.android.synthetic.main.activity_sign_up.*

import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class LoginActivity : AppCompatActivity() {
    var remembered:Boolean=false
    var phone: String = ""
    var password: String = ""
    var doubleClick:Boolean=false
    var spannableString : SpannableString?= null
    private var country_code= ArrayList<String>()
    private var countryList= ArrayList<ModelForSpinner>()
    private var countryCodeList= ArrayList<ModelForSpinner>()
    var cCodeList= arrayListOf<String>()
    var servedCountriesList= ArrayList<ServeCountries>()
    private var accountList= ArrayList<ModelForAccountType>()
    var user_id: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        spannableString = SpannableString(getString(R.string.signup))
        spannableString!!.setSpan(UnderlineSpan(), 0, spannableString!!.length, 0)
        tv_signup.setText(spannableString)
        setUpViews()
        getCountires()
    }
    private fun getCountires() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val builder = ApiClient.createBuilder(arrayOf("lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = Utility.apiInterface.getCountries(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val countries = jsonObject.getJSONArray("countries")
                        country_code.clear()
                        countryList.clear()
                        val s= ModelForSpinner()
                        s.id=0
                        s.name=getString(R.string.select_country)
                        countryList.add(s)

                        cCodeList.clear()
                        for (i in 0 until countries.length()) {
                            val jsonObj = countries.getJSONObject(i)
                            country_code.add(jsonObj.getString("country_code"))

                            val s1= ModelForSpinner()
                            s1.id=jsonObj.getInt("id")
                            s1.name=jsonObj.getString("country_name")
                            countryList.add(s1)
                            countryCodeList.add(s1)
                            cCodeList.add(jsonObj.getString("country_name") + " ("+jsonObj.getString("country_code")+")")
                        }

                        val countries_to_be_served = jsonObject.getJSONArray("countries_to_be_served")
                        servedCountriesList.clear()
                        for (i in 0 until countries_to_be_served.length()) {
                            val jsonObj = countries_to_be_served.getJSONObject(i)
                            val s1= ServeCountries()
                            s1.id=jsonObj.getInt("id")
                            s1.country_name=jsonObj.getString("country_name")
                            s1.country_code=jsonObj.getString("country_name")
                            servedCountriesList.add(s1)
                        }

                        val account_types = jsonObject.getJSONArray("account_types")
                        accountList.clear()
                        for (i in 0 until account_types.length()) {
                            val jsonObj = account_types.getJSONObject(i)
                            val m1 = ModelForAccountType()
                            m1.id=jsonObj.getInt("id")
                            m1.name=jsonObj.getString("name")
                            m1.url =jsonObj.getString("url")
                            accountList.add(m1)
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
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun setUpViews() {

        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en"){
            edtPass.gravity = Gravity.LEFT
        }else if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="ar"){
            edtPass.gravity = Gravity.RIGHT
        }

        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsRemembered, false]){
            remembered=true
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en") {
                chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0)
            }
            else{
                chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0)
            }
            phone=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.Phone, ""]
            password=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.Password, ""]
            user_id = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString()
            edtPhone.setText(phone)
            edtPass.setText(password)
        }else{
            remembered = false
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en") {
                chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ellipse, 0, 0, 0)
            }
            else{
                chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ellipse, 0)
            }
            user_id = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString()
            edtPhone.setText("")
            edtPass.setText("")
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
                   chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0)
                }
                else{
                   chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0)
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
            startActivity(Intent(this, RegisterActivity_1::class.java))
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
                            /*val data = jsonObject.getJSONObject("data")
                            val account_type = data.getInt("account_type")
                            val package_id = data.getInt("package_id")
                            val category_array = data.getString("category_array")
                            if(account_type==2){
                                if(TextUtils.isEmpty(category_array)){
                                    startActivity(Intent(this@LoginActivity, ChooseCategoriesActivity::class.java).putExtra("user_id", data.getInt("user_id").toString()))
                                }
                                else{
                                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                }

                            }else {
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
                            }*/

                            val data = jsonObject.getJSONObject("data")
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))

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
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            val bundle = Bundle()
                            bundle.putSerializable("accountList", accountList)
                            startActivity(
                                Intent(this@LoginActivity, ChooseCategoryActivity::class.java).putExtras(bundle))

                        }else if (jsonObject.getInt("response") == 3){
                            val data = jsonObject.getJSONObject("data")
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            user_id = data.getInt("user_id").toString()
                            startActivity(Intent(this@LoginActivity, PackagePlanActivity::class.java)
                                    .putExtra("user_id", user_id))
                        }else if (jsonObject.getInt("response") == 4){
                            val data = jsonObject.getJSONObject("data")
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            user_id = data.getInt("user_id").toString()
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
                            bundle.putString("ref", "1")
                            bundle.putString("user_id", user_id)
                            startActivity(Intent(this@LoginActivity, OtpVerificationActivity::class.java)
                                .putExtras(bundle))
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