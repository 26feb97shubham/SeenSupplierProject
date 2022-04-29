package com.dev.ecomercesupplier.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev.ecomercesupplier.BuildConfig
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CustomSpinnerAdapter
import com.dev.ecomercesupplier.adapter.ServedCountriesAdapter
import com.dev.ecomercesupplier.custom.FetchPath
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.custom.Utility.Companion.IMAGE_DIRECTORY_NAME
import com.dev.ecomercesupplier.custom.Utility.Companion.apiInterface
import com.dev.ecomercesupplier.custom.Utility.Companion.hasPermissions
import com.dev.ecomercesupplier.model.ModelForAccountType
import com.dev.ecomercesupplier.model.ModelForSpinner
import com.dev.ecomercesupplier.model.ServeCountries
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import kotlinx.android.synthetic.main.activity_register_1.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.backImg
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity_1 : AppCompatActivity() {
    private var businessName : String ?= null
    private var businessEmail : String ?= null
    private var businessContact : String ?= null
    private var password : String ?= null
    private var cnfrmPassword : String ?= null
    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var uri: Uri? = null
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private var profilePath = ""
    private var selectCountryCode = ""
    private var country_code=ArrayList<String>()
    private var countryList=ArrayList<ModelForSpinner>()
    private var countryCodeList=ArrayList<ModelForSpinner>()
    var cCodeList= arrayListOf<String>()
    var servedCountriesList=ArrayList<ServeCountries>()
    private var accountList=ArrayList<ModelForAccountType>()
    var doubleClick:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_1)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        getCountires()
    }

    private fun getCountires() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar_register_1.visibility= View.VISIBLE

        val builder = ApiClient.createBuilder(arrayOf("lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getCountries(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar_register_1.visibility = View.GONE
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
                LogUtils.shortToast(this@RegisterActivity_1, getString(R.string.check_internet))
                progressBar_register_1.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    private fun setUpViews() {
        iv_back_register_1.setOnClickListener {
            iv_back_register_1.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, iv_back_register_1)
            onBackPressed()
        }

       /* scrollView_register_1.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                et_business_name.clearFocus()
                et_business_email.clearFocus()
                et_business_phone.clearFocus()
                edtPassword.clearFocus()
                edtConfirmPassword.clearFocus()
                return false
            }

        })*/

        editProfile_register.setOnClickListener {
            editProfile_register.startAnimation(AlphaAnimation(1f, 0.5f))
            requestToUploadProfilePhoto()
        }

       /* txtCountryCode_register.setOnClickListener {
            if(cCodeList.size != 0){
                showCountryCodeList()
            }
        }*/

        btnNext_register_1.setOnClickListener {
            btnNext_register_1.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnNext_register_1)
            validateAndNext()
        }

        mtv_log_in.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateAndNext() {
        businessName = et_business_name.text.toString().trim()
        businessEmail = et_business_email.text.toString().trim()
        businessContact = et_business_phone.text.toString().trim()
        password = et_password.text.toString().trim()
        cnfrmPassword = et_verify_password.text.toString().trim()
        selectCountryCode = txtCountryCode_register.text.toString().trim()

        if (TextUtils.isEmpty(businessName)) {
            scrollView_register_1.scrollTo(0, 150)
            et_business_name.requestFocus()
            et_business_name.error=getString(R.string.please_enter_your_company_name)

        }
        else if (!SharedPreferenceUtility.getInstance().isCharacterAllowed(businessName!!)) {
            scrollView_register_1.scrollTo(0, 150)
            et_business_name.requestFocus()
            et_business_name.error=getString(R.string.emojis_are_not_allowed)
        }
        else if (!TextUtils.isEmpty(businessEmail) && !SharedPreferenceUtility.getInstance().isEmailValid(businessEmail!!)) {
            scrollView_register_1.scrollTo(0, 180)
            et_business_email.requestFocus()
            et_business_email.error=getString(R.string.please_enter_valid_email)
        }
        else if (TextUtils.isEmpty(selectCountryCode)) {
            LogUtils.shortToast(this, getString(R.string.please_select_your_country_code))
        }

        else if (TextUtils.isEmpty(businessContact)) {
            scrollView_register_1.scrollTo(0, 210)
            et_business_phone.requestFocus()
            et_business_phone.error=getString(R.string.please_enter_your_phone_number)
        }
        else if ((businessContact!!.length < 7 || businessContact!!.length > 15)) {
            scrollView_register_1.scrollTo(0, 210)
            et_business_phone.requestFocus()
            et_business_phone.error=getString(R.string.mob_num_length_valid)
        }

        else if (TextUtils.isEmpty(password)) {
            et_password.error=getString(R.string.please_enter_your_password)
            et_password.requestFocus()
            scrollView_register_1.scrollTo(0, 240)
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(password!!)) {
            scrollView_register_1.scrollTo(0, 240)
            et_password.requestFocus()
            et_password.error=getString(R.string.password_length_valid)
        }
        else if (TextUtils.isEmpty(cnfrmPassword)) {
            scrollView_register_1.scrollTo(0, 270)
            et_verify_password.requestFocus()
            et_verify_password.error=getString(R.string.please_verify_your_password)
        }
        else if (!cnfrmPassword.equals(password)) {
            scrollView_register_1.scrollTo(0, 270)
            et_verify_password.requestFocus()
            et_verify_password.error=getString(R.string.password_doesnt_match_with_confirm_password)
        }
        else {
            continueToNextPage()
        }
    }

    private fun continueToNextPage() {
        val bundle = Bundle()
        bundle.putString("businessName", businessName)
        bundle.putString("businessEmail", businessEmail)
        bundle.putString("country_code", selectCountryCode)
        bundle.putString("businessContact", businessContact)
        bundle.putString("password", password)
        bundle.putString("cnfrmPassword", cnfrmPassword)
        bundle.putString("profilePath", profilePath)
        bundle.putSerializable("countryList", countryList)
        bundle.putSerializable("countryServedList", servedCountriesList)
        bundle.putSerializable("accountList", accountList)
        startActivity(Intent(this, RegisterActivity_2::class.java).putExtras(bundle))
    }

    private fun showCountryCodeList() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select country code")
        builder.setItems(cCodeList.toArray(arrayOfNulls<String>(cCodeList.size))) { dialogInterface, i ->
            txtCountryCode_register.text=country_code[i]
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
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.8f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        dialog.window!!.attributes = layoutParams
    }

    private fun requestToUploadProfilePhoto() {
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE)
        } else if (hasPermissions(this, *PERMISSIONS)) {
            openCameraDialog()
        }
    }


    private fun openCameraDialog() {
        val items = arrayOf<CharSequence>(getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(items) { dialogInterface, i ->
            if (items[i] == getString(R.string.camera)) {
                captureImage()
            } else if (items[i] == getString(R.string.gallery)) {
                chooseImage()
            } else if (items[i] == getString(R.string.cancel)) {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)
    }


    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }


    fun getOutputMediaFileUri(type: Int): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.toString() + ".provider", getOutputMediaFile(type)!!)
        } else {
            Uri.fromFile(getOutputMediaFile(type))
        }
    }

    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME)
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
            Locale.getDefault()).format(Date())
        val mediaFile: File
        mediaFile = if (type == MEDIA_TYPE_IMAGE) {
            File(mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".png")
        } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            File(mediaStorageDir.path + File.separator
                    + "VID_" + timeStamp + ".mp4")
        } else {
            return null
        }
        return mediaFile
    }

    fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE) {
            if (grantResults.size > 0) {
                if (hasAllPermissionsGranted(grantResults)) {
                    openCameraDialog()
                } else {
                    LogUtils.shortToast(this, getString(R.string.please_grant_both_camera_and_storage_permissions))

                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                LogUtils.shortToast(this, getString(R.string.please_grant_both_camera_and_storage_permissions))
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (uri != null) {

                    profilePath = ""
                    Log.e("uri", uri.toString())
                    profilePath = uri!!.path!!
                    Glide.with(this).load("file:///$profilePath").placeholder(R.drawable.user).into(img_register_1)

                } else {
                    LogUtils.shortToast(this, getString(R.string.something_went_wrong))
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                profilePath = ""
                val uri = data.data
                profilePath = if (uri.toString().startsWith("content")) {
                    FetchPath.getPath(this, uri!!)!!
                } else {
                    uri!!.path!!
                }
                Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load("file:///$profilePath").into(img_register_1)
            }
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
            }, 500)
        }
    }
}