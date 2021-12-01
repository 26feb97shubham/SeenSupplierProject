package com.dev.ecomercesupplier.fragment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev.ecomercesupplier.BuildConfig
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.FetchPath
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_add_coupon.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
 * Use the [AddCouponFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddCouponFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    var type: String="1"
    var params: String=""
    var title: String=""
    var fromTime: String=""
    var toTime: String=""
    var from_date: String=""
    var to_date: String=""
    var description: String=""
    var perDesc: String=""
    var minTrans: String=""
    var maxCouponAmt: String=""
    var code: String=""
    var coupon_id: String="0"
    var picture: String=""
    private val IMAGE_DIRECTORY_NAME = "Seen Supplier"
    private var imgPath = ""
    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var uri: Uri? = null
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private var mYear = 0
    private  var mMonth:Int = 0
    private  var mDay:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString("type").toString()
            coupon_id = it.getInt("id").toString()
            title = it.getString("title").toString()
            description = it.getString("description").toString()
            code = it.getString("code").toString()
            fromTime = it.getString("from_date").toString()
            toTime = it.getString("to_date").toString()
            minTrans = it.getString("min_price").toString()
            maxCouponAmt = it.getString("max_discount_price").toString()
            perDesc = it.getString("percentage").toString()
            picture = it.getString("picture").toString()

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_add_coupon, container, false)
        setUpViews()
        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility = View.VISIBLE


        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        if(type=="3"){
            mView.header.text=getString(R.string.my_coupon)
            mView.editProfile.isEnabled=false
            mView.title.isEnabled=false
            mView.description.isEnabled=false
            mView.llFrom.isEnabled=false
            mView.llTo.isEnabled=false
            mView.perDesc.isEnabled=false
            mView.minTrans.isEnabled=false
            mView.maxCouponAmt.isEnabled=false
            mView.code.isEnabled=false
            mView.btnSave.visibility=View.GONE
        }
        else if(type=="2"){
            mView.header.text=getString(R.string.edit_coupon)
        }

        mView.title.setText(title)
        mView.description.setText(description)
        mView.fromTime.setText(fromTime)
        mView.toTime.setText(toTime)
        mView.perDesc.setText(perDesc)
        mView.minTrans.setText(minTrans)
        mView.maxCouponAmt.setText(maxCouponAmt)
        mView.code.setText(code)
        Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load(picture).into(mView.img)

        mView.editProfile.setOnClickListener {
            mView.editProfile.startAnimation(AlphaAnimation(1f, 0.5f))
            requestToUploadProfilePhoto()
        }

        mView.btnSave.setOnClickListener {
            mView.btnSave.startAnimation(AlphaAnimation(1f, 0.5f))
            validateAndSave()
        }


        mView.perDesc.doOnTextChanged { s, start, before, count ->
            if (s.toString().length > 3 && s.toString().contains(".")) {
                if (s.toString().length - s.toString().indexOf(".") > 3) {
                    mView.perDesc.setText(s.toString().substring(0, s.toString().length -1))
                    mView.perDesc.setSelection(mView.perDesc.text!!.length)
                }

            }
            else  if (s.toString().length > 2 && !s.toString().contains(".")) {
                mView.perDesc.setText(s.toString().substring(0, s.toString().length -1))
                mView.perDesc.setSelection(mView.perDesc.text!!.length)

            }
            /*else  if(s.toString().length>5){
                mView.perDisc.setText(s.toString().substring(0, s.toString().length -1))
                mView.perDisc.setSelection(mView.perDisc.text!!.length)
            }*/
            else if(s.toString().length==1 && s.toString().contains("0")){
                mView.perDesc.setText(s.toString().substring(0, s.toString().length -1))
                mView.perDesc.setSelection(mView.perDesc.text!!.length)
            }
            else  if(s.toString().length==2 && s.toString().contains(".0")){
                mView.perDesc.setText(s.toString().substring(0, s.toString().length -1))
                mView.perDesc.setSelection(mView.perDesc.text!!.length)
            }
        }

        mView.minTrans.doOnTextChanged { s, start, before, count ->
            if (s.toString().length > 3 && s.toString().contains(".")) {
                if (s.toString().length - s.toString().indexOf(".") > 3) {
                    mView.minTrans.setText(s.toString().substring(0, s.toString().length -1))
                    mView.minTrans.setSelection(mView.minTrans.text!!.length)
                }

            }
            else  if(s.toString().length>8){
                mView.minTrans.setText(s.toString().substring(0, s.toString().length -1))
                mView.minTrans.setSelection(mView.minTrans.text!!.length)
            }
            else if(s.toString().length==1 && s.toString().contains("0")){
                mView.minTrans.setText(s.toString().substring(0, s.toString().length -1))
                mView.minTrans.setSelection(mView.minTrans.text!!.length)
            }
            else  if(s.toString().length==2 && s.toString().contains(".0")){
                mView.minTrans.setText(s.toString().substring(0, s.toString().length -1))
                mView.minTrans.setSelection(mView.minTrans.text!!.length)
            }
        }

        mView.maxCouponAmt.doOnTextChanged { s, start, before, count ->
            if (s.toString().length > 3 && s.toString().contains(".")) {
                if (s.toString().length - s.toString().indexOf(".") > 3) {
                    mView.maxCouponAmt.setText(s.toString().substring(0, s.toString().length -1))
                    mView.maxCouponAmt.setSelection(mView.maxCouponAmt.text!!.length)
                }

            }
            else  if(s.toString().length>8){
                mView.maxCouponAmt.setText(s.toString().substring(0, s.toString().length -1))
                mView.maxCouponAmt.setSelection(mView.maxCouponAmt.text!!.length)
            }
            else if(s.toString().length==1 && s.toString().contains("0")){
                mView.maxCouponAmt.setText(s.toString().substring(0, s.toString().length -1))
                mView.maxCouponAmt.setSelection(mView.maxCouponAmt.text!!.length)
            }
            else  if(s.toString().length==2 && s.toString().contains(".0")){
                mView.maxCouponAmt.setText(s.toString().substring(0, s.toString().length -1))
                mView.maxCouponAmt.setSelection(mView.maxCouponAmt.text!!.length)
            }
        }
        mView.scrollView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                mView.title.clearFocus()
                mView.description.clearFocus()
                /*mView.fromTime.clearFocus()
                mView.toTime.clearFocus()*/
                mView.perDesc.clearFocus()
                mView.minTrans.clearFocus()
                mView.maxCouponAmt.clearFocus()
                mView.code.clearFocus()

                return false
            }

        })

        mView.llFrom.setOnClickListener {
            mView.llFrom.startAnimation(AlphaAnimation(1f, 0.5f))
            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val from=String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year
                        val fromDate = SimpleDateFormat("dd/MM/yyyy").parse(from)
                        if(!TextUtils.isEmpty(mView.toTime.text.toString())) {
                            val toDate = SimpleDateFormat("dd/MM/yyyy").parse(mView.toTime.text.toString())
                            if(toDate.after(fromDate)){
                                mView.fromTime.text = from
                            } else{
                                LogUtils.shortToast(requireContext(), getString(R.string.please_select_valid_date))
                            }
                        }
                        else{
                            mView.fromTime.text = from
                        }
                    }, mYear, mMonth, mDay)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()

        }
        mView.llTo.setOnClickListener {
            mView.llTo.startAnimation(AlphaAnimation(1f, 0.5f))
            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val to=String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year
                        val toDate = SimpleDateFormat("dd/MM/yyyy").parse(to)
                        if(!TextUtils.isEmpty(mView.fromTime.text.toString())) {
                            val fromDate = SimpleDateFormat("dd/MM/yyyy").parse(mView.fromTime.text.toString())
                            if(toDate.after(fromDate)){
                                mView.toTime.text = to
                            } else{
                                LogUtils.shortToast(requireContext(), getString(R.string.please_select_valid_date))
                            }
                        }
                        else{
//                            mView.toTime.text = to
                            LogUtils.shortToast(requireContext(), getString(R.string.please_first_select_from_date))
                        }

                    }, mYear, mMonth, mDay)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()

        }

    }

    private fun validateAndSave() {
        title=mView.title.text.toString()
        description=mView.description.text.toString()
        toTime=mView.toTime.text.toString()
        fromTime=mView.fromTime.text.toString()
        perDesc =mView.perDesc.text.toString()
        minTrans=mView.minTrans.text.toString()
        maxCouponAmt=mView.maxCouponAmt.text.toString()
        code=mView.code.text.toString()

        if(!TextUtils.isEmpty(fromTime)){
            from_date=SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("dd/MM/yyyy").parse(fromTime))
        }

        if(!TextUtils.isEmpty(toTime)){
            to_date=SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("dd/MM/yyyy").parse(toTime))
        }


        if(TextUtils.isEmpty(title)){
            mView.scrollView.scrollTo(0, 100)
            mView.title.requestFocus()
            mView.title.error="Please enter title for your coupon"
        }
        else if(!SharedPreferenceUtility.getInstance().isCharacterAllowed(title)){
            mView.scrollView.scrollTo(0, 100)
            mView.title.requestFocus()
            mView.title.error="Emoji and special characters are not allowed in title"
        }
        else if(TextUtils.isEmpty(description)){
            mView.scrollView.scrollTo(0, 100)
            mView.description.requestFocus()
            mView.description.error="Please enter description for your coupon"
        }
        else if(TextUtils.isEmpty(fromTime)){
           /* mView.scrollView.scrollTo(0, 150)
            mView.fromTime.requestFocus()
            mView.fromTime.error="Please enter from date for your coupon"*/
            LogUtils.shortToast(requireContext(), "Please enter from date for your coupon")
        }
        else if(TextUtils.isEmpty(toTime)){
           /* mView.scrollView.scrollTo(0, 150)
            mView.toTime.requestFocus()
            mView.toTime.error="Please enter to date for your coupon"*/
            LogUtils.shortToast(requireContext(), "Please enter to date for your coupon")
        }
        else if(TextUtils.isEmpty(perDesc)){
            mView.scrollView.scrollTo(0, 100)
            mView.perDesc.requestFocus()
            mView.perDesc.error="Please enter discount(%) for your coupon"
        }
        else if(TextUtils.isEmpty(minTrans)){
            mView.scrollView.scrollTo(0, 100)
            mView.minTrans.requestFocus()
            mView.minTrans.error="Please enter min txn value for your coupon"
        }
        else if(TextUtils.isEmpty(maxCouponAmt)){
            mView.scrollView.scrollTo(0, 100)
            mView.maxCouponAmt.requestFocus()
            mView.maxCouponAmt.error="Please enter max coupon amt for your coupon"
        }
        else if(TextUtils.isEmpty(code)){
            mView.scrollView.scrollTo(0, 100)
            mView.code.requestFocus()
            mView.code.error="Please enter code for your coupon"
        }
        else if(code.length<4){
            mView.scrollView.scrollTo(0, 100)
            mView.code.requestFocus()
            mView.code.error="Code length should be at least 4 characters long"
        }
        else{
            couponAdd()
        }
    }

    private fun couponAdd() {

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("user_id", "title", "description"
                , "percentage", "min_price", "max_discount_price", "from_date", "to_date", "type", "coupon_id", "code", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), title
                        , description, perDesc, minTrans, maxCouponAmt, from_date, to_date, type, coupon_id, code
                        , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        if (imgPath != "") {
            val file = File(imgPath)
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            builder!!.addFormDataPart("picture", file.name, requestBody)
        }


        val call = apiInterface.couponAdd(builder!!.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if (jsonObject.getInt("response") == 1) {
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            findNavController().popBackStack()
                        }
                        else {
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
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
            openCameraDialog()

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
                   openCameraDialog()

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
                    imgPath = ""
                    Log.e("uri", uri.toString())
                    imgPath = uri!!.path!!
                    Glide.with(this).load("file:///$imgPath").placeholder(R.drawable.user).into(mView.img)

                } else {
                    LogUtils.shortToast(requireContext(), "something went wrong! please try again")
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                    imgPath = ""
                    val uri = data.data
                    imgPath = if (uri.toString().startsWith("content")) {
                        FetchPath.getPath(requireContext(), uri!!)!!
                    } else {
                        uri!!.path!!
                    }
                    Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load("file:///$imgPath").into(mView.img)

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
         * @return A new instance of fragment AddCouponFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                AddCouponFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}