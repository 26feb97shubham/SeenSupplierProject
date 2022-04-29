package com.dev.ecomercesupplier.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev.ecomercesupplier.BuildConfig
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CustomSpinnerAdapter
import com.dev.ecomercesupplier.adapter.ServedCountriesAdapter
import com.dev.ecomercesupplier.custom.FetchPath
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.custom.Utility.Companion.showPassword
import com.dev.ecomercesupplier.interfaces.ClickInterface
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

class SignUpActivity : AppCompatActivity() {
    var name: String = ""
    var phone: String = ""
    var email: String = ""
    var password: String = ""
    var doubleClick:Boolean=false
    var confirmPassword: String = ""
    var accountHolderName:String=""
    var accountNumber:String=""
    var iban_number:String=""
    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val BANK_REQUEST_CODE = 44
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var uri: Uri? = null
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    val PICK_DOC = 11
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private val IMAGE_DIRECTORY_NAME = "Seen Supplier"
    private var profilePath = ""
    private var docpath = ""
    private var selectCountryCode = ""
    private var selectCountryId = "0"
    private var selectAccountTypeId = "0"
//    private var country_name=ArrayList<String>()
    private var country_code=ArrayList<String>()
//    private var accountStrList=ArrayList<String>()
    private var accountList=ArrayList<ModelForSpinner>()
    private var countryList=ArrayList<ModelForSpinner>()
    lateinit var adp_country_name: CustomSpinnerAdapter
//    lateinit var adp_country_code: ArrayAdapter<String>
    lateinit var adp_accountType: CustomSpinnerAdapter
    var isChecked: Boolean=false
    var profileOrDoc: Int=0
    var servedCountries=JSONArray()
    var cCodeList= arrayListOf<String>()
    lateinit var servedCountriesAdapter: ServedCountriesAdapter
    var servedCountriesList=ArrayList<ServeCountries>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
//        getCountires()
       /* getAccountTypes()*/
    }
    private fun setUpViews() {
        backImg.setOnClickListener {
            backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, backImg)
            onBackPressed()
        }

        scrollView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                edtName.clearFocus()
                edtEmail.clearFocus()
                edtPhone.clearFocus()
                edtPassword.clearFocus()
                edtConfirmPassword.clearFocus()
                return false
            }

        })

        iv_pass_show_hide_login.setOnClickListener {
            showPassword(iv_pass_show_hide_login, edtPassword)
        }

        iv_pass_show_hide_login_verify.setOnClickListener {
            showPassword(iv_pass_show_hide_login_verify, edtConfirmPassword)
        }

//        txtCountryCode.setOnClickListener {
//            if(cCodeList.size != 0){
//                showCountryCodeList()
//            }
//
//        }

        bankAccountDetails.setOnClickListener {
            bankAccountDetails.startAnimation(AlphaAnimation(1f, 0.5f))

           /* val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent = result.data
                    accountHolderName=data.getStringExtra("accountHolderName").toString()
                    accountNumber=data.getStringExtra("accountNumber").toString()
                    iban_number=data.getStringExtra("iban_number").toString()
                    bankAccountDetails.text = accountNumber

                }
            }*/

            val intent = Intent(this, BankAccountDeatilsActivity::class.java)
            intent.putExtra("accountHolderName", accountHolderName)
            intent.putExtra("accountNumber", accountNumber)
            intent.putExtra("iban_number", iban_number)
            startActivityForResult(intent, BANK_REQUEST_CODE)
//            resultLauncher.launch(intent)
        }


        btnSignUp.setOnClickListener {
            btnSignUp.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnSignUp)
            validateAndSignUp()
        }

        editProfile.setOnClickListener {
            editProfile.startAnimation(AlphaAnimation(1f, 0.5f))
            profileOrDoc=1
            requestToUploadProfilePhoto()
        }

        imgChk.setOnClickListener {
            imgChk.startAnimation(AlphaAnimation(1f, 0.5f))
            if(isChecked){
                isChecked=false
                imgChk.setImageResource(R.drawable.un_check)
            }
            else{
                isChecked=true
                imgChk.setImageResource(R.drawable.check)
            }
        }

        imgAttach.setOnClickListener {
            imgAttach.startAnimation(AlphaAnimation(1f, 0.5f))
            profileOrDoc=2
            requestToUploadProfilePhoto()

        }


        rvServedCountries.layoutManager=GridLayoutManager(this, 2)

        /*servedCountriesAdapter= ServedCountriesAdapter(this, servedCountriesList, is_back, object : ClickInterface.ClickArrayInterface{
            override fun clickArray(array: JSONArray) {
                servedCountries=array
                Log.e("servedCountries", servedCountries.toString())
            }

        })
        rvServedCountries.adapter=servedCountriesAdapter*/

        txtTermsConditions.setOnClickListener {
            txtTermsConditions.startAnimation(AlphaAnimation(1f, 0.5f))
            startActivity(Intent(this, TermsAndConditionsActivity::class.java).putExtra("title", getString(R.string.terms_amp_conditions)))
        }


        adp_country_name = CustomSpinnerAdapter(this, countryList, "countryList")
        spinnerCountry.adapter = adp_country_name

        spinnerCountry.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectCountryId=countryList[p2].id.toString()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectCountryId=""
            }


        }

        adp_accountType = CustomSpinnerAdapter(this, accountList, "accountList")
        spinnerAccountType.adapter = adp_accountType

        spinnerAccountType.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectAccountTypeId= accountList[p2].id.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectAccountTypeId="0"
            }


        }

        edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val pass =edtPassword.text.toString()

                if(!TextUtils.isEmpty(pass)){
                    if(!pass.equals(charSeq.toString(), false)){
                        edtConfirmPassword.error=getString(R.string.password_doesnt_match_with_confirm_password)
                    }
                }
                else{
                    edtPassword.error=getString(R.string.please_first_enter_your_password)
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }
    private fun openCameraDialog2() {
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

    /*private fun chooseImage2() {
        val intent=Intent(this, FilePickerActivity::class.java)
        intent.putExtra(FilePickerActivity.CONFIGS,  Configurations.Builder()
            .setCheckPermission(true)
            .setShowImages(true)
            .setShowFiles(false)
            .setShowVideos(false)
            .setMaxSelection(1)
            .setSkipZeroSizeFiles(true)
            .build())
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)
    }*/
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

    private fun getCountires() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getCountries(builder.build())
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
                            cCodeList.add(jsonObj.getString("country_name") + " ("+jsonObj.getString("country_code")+")")
                        }
//                        txtCountryCode.text=country_code[0]
                        adp_country_name.notifyDataSetChanged()

                        val account_types = jsonObject.getJSONArray("account_types")
                        accountList.clear()
                        val m = ModelForSpinner()
                        m.id=0
                        m.name=getString(R.string.select_category)
                        accountList.add(m)
                        for (i in 0 until account_types.length()) {
                            val jsonObj = account_types.getJSONObject(i)
                            val m1 = ModelForSpinner()
                            m1.id=jsonObj.getInt("id")
                            m1.name=jsonObj.getString("name")
                            accountList.add(m1)
                        }
                        adp_accountType.notifyDataSetChanged()

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
                        servedCountriesAdapter.notifyDataSetChanged()
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
                LogUtils.shortToast(this@SignUpActivity, getString(R.string.check_internet))
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun showCountryCodeList() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select country code")
        builder.setItems(cCodeList.toArray(arrayOfNulls<String>(cCodeList.size))) { dialogInterface, i ->
            txtCountryCode.text=country_code[i]
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
    private fun validateAndSignUp() {
        name = edtName.text.toString()
        phone = edtPhone.text.toString()
        email = edtEmail.text.toString()
        password= edtPassword.text.toString()
        confirmPassword= edtConfirmPassword.text.toString()
        selectCountryCode= txtCountryCode.text.toString()

        if (TextUtils.isEmpty(name)) {
            scrollView.scrollTo(0, 150)
            edtName.requestFocus()
            edtName.error=getString(R.string.please_enter_your_company_name)
//            LogUtils.shortToast(this, getString(R.string.please_eRkjRR

        }
        else if (!SharedPreferenceUtility.getInstance().isCharacterAllowed(name)) {
            scrollView.scrollTo(0, 150)
            edtName.requestFocus()
            edtName.error=getString(R.string.emojis_are_not_allowed)
//              LogUtils.shortToast(this, getString(R.string.emojis_are_not_allowed))
        }
        else if (!TextUtils.isEmpty(email) && !SharedPreferenceUtility.getInstance().isEmailValid(email)) {
            scrollView.scrollTo(0, 180)
            edtEmail.requestFocus()
            edtEmail.error=getString(R.string.please_enter_valid_email)
//            LogUtils.shortToast(this, getString(R.string.please_enter_valid_email))
        }
        else if (TextUtils.isEmpty(selectCountryCode)) {
//            edtPhone.error=getString(R.string.please_select_your_country_code)
            LogUtils.shortToast(this, getString(R.string.please_select_your_country_code))

        }
        else if (TextUtils.isEmpty(phone)) {
            scrollView.scrollTo(0, 210)
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.please_enter_your_phone_number)
//             LogUtils.shortToast(this, getString(R.string.please_enter_your_mob_number))

        }
        else if ((phone.length < 7 || phone.length > 15)) {
            scrollView.scrollTo(0, 210)
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.mob_num_length_valid)
//             LogUtils.shortToast(this, getString(R.string.mob_num_length_valid))
        }
        else if(TextUtils.isEmpty(accountHolderName)){
            LogUtils.shortToast(this, getString(R.string.please_enter_your_bank_account_details))
        }
        else if (selectAccountTypeId=="0") {
//            edtPhone.error=getString(R.string.please_select_your_country_code)
            LogUtils.shortToast(this, getString(R.string.please_select_your_account_actegory))

        }
        else if (TextUtils.isEmpty(password)) {
            edtPassword.error=getString(R.string.please_enter_your_password)
            edtPassword.requestFocus()
            scrollView.scrollTo(0, 240)
//            LogUtils.shortToast(this, getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(password)) {
            scrollView.scrollTo(0, 240)
            edtPassword.requestFocus()
            edtPassword.error=getString(R.string.password_length_valid)
//            LogUtils.shortToast(this, getString(R.string.password_length_valid))
        }
        else if (TextUtils.isEmpty(confirmPassword)) {
            scrollView.scrollTo(0, 270)
            edtConfirmPassword.requestFocus()
            edtConfirmPassword.error=getString(R.string.please_verify_your_password)
//            LogUtils.shortToast(this, getString(R.string.please_verify_your_password))
        }
        /* else if (confirmPassword.length < 6) {
             edtConfirmPassword.error=getString(R.string.verify_password_length_valid)
 //            LogUtils.shortToast(this, getString(R.string.verify_password_length_valid))

         }*/
        else if (!confirmPassword.equals(password)) {
            scrollView.scrollTo(0, 270)
            edtConfirmPassword.requestFocus()
            edtConfirmPassword.error=getString(R.string.password_doesnt_match_with_confirm_password)
//            LogUtils.shortToast(this, getString(R.string.password_doesnt_match_with_verify_password))
        }

        else if (selectCountryId=="0") {
//            edtPhone.error=getString(R.string.please_select_your_country_code)
            LogUtils.shortToast(this, getString(R.string.please_select_your_country_name))

        }

        else if(servedCountries.length()==0){
            LogUtils.shortToast(this, getString(R.string.please_select_the_countries_that_you_will_serve))
        }

        else if(!isChecked){
            LogUtils.shortToast(this, getString(R.string.please_accept_terms_conditions))
        }

        else if(TextUtils.isEmpty(docpath)){
            LogUtils.shortToast(this, getString(R.string.please_upload_your_trade_license))
        }

        else {
            getSignUp()
        }
    }
    private fun getSignUp() {

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("email", "password", "fcm_token", "device_type", "device_id", "name", "mobile", "country_code"
            , "lang", "account_holder_name", "account_number", "iban", "account_type", "country_id", "countries_to_be_served"),
            arrayOf(email.trim({ it <= ' ' }), password.trim({ it <= ' ' }),
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""], ApiUtils.DeviceType, name.trim { it <= ' ' }
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""],phone.trim({ it <= ' ' })
                , selectCountryCode, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]
                , accountHolderName, accountNumber, iban_number, selectAccountTypeId, selectCountryId, servedCountries.toString()))

        if (profilePath != "") {
            val file = File(profilePath)
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
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if (jsonObject.getInt("response") == 1) {

//                            LogUtils.shortToast(this@SignUpActivity, jsonObject.getString("message"))
                            val data = jsonObject.getJSONObject("data")
                          /*  SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))*/
                            startActivity(Intent(this@SignUpActivity, OtpVerificationActivity::class.java).putExtra("ref", "1")
                                .putExtra("user_id", data.getInt("user_id").toString()))
                            finish()

                        }
                        else {
                            LogUtils.shortToast(this@SignUpActivity, jsonObject.getString("message"))
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
                LogUtils.shortToast(this@SignUpActivity, getString(R.string.check_internet))
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

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


    fun requestToUploadProfilePhoto() {
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE)
        } else if (hasPermissions(this, *PERMISSIONS)) {
            if(profileOrDoc==1){
                openCameraDialog()
            }
            else{
                openCameraDialog2()
            }

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
                    if(profileOrDoc==1){
                        openCameraDialog()
                    }
                    else{
                        openCameraDialog2()
                    }
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
            if (resultCode == Activity.RESULT_OK) { //previewCapturedImage();
                if (uri != null) {
                    if(profileOrDoc==1){
                        profilePath = ""
                        Log.e("uri", uri.toString())
                        profilePath = uri!!.path!!
                        Glide.with(this).load("file:///$profilePath").placeholder(R.drawable.user).into(img)
                    }
                    else{
                        docpath = ""
                        docpath = uri!!.path!!
                        txt2.text = getString(R.string.document_uploaded)

                        Glide.with(this).load("file:///$docpath").placeholder(R.drawable.attach).into(imgAttach)
                    }

                } else {
                    LogUtils.shortToast(this, getString(R.string.something_went_wrong))
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                if(profileOrDoc==1){
                    profilePath = ""
                    val uri = data.data
                    profilePath = if (uri.toString().startsWith("content")) {
                        FetchPath.getPath(this, uri!!)!!
                    } else {
                        uri!!.path!!
                    }
                    Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load("file:///$profilePath").into(img)
                }
                else{
                    docpath = ""
                    val uri = data.data
                    docpath = if (uri.toString().startsWith("content")) {
                        FetchPath.getPath(this, uri!!)!!
                    } else {
                        uri!!.path!!
                    }
                    txt2.text = getString(R.string.document_uploaded)

                    Glide.with(this).load("file:///$docpath").placeholder(R.drawable.attach).into(imgAttach)
                }

            }
        }
        else if (requestCode == PICK_DOC && resultCode == Activity.RESULT_OK && data != null) {
            val files: java.util.ArrayList<MediaFile> = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)!!
//            getPath(files)
            if (files.size != 0) {
                docpath=""
                for (i in 0 until files.size) {
                   /* val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, File(files[i].toString()))
                    Log.e("uri", uri.toString())*/
                    val filePath = FetchPath.getPath(this, files[i].uri)
                    if (filePath!!.contains(".doc")
                        || filePath.contains(".docx") || filePath.contains(".pdf") || filePath.contains(".txt")) {
                        docpath=filePath
                        txt2.text = getString(R.string.document_uploaded)

                        if(docpath.contains(".pdf")){
                            Glide.with(this).load(docpath).placeholder(R.drawable.pdfbox).into(imgAttach)
                        }
                        else if(docpath.contains(".doc") || docpath.contains(".docx") ){
                            Glide.with(this).load(docpath).placeholder(R.drawable.docbox).into(imgAttach)
                        }
                        else if(docpath.contains(".txt") ){
                            Glide.with(this).load(docpath).placeholder(R.drawable.txt).into(imgAttach)
                        }
                        else{
                            Glide.with(this).load(docpath).placeholder(R.drawable.txt).into(imgAttach)
                        }
                    }
                }
            }
        }
        else if(requestCode == BANK_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            accountHolderName=data.getStringExtra("accountHolderName").toString()
            accountNumber=data.getStringExtra("accountNumber").toString()
            iban_number=data.getStringExtra("iban_number").toString()
            bankAccountDetails.text = accountNumber
        }

    }
    /*fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id: String = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                        split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }*/

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
                column
        )
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs,
                    null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun spinnerAccountTypeisMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
/*    fun getPath(files: java.util.ArrayList<MediaFile>) {
      *//*  val externalStorageDirectory = Environment.getExternalStorageDirectory()
        val folder = File(externalStorageDirectory.absolutePath + "/FilePath")
        val file = folder.listFiles()*//*
        if (files.size != 0) {
            docpath=""
            for (i in files.indices) {
                val filePath = files[i].path
                if (filePath.contains(".doc")
                        || filePath.contains(".docx") || filePath.contains(".pdf") || filePath.contains(".txt")) {
                    docpath=filePath
                    txt2.text = getString(R.string.document_uploaded)

                    if(docpath.contains(".pdf")){
                        Glide.with(this).load(docpath).placeholder(R.drawable.pdfbox).into(imgAttach)
                    }
                    else if(docpath.contains(".doc") || docpath.contains(".docx") ){
                        Glide.with(this).load(docpath).placeholder(R.drawable.docbox).into(imgAttach)
                    }
                    else if(docpath.contains(".txt") ){
                        Glide.with(this).load(docpath).placeholder(R.drawable.txt).into(imgAttach)
                    }
                    else{
                        Glide.with(this).load(docpath).placeholder(R.drawable.txt).into(imgAttach)
                    }
                }
            }

        } else {
            //no file available
        }
    }*/

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

    override fun onBackPressed() {
        exitApp()
    }
}