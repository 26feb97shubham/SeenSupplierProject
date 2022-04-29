package com.dev.ecomercesupplier.custom

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.model.CatListModel
import com.dev.ecomercesupplier.model.ModelForSpinner
import com.dev.ecomercesupplier.model.ServeCountries
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class Utility {
    companion object{
        var showPass = false
        val IMAGE_DIRECTORY_NAME = "Seen Supplier"
        var direction = ""
        var signUpcatIDList= JSONArray()
        val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
        var checkedPosition : Int = 0
        var checkedPackagePosition : Int = 0
        var isSelectAll = false
        var catListSaved = ArrayList<CatListModel>()
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        fun changeLanguage(context:Context, language:String){
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        }
        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
            if (context != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
            }
            return true
        }

        fun showPassword(iv_pass_show_hide_login_verify: ImageView, editText : EditText) {
            if (showPass){
                showPass = false
                editText.transformationMethod = null
                iv_pass_show_hide_login_verify.setImageResource(R.drawable.visible)
            }else{
                showPass = true
                editText.transformationMethod = PasswordTransformationMethod()
                iv_pass_show_hide_login_verify.setImageResource(R.drawable.invisible)
            }
        }


        fun setLanguage(context: Context, language: String){
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        }

    }
}