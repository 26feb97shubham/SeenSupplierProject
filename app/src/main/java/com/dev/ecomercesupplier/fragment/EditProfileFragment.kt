package com.dev.ecomercesupplier.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev.ecomercesupplier.BuildConfig
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.activity.ChooseLoginSignUpActivity
import com.dev.ecomercesupplier.adapter.CustomDropdownCountryNameAdapter
import com.dev.ecomercesupplier.adapter.ServedCountriesAdapter
import com.dev.ecomercesupplier.custom.FetchPath
import com.dev.ecomercesupplier.custom.Utility
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
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView:View
    var name: String = ""
    var phone: String = ""
    var email: String = ""
    var bio: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var bankDet: String = ""
    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
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
    private var selectCountryId = ""
    private var selectAccountTypeId = ""
    private var country_name=ArrayList<String>()
    private var country_code=ArrayList<String>()
    private var countryList=ArrayList<ModelForSpinner>()
    lateinit var adp_country_name: ArrayAdapter<String>
    lateinit var adp_country_code: ArrayAdapter<String>
    var profileOrDoc: Int=0
    var servedCountries= JSONArray()
    var responseData:String=""
    var profile_picture:String=""
    lateinit var servedCountriesAdapter: ServedCountriesAdapter
    var servedCountriesList=ArrayList<ServeCountries>()
    var cCodeList= arrayListOf<String>()
    var registered_number=""
    var registered_country_code=""
    var is_back = false
    lateinit var customDropdownCountryNameAdapter : CustomDropdownCountryNameAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            responseData = it.getString("responseData").toString()

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        Utility.setLanguage(requireContext(), SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        getCountries()
        return mView
    }
    private fun setUpViews() {
        is_back = false
        requireActivity().other_frag_backImg.visibility = View.VISIBLE
        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            is_back = true
            findNavController().popBackStack()
        }

        underlineForChangePass()
        underlineForUpdateTradeLicense()

        mView.editProfile.setOnClickListener {
            mView.editProfile.startAnimation(AlphaAnimation(1f, 0.5f))
            profileOrDoc=1
            requestToUploadProfilePhoto()
        }

        mView.scrollView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                mView.edtName.clearFocus()
                mView.edtEmail.clearFocus()
                mView.edtPhone.clearFocus()
                mView.edtBio.clearFocus()
                return false
            }

        })

        mView.btnSubmit.setOnClickListener {
            mView.btnSubmit.startAnimation(AlphaAnimation(1f, 0.5f))
            validateAndEdit()
        }

        mView.txtChangePass.setOnClickListener {
            mView.txtChangePass.startAnimation(AlphaAnimation(1f, 0.5f))
            val args= Bundle()
            args.putString("profile_picture", profile_picture)
            findNavController().navigate(R.id.action_editProfileFragment_to_changePasswordFragment, args)
        }


        mView.imgAttach.setOnClickListener {
            mView.imgAttach.startAnimation(AlphaAnimation(1f, 0.5f))
            profileOrDoc=2
            requestToUploadProfilePhoto()

        }

        mView.txtUpdateLicense.setOnClickListener {
            mView.txtUpdateLicense.startAnimation(AlphaAnimation(1f, 0.5f))
            profileOrDoc=2
            requestToUploadProfilePhoto()

        }
        mView.rvServedCountries.layoutManager= GridLayoutManager(requireContext(), 2)
        servedCountriesAdapter= ServedCountriesAdapter(
            requireContext(),
            servedCountriesList,
            is_back,
            object : ClickInterface.ClickArrayInterface{
                override fun clickArray(array: JSONArray, nameArrayType : JSONArray) {
                    servedCountries=array
                    Log.e("servedCountries", servedCountries.toString())
                }

            })
        mView.rvServedCountries.adapter=servedCountriesAdapter

       /* mView.radUAE.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    servedCountries.put(1)
                }
                else{
                    for(i in 0 until servedCountries.length()){
                        if(servedCountries[i]==1){
                            servedCountries.remove(i)
                            break
                        }
                    }
                }

            }

        })
        mView.radSA.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    servedCountries.put(2)
                }
                else{
                    for(i in 0 until servedCountries.length()){
                        if(servedCountries[i]==2){
                            servedCountries.remove(i)
                            break
                        }
                    }
                }

            }

        })
        mView.radOman.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    servedCountries.put(3)
                }
                else{
                    for(i in 0 until servedCountries.length()){
                        if(servedCountries[i]==3){
                            servedCountries.remove(i)
                            break
                        }
                    }
                }

            }

        })
        mView.radKuwait.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    servedCountries.put(4)
                }
                else{
                    for(i in 0 until servedCountries.length()){
                        if(servedCountries[i]==4){
                            servedCountries.remove(i)
                            break
                        }
                    }
                }

            }

        })
        mView.radBahrain.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    servedCountries.put(5)
                }
                else{
                    for(i in 0 until servedCountries.length()){
                        if(servedCountries[i]==5){
                            servedCountries.remove(i)
                            break
                        }
                    }
                }

            }

        })*/


        adp_country_code = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, country_code)
        adp_country_code.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        /*mView.spinner.adapter = adp_country_code

        mView.spinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectCountryCode=mView.spinner.selectedItem.toString()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectCountryCode=""
            }


        }*/
    /*    mView.txtCountryCode.setOnClickListener {
            if(cCodeList.size != 0){
                showCountryCodeList()
            }

        }*/

        customDropdownCountryNameAdapter = CustomDropdownCountryNameAdapter(requireContext(), country_name)
       /* adp_country_name = ArrayAdapter(requireContext(), R.layout.spinner_item, country_name)
        adp_country_name.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)*/
        mView.spinnerCountry.adapter = customDropdownCountryNameAdapter

        mView.spinnerCountry.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                for(i in 0 until countryList.size){
                    if(countryList[i].name.equals(mView.spinnerCountry.selectedItem.toString(), false)){
                        selectCountryId=countryList[i].id.toString()
                    }

                }



            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectCountryId=""
            }


        }

    }
    private fun underlineForChangePass() {
        val underline = SpannableString(requireContext().getString(R.string.change_password_caps))
        underline.setSpan(UnderlineSpan(), 0, underline.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mView.txtChangePass.text=underline
    }
    private fun underlineForUpdateTradeLicense() {
        val underline = SpannableString(requireContext().getString(R.string.update_trade_license))
        underline.setSpan(UnderlineSpan(), 0, underline.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mView.txtUpdateLicense.text=underline
    }
    private fun showCountryCodeList() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select country code")
        builder.setItems(cCodeList.toArray(arrayOfNulls<String>(cCodeList.size))) { dialogInterface, i ->
            if( mView.txtCountryCode.text.toString()!=country_code[i]){
                mView.txtCountryCode.text=country_code[i]
                mView.edtPhone.setText("")
            }
        }


        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
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
    private fun getProfile() {
        try {
            if (!TextUtils.isEmpty(responseData)) {
                val jsonObject = JSONObject(responseData)

                if(jsonObject.getInt("response")==1){
                    val data= jsonObject.getJSONObject("data")
                    mView.edtName.setText( data.getString("name"))
                    mView.edtEmail.setText(data.getString("email"))
                    registered_number=data.getString("mobile")
                    mView.edtPhone.setText(registered_number)
                    mView.edtBio.setText(data.getString("bio"))
                    registered_country_code=data.getString("country_code")
                    for(i in 0 until country_code.size){
                        if(country_code[i]==registered_country_code){
                            mView.txtCountryCode.text=country_code[i]
                        }
                    }
                    for(i in 0 until countryList.size){

                        if(countryList[i].id==data.getInt("country_id")){
                            mView.spinnerCountry.setSelection(i)
                        }
                    }

                    servedCountries=data.getJSONArray("countries_to_be_served")
                    ServedCountriesAdapter.servedCountries=JSONArray()
                    ServedCountriesAdapter.servedCountries=servedCountries
                    servedCountriesAdapter.notifyDataSetChanged()

                    profile_picture=data.getString("profile_picture")
                    Glide.with(requireContext()).load(profile_picture).placeholder(R.drawable.user).into(mView.img)
                    if(data.getString("document").contains(".pdf")){
                        Glide.with(requireContext()).load(data.getString("document")).placeholder(R.drawable.pdfbox).into(mView.imgAttach)
                    }
                    else if(data.getString("document").contains(".doc") || data.getString("document").contains(".docx") ){
                        Glide.with(requireContext()).load(data.getString("document")).placeholder(R.drawable.docbox).into(mView.imgAttach)
                    }
                    else if(data.getString("document").contains(".txt") ){
                        Glide.with(requireContext()).load(data.getString("document")).placeholder(R.drawable.txt).into(mView.imgAttach)
                    }
                    else{
                        Glide.with(requireContext()).load(data.getString("document")).placeholder(R.drawable.txt).into(mView.imgAttach)
                    }

                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.getProfile(builder?.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            val data= jsonObject.getJSONObject("data")
                            mView.edtName.setText( data.getString("name"))
                            mView.edtEmail.setText(data.getString("email"))
                            mView.edtPhone.setText(data.getString("mobile"))
                            mView.edtBio.setText(data.getString("bio"))
                            for(i in 0 until country_code.size){
                                if(country_code[i]==data.getString("country_code")){
                                    mView.spinner.setSelection(i)
                                }
                            }
                            for(i in 0 until countryList.size){

                                if(countryList[i].id==data.getInt("country_id")){
                                    mView.spinnerCountry.setSelection(i)
                                }
                            }

                           val countries_to_be_served=data.getString("countries_to_be_served")
                            if(!TextUtils.isEmpty(countries_to_be_served)){
                                val countryArray=JSONArray(countries_to_be_served)
                                for(i in 0 until countryArray.length()){
                                    if(countryArray[i]==1){
                                        mView.radUAE.isChecked=true
                                    }
                                    else if(countryArray[i]==2){
                                        mView.radSA.isChecked=true
                                    }
                                    else if(countryArray[i]==3){
                                        mView.radOman.isChecked=true
                                    }
                                    else if(countryArray[i]==4){
                                        mView.radKuwait.isChecked=true
                                    }
                                    else if(countryArray[i]==5){
                                        mView.radBahrain.isChecked=true
                                    }
                                }
                            }
                            Glide.with(requireContext()).load(data.getString("profile_picture")).placeholder(R.drawable.user).into(mView.img)
                            Glide.with(requireContext()).load(data.getString("document")).placeholder(R.drawable.attach).into(mView.imgAttach)
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
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })*/
    }
    private fun openCameraDialog2() {
        val items = arrayOf<CharSequence>(getString(R.string.camera), getString(R.string.gallery), getString(R.string.document), getString(R.string.cancel))
        val builder = AlertDialog.Builder(requireContext())
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
        val intent= Intent(requireContext(), FilePickerActivity::class.java)
        intent.putExtra(
            FilePickerActivity.CONFIGS, Configurations.Builder()
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

    private fun getCountries() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getCountries(builder.build())

        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val countries = jsonObject.getJSONArray("countries")
                        country_name.clear()
                        country_code.clear()
                        countryList.clear()
                        cCodeList.clear()
                        for (i in 0 until countries.length()) {
                            val jsonObj = countries.getJSONObject(i)
                            country_name.add(jsonObj.getString("country_name"))
                            country_code.add(jsonObj.getString("country_code"))

                            val s= ModelForSpinner()
                            s.id=jsonObj.getInt("id")
                            s.name=jsonObj.getString("country_name")
                            countryList.add(s)

                            cCodeList.add(jsonObj.getString("country_name") + " ("+jsonObj.getString("country_code")+")")
                        }
//                        txtCountryCode.text=country_code[0]
                        adp_country_code.notifyDataSetChanged()
                        customDropdownCountryNameAdapter.notifyDataSetChanged()

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
                getProfile()
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun validateAndEdit() {
        name = mView.edtName.text.toString()
        phone = mView.edtPhone.text.toString()
        email = mView.edtEmail.text.toString()
        bio = mView.edtBio.text.toString()

        if (TextUtils.isEmpty(name)) {
            mView.edtName.requestFocus()
            mView.edtName.error=getString(R.string.please_enter_your_company_name)
//            LogUtils.shortToast(this, getString(R.string.please_eRkjRR

        }
        else if (!isCharacterAllowed(name)) {
            mView.edtName.requestFocus()
            mView.edtName.error=getString(R.string.emojis_are_not_allowed)
//              LogUtils.shortToast(this, getString(R.string.emojis_are_not_allowed))
        }
        else if (!TextUtils.isEmpty(email) && !SharedPreferenceUtility.getInstance().isEmailValid(email)) {
            mView.edtEmail.requestFocus()
            mView.edtEmail.error=getString(R.string.please_enter_valid_email)
//            LogUtils.shortToast(this, getString(R.string.please_enter_valid_email))
        }
     /*   else if (TextUtils.isEmpty(selectCountryCode)) {
//            edtPhone.error=getString(R.string.please_select_your_country_code)
            LogUtils.shortToast(requireContext(), getString(R.string.please_select_your_country_code))

        }*/
        else if (TextUtils.isEmpty(phone)) {
            mView.edtPhone.requestFocus()
            mView.edtPhone.error=getString(R.string.please_enter_your_phone_number)
//             LogUtils.shortToast(this, getString(R.string.please_enter_your_mob_number))

        }
        else if ((phone.length < 7 || phone.length > 15)) {
            mView.edtPhone.requestFocus()
            mView.edtPhone.error=getString(R.string.mob_num_length_valid)
//             LogUtils.shortToast(this, getString(R.string.mob_num_length_valid))
        }

        else if (TextUtils.isEmpty(selectCountryId)) {
//            edtPhone.error=getString(R.string.please_select_your_country_code)
            LogUtils.shortToast(requireContext(), getString(R.string.please_select_your_country_name))

        }

        else if(servedCountries.length()==0){
            LogUtils.shortToast(requireContext(), getString(R.string.please_select_the_countries_that_you_will_serve))
        }


        /*else if(TextUtils.isEmpty(docpath)){
            LogUtils.shortToast(requireContext(), getString(R.string.please_upload_your_trade_license))
        }*/

        else {
            if(registered_number!=phone){
                val builder = android.app.AlertDialog.Builder(requireContext())
                builder.setTitle(requireContext().getString(R.string.alert))
                builder.setMessage(requireContext().getString(R.string.are_you_sure_you_want_to_update_your_phone_number))
                builder.setPositiveButton(R.string.ok) { dialog, which ->
                    dialog.cancel()
                    editProfile()
                }
                builder.setNegativeButton(R.string.cancel) { dialog, which ->
                    dialog.cancel()
                    phone=registered_number
                    selectCountryCode=registered_country_code
                    editProfile()
                }
                builder.show()
            }
            else {
                editProfile()
            }
        }
    }
    private fun isCharacterAllowed(validateString: String): Boolean {
        var containsInvalidChar = false
        for (i in 0 until validateString.length) {
            val type = Character.getType(validateString[i])
            containsInvalidChar = !(type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt())
        }
        return containsInvalidChar
    }
    private fun editProfile() {

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("user_id", "email", "fcm_token", "device_type", "name", "mobile", "country_code"
            , "lang", "country_id", "countries_to_be_served", "bio"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), email.trim({ it <= ' ' })
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""], ApiUtils.DeviceType, name.trim { it <= ' ' }, phone.trim({ it <= ' ' })
                , selectCountryCode, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""], selectCountryId, servedCountries.toString(), bio))

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


        val call = apiInterface.editProfile(builder!!.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        when {
                            jsonObject.getInt("response") == 1 -> {
                                LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                                findNavController().popBackStack()
                            }
                            jsonObject.getInt("response") == 2 -> {
                                LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                                SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.UserId)
                                SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.IsLogin)
                                startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
                                requireActivity().finishAffinity()
                            }
                            else -> {
                                LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            }
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
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }


    fun requestToUploadProfilePhoto() {
        if (!hasPermissions(requireContext(), *PERMISSIONS)) {
           requestPermissions(PERMISSIONS, PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE)
        } else if (hasPermissions(requireContext(), *PERMISSIONS)) {
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
        val builder = AlertDialog.Builder(requireContext())
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
            FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID.toString() + ".provider", getOutputMediaFile(type)!!)
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
                    LogUtils.shortToast(requireContext(), "Please grant both Camera and Storage permissions")

                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                LogUtils.shortToast(requireContext(), "Please grant both Camera and Storage permissions")
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
                        Glide.with(this).load("file:///$profilePath").placeholder(R.drawable.user).into(mView.img)
                    }
                    else{
                        docpath = ""
                        docpath = uri!!.path!!
//                        mView.txtUpdateLicense.text = getString(R.string.document_updated)
                        LogUtils.shortToast(requireContext(), getString(R.string.document_uploaded_successfully))
                        Glide.with(this).load("file:///$docpath").placeholder(R.drawable.attach).into(mView.imgAttach)
                    }

                } else {
                    LogUtils.shortToast(requireContext(), requireContext().getString(R.string.something_went_wrong))
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                if(profileOrDoc==1){
                    profilePath = ""
                    val uri = data.data
                    profilePath = if (uri.toString().startsWith("content")) {
                        FetchPath.getPath(requireContext(), uri!!)!!
                    } else {
                        uri!!.path!!
                    }
                    Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load("file:///$profilePath").into(mView.img)

                }
                else{
                    docpath = ""
                    val uri = data.data
                    docpath = if (uri.toString().startsWith("content")) {
                        FetchPath.getPath(requireContext(), uri!!)!!
                    } else {
                        uri!!.path!!
                    }
//                    mView.txtUpdateLicense.text = getString(R.string.document_updated)
                    LogUtils.shortToast(requireContext(), getString(R.string.document_updated_successfully))
                    Glide.with(this).load("file:///$docpath").placeholder(R.drawable.attach).into(mView.imgAttach)
                }

            }
        }
        else if (requestCode == PICK_DOC && resultCode == Activity.RESULT_OK && data != null) {
            val files: java.util.ArrayList<MediaFile> = data.getParcelableArrayListExtra(
                FilePickerActivity.MEDIA_FILES)!!
            if (files.size != 0) {
                docpath=""
                for (i in 0 until files.size) {
                    val filePath = FetchPath.getPath(requireContext(), files[i].uri)
                    if (filePath!!.contains(".doc")
                        || filePath.contains(".docx") || filePath.contains(".pdf") || filePath.contains(".txt")) {
                        docpath=filePath
//                        mView.txtUpdateLicense.text = getString(R.string.document_updated)
                        LogUtils.shortToast(requireContext(), getString(R.string.document_updated_successfully))
                        if(docpath.contains(".pdf")){
                            Glide.with(requireContext()).load(docpath).placeholder(R.drawable.pdfbox).into(mView.imgAttach)
                        }
                        else if(docpath.contains(".doc") || docpath.contains(".docx") ){
                            Glide.with(requireContext()).load(docpath).placeholder(R.drawable.docbox).into(mView.imgAttach)
                        }
                        else if(docpath.contains(".txt") ){
                            Glide.with(requireContext()).load(docpath).placeholder(R.drawable.txt).into(mView.imgAttach)
                        }
                        else{
                            Glide.with(requireContext()).load(docpath).placeholder(R.drawable.txt).into(mView.imgAttach)
                        }
                    }
                }
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                EditProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}