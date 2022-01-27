package com.dev.ecomercesupplier.utils

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.dev.ecomercesupplier.BuildConfig

class LogUtils {
    companion object{
        fun d(key: String?, message: String?) {
            if (BuildConfig.DEBUG) {
                if (message != null) {
                    Log.d(key, message)
                }
            }
        }

        fun e(key: String?, message: String?) {
            if (BuildConfig.DEBUG) {
                if (message != null) {
                    Log.e(key, message)
                }
            }
        }

        fun shortToast(context: Context?, message: String?) {
            if (context != null) {
                val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                toast.show()
            }
        }

        fun longToast(context: Context?, message: String?) {
            if (context != null) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }


        var att_count_1:Int=0

        var my_reference = ""

    }
}