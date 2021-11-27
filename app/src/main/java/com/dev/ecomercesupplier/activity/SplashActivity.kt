package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setUpViews()
        getFCMToken()
        deviceId()
    }
    private fun setUpViews() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsWelcomeShow, false]) {
                if (!TextUtils.isEmpty(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""])) {
                    Utility.changeLanguage(this, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""])
                    if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsLogin, false]) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finishAffinity()
                    } else {
                        startActivity(Intent(this, IntroductionActivity::class.java))
                        finishAffinity()
                    }
                } else {
                    startActivity(Intent(this, ChooseLangActivity::class.java))
                    finishAffinity()
                }


            }
            else{
                startActivity(Intent(this, IntroductionActivity::class.java))
                finishAffinity()
            }
        },2000)
    }
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("getInstanceId", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val fcmToken = task.result
                Log.e("getInstanceId", fcmToken.toString())
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.FCMTOKEN,fcmToken.toString())

            })

    }
    private fun deviceId(){
        val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        Log.e("deviceId", deviceId)
        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.DeviceId,deviceId.toString())
    }
}