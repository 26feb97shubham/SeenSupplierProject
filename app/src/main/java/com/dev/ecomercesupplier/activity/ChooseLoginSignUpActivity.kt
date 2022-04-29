package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.Toast
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_choose_login_sign_up.*

class ChooseLoginSignUpActivity : AppCompatActivity() {
    var doubleClick:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login_sign_up)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
    }
    private fun setUpViews() {

        btnLogin.setOnClickListener {
            btnLogin.startAnimation(AlphaAnimation(1f, 0.5f))
            startActivity(Intent(this, LoginActivity::class.java))
        }
        btnSignUp.setOnClickListener {
            btnSignUp.startAnimation(AlphaAnimation(1f, 0.5f))
            startActivity(Intent(this, RegisterActivity_1::class.java))
        }
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
            }, 300)
        }
    }
}