package com.dev.ecomercesupplier.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.BuildConfig
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CustomSpinnerAdapter
import com.dev.ecomercesupplier.adapter.ServedCountriesAdapter
import com.dev.ecomercesupplier.custom.FetchPath
import com.dev.ecomercesupplier.custom.Utility.Companion.IMAGE_DIRECTORY_NAME
import com.dev.ecomercesupplier.custom.Utility.Companion.PERMISSIONS
import com.dev.ecomercesupplier.custom.Utility.Companion.PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE
import com.dev.ecomercesupplier.custom.Utility.Companion.hasPermissions
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.ModelForAccountType
import com.dev.ecomercesupplier.model.ModelForSpinner
import com.dev.ecomercesupplier.model.ServeCountries
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.rest.ApiUtils
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import kotlinx.android.synthetic.main.activity_register_1.*
import kotlinx.android.synthetic.main.activity_register_2.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.MediaType
import okhttp3.RequestBody
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
import kotlin.collections.ArrayList

class RegisterActivity_2 : AppCompatActivity() {
    private var businessName : String ?= null
    private var businessEmail : String ?= null
    private var businessContact : String ?= null
    private var password : String ?= null
    private var profilePath : String ?= null
    private var selectCountryCode : String ?= null
    val PICK_DOC = 11
    private var docpath = ""
    val MEDIA_TYPE_IMAGE = 1
    private var countryList=ArrayList<ModelForSpinner>()
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    lateinit var adp_country_name: CustomSpinnerAdapter
    var servedCountries= JSONArray()
    private var accountList= java.util.ArrayList<ModelForAccountType>()
    lateinit var servedCountriesAdapter: ServedCountriesAdapter
    private var uri: Uri? = null
    var servedCountriesList=ArrayList<ServeCountries>()
    private var selectCountryId = "0"
    var document_uploaded = ""
    val PICK_IMAGE_FROM_GALLERY = 10
    var isChecked: Boolean=false
    var is_back = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_2)
        setUpViews()
    }

    private fun setUpViews() {
        is_back = false
        iv_back_register_2.setOnClickListener {
            iv_back_register_2.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, iv_back_register_2)
            is_back = true
            onBackPressed()
        }

        if (intent!=null){
            if(intent.extras!=null){
                businessName = intent.getStringExtra("businessName")
                businessEmail = intent.getStringExtra("businessEmail")
                businessContact = intent.getStringExtra("businessContact")
                password = intent.getStringExtra("password")
                profilePath = intent.getStringExtra("profilePath")
                selectCountryCode = intent.getStringExtra("country_code")
                servedCountriesList = intent.getSerializableExtra("countryServedList") as ArrayList<ServeCountries>
                countryList = intent.getSerializableExtra("countryList") as ArrayList<ModelForSpinner>
                accountList = intent.getSerializableExtra("accountList") as ArrayList<ModelForAccountType>
            }
        }

        btnSignUp_register_2.setOnClickListener {
            btnSignUp_register_2.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnSignUp_register_2)
            validateAndSignUp()
        }

        adp_country_name = CustomSpinnerAdapter(this, countryList)
        spinnerCountry_register_2.adapter = adp_country_name

        spinnerCountry_register_2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectCountryId=countryList[p2].id.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectCountryId=""
            }
        }
        adp_country_name.notifyDataSetChanged()

        rv_countries_to_be_served_register_2.layoutManager =LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        servedCountriesAdapter= ServedCountriesAdapter(this, servedCountriesList,is_back, object : ClickInterface.ClickArrayInterface{
            override fun clickArray(array: JSONArray) {
                servedCountries=array
                Log.e("servedCountries", servedCountries.toString())
            }
        })
        rv_countries_to_be_served_register_2.adapter=servedCountriesAdapter
        servedCountriesAdapter.notifyDataSetChanged()

        mtv_upload_trade_licence.setOnClickListener {
            mtv_upload_trade_licence.startAnimation(AlphaAnimation(1f, 0.5f))
            requestToUploadDocument()
        }

        imgChkTnC.setOnClickListener {
            imgChkTnC.startAnimation(AlphaAnimation(1f, 0.5f))
            if(isChecked){
                isChecked=false
                imgChkTnC.setImageResource(R.drawable.un_check)
            }
            else{
                isChecked=true
                imgChkTnC.setImageResource(R.drawable.check)
            }
        }
        txtTermsConditions_view.setOnClickListener {
            txtTermsConditions_view.startAnimation(AlphaAnimation(1f, 0.5f))
            startActivity(Intent(this, TermsAndConditionsActivity::class.java).putExtra("title", getString(R.string.terms_amp_conditions)))
        }
        
    }

    private fun requestToUploadDocument() {
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE)
        } else if (hasPermissions(this, *PERMISSIONS)) {
            openDocDialog()
        }
    }

    private fun openDocDialog() {
        val items = arrayOf<CharSequence>(getString(R.string.camera), getString(R.string.gallery), getString(R.string.document), getString(R.string.cancel))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_documents))
        builder.setItems(items) { dialogInterface, i ->
            if (items[i] == getString(R.string.camera)) {
                captureImage()
            } else if (items[i] == getString(R.string.gallery)) {
                chooseImage()
            }
            else if (items[i] == getString(R.string.document)) {
                chooseDoc()
            }else if (items[i] == getString(R.string.cancel)) {
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

    private fun chooseDoc() {
        val intent=Intent(this, FilePickerActivity::class.java)
        intent.putExtra(FilePickerActivity.CONFIGS, Configurations.Builder()
            .setCheckPermission(true)
            .setShowFiles(true)
            .setShowImages(false)
            .setShowAudios(false)
            .setShowVideos(false)
            .setMaxSelection(1)
            .setSuffixes("txt", "pdf","doc", "docx")
            .setSkipZeroSizeFiles(true)
            .build())
        startActivityForResult(intent, PICK_DOC)
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
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        // Create a media file name
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
            if (grantResults.size > 0) { /*  if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {*/
                if (hasAllPermissionsGranted(grantResults)) {
                    openDocDialog()
                } else {
                    LogUtils.shortToast(this, "Please grant both Camera and Storage permissions")
                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                LogUtils.shortToast(this, "Please grant both Camera and Storage permissions")
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) { //previewCapturedImage();
                if (uri != null) {
                    docpath = ""
                    docpath = uri!!.path!!
                    document_uploaded = getString(R.string.document_uploaded)
                    mtv_upload_trade_licence.visibility = View.GONE
                    rlUploadLicense_register_2.visibility = View.VISIBLE
                    txt2_register_2.text = getString(R.string.document_uploaded)
                    Glide.with(this).load("file:///$docpath").placeholder(R.drawable.attach).into(imgAttach_register_2)

                } else {
                    LogUtils.shortToast(this, "something went wrong! please try again")
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                docpath = ""
                val uri = data.data
                docpath = if (uri.toString().startsWith("content")) {
                    FetchPath.getPath(this, uri!!)!!
                } else {
                    uri!!.path!!
                }
                document_uploaded = getString(R.string.document_uploaded)
                mtv_upload_trade_licence.visibility = View.GONE
                rlUploadLicense_register_2.visibility = View.VISIBLE
                txt2_register_2.text = getString(R.string.document_uploaded)

                Glide.with(this).load("file:///$docpath").placeholder(R.drawable.attach).into(imgAttach_register_2)

            }
        }
        else if (requestCode == PICK_DOC && resultCode == Activity.RESULT_OK && data != null) {
            val files: java.util.ArrayList<MediaFile> = data.getParcelableArrayListExtra(
                FilePickerActivity.MEDIA_FILES)!!
            if (files.size != 0) {
                docpath=""
                for (i in 0 until files.size) {
                    val filePath = FetchPath.getPath(this, files[i].uri)
                    if (filePath!!.contains(".doc")
                        || filePath.contains(".docx") || filePath.contains(".pdf") || filePath.contains(".txt")) {
                        docpath=filePath
                        mtv_upload_trade_licence.visibility = View.GONE
                        rlUploadLicense_register_2.visibility = View.VISIBLE
                        document_uploaded = getString(R.string.document_uploaded)
                        txt2_register_2.text = getString(R.string.document_uploaded)

                        if(docpath.contains(".pdf")){
                            Glide.with(this).load(docpath).placeholder(R.drawable.pdfbox).into(imgAttach_register_2)
                        }
                        else if(docpath.contains(".doc") || docpath.contains(".docx") ){
                            Glide.with(this).load(docpath).placeholder(R.drawable.docbox).into(imgAttach_register_2)
                        }
                        else if(docpath.contains(".txt") ){
                            Glide.with(this).load(docpath).placeholder(R.drawable.txt).into(imgAttach_register_2)
                        }
                        else{
                            Glide.with(this).load(docpath).placeholder(R.drawable.txt).into(imgAttach_register_2)
                        }
                    }
                }
            }
        }
    }

    private fun validateAndSignUp() {
        if (selectCountryId=="0") {
            LogUtils.shortToast(this, getString(R.string.please_select_your_country))
        } else if(servedCountries.length()==0){
            LogUtils.shortToast(this, getString(R.string.please_select_the_countries_that_you_will_serve))
        }else if(!document_uploaded.equals(getString(R.string.document_uploaded))){
            LogUtils.shortToast(this, getString(R.string.please_upload_your_trade_license))
        }else if(!isChecked){
            LogUtils.shortToast(this, getString(R.string.please_accept_terms_conditions))
        }else {
            signUp()
        }
    }

    private fun signUp() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar_register_2.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("email", "password", "fcm_token", "device_type",
            "device_id", "name", "mobile", "country_code", "lang", "country_id", "countries_to_be_served"),
        arrayOf(businessEmail!!, password!!,SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""],
            ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""],
        businessName!!,businessContact!!,selectCountryCode!!,SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""],
            selectCountryId, servedCountries.toString()))
        if (profilePath != "") {
            val file = File(profilePath!!)
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            builder!!.addFormDataPart("profile_picture", file.name, requestBody)
        }

        if (docpath != "") {
            val file = File(docpath)
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            builder!!.addFormDataPart("document", file.name, requestBody)
        }


        val call = apiInterface.signUp(builder!!.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar_register_2.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if (jsonObject.getInt("response") == 1) {
                            val data = jsonObject.getJSONObject("data")
                            val bundle = Bundle()
                            bundle.putSerializable("accountList", accountList)
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            startActivity(Intent(this@RegisterActivity_2, ChooseCategoryActivity::class.java).putExtras(bundle))
                            finish()
                        }
                        else {
                            LogUtils.shortToast(this@RegisterActivity_2, jsonObject.getString("message"))
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
                LogUtils.shortToast(this@RegisterActivity_2, getString(R.string.check_internet))
                progressBar_register_2.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    override fun onBackPressed() {
        is_back = true
        super.onBackPressed()
    }
}