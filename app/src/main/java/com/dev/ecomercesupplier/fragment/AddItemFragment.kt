package com.dev.ecomercesupplier.fragment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.BuildConfig
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.AttributesAdapter
import com.dev.ecomercesupplier.adapter.CustomSpinnerAdapter
import com.dev.ecomercesupplier.adapter.ImageVideoAdapter
import com.dev.ecomercesupplier.adapter.NewImageVideoAdapter
import com.dev.ecomercesupplier.custom.FetchPath
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Attributes
import com.dev.ecomercesupplier.model.CategoryName
import com.dev.ecomercesupplier.model.ModelForSpinner
import com.dev.ecomercesupplier.model.PhotoData
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.LogUtils.Companion.att_count_1
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.android.synthetic.main.fragment_add_item.view.header
import kotlinx.android.synthetic.main.fragment_add_item.view.imgAttach
import kotlinx.android.synthetic.main.fragment_add_item.view.rvList
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddItemFragment : Fragment() {
    // TODO: Rename and change types of parameters
     var mView: View?=null
    var catSpinner=ArrayList<ModelForSpinner>()
    var catNameList=ArrayList<CategoryName>()
    var attrList=ArrayList<Attributes>()
    var PICK_IMAGE_MULTIPLE = 1
    var mArrayUri: ArrayList<Uri>? = null
    lateinit var attributesAdapter: AttributesAdapter
    lateinit var adp_categories: CustomSpinnerAdapter
    lateinit var imageVideoAdapter: ImageVideoAdapter
    var pathList=ArrayList<String>()
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val PICK_DOC = 11
    private var mYear = 0
    private  var mMonth:Int = 0
    private  var mDay:Int = 0
    private var selectCatID = "0"
    var allow_coupans :Int = 0
    var add_offer  :Int = 0
    private var imagePath = ""
    private val galleryPhotos = ArrayList<PhotoData>()
    var name:String=""
    var description :String=""
    var discount :String=""
    var from_date :String=""
    var to_date :String=""
    var attributeObj=JSONObject()
    var responseBody:String=""
    var pos:Int=-1
    var product_id:Int=0
    var type:Int=1
    var remainingImages=JSONArray()
    var galleryfiles=JSONArray()
    /*var galleryPhoto = ArrayList<String>()*/
    var new_upload_photos_count = 0
    private var uri: Uri? = null
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private val IMAGE_DIRECTORY_NAME = "Seen"
    private var galleryItemListSize : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            responseBody = it.getString("responseBody").toString()
            pos = it.getInt("pos", -1)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment_add_item, container, false)
            setUpViews()
            if(pos==-1){
                mView!!.btnUploadItem.text = requireContext().getString(R.string.place_item)
                getCategories()
            }
            else{
//                getCategories()
                setEditData()
            }
//            getCategories()
        }
        return mView
    }
    private fun setUpViews() {
        attributeArray= JSONArray()
        SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.AdapterClickCount)
        requireActivity().other_frag_backImg.visibility = View.VISIBLE


        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView!!.edtItemPrice.doOnTextChanged { s, start, before, count ->
            if (s.toString().length > 3 && s.toString().contains(".")) {
                if (s.toString().length - s.toString().indexOf(".") > 3) {
                    mView!!.edtItemPrice.setText(s.toString().substring(0, s.toString().length -1))
                    mView!!.edtItemPrice.setSelection( mView!!.edtItemPrice.text!!.length)
                }

            }
            else  if(s.toString().length>8){
                mView!!.edtItemPrice.setText(s.toString().substring(0, s.toString().length -1))
                mView!!.edtItemPrice.setSelection( mView!!.edtItemPrice.text!!.length)
            }
            else if(s.toString().length==1 && s.toString().contains("0")){
                mView!!.edtItemPrice.setText(s.toString().substring(0, s.toString().length -1))
                mView!!.edtItemPrice.setSelection( mView!!.edtItemPrice.text!!.length)
            }
            else  if(s.toString().length==2 && s.toString().contains(".0")){
                mView!!.edtItemPrice.setText(s.toString().substring(0, s.toString().length -1))
                mView!!.edtItemPrice.setSelection( mView!!.edtItemPrice.text!!.length)
            }
        }

        mView!!.edtDiscPer.doOnTextChanged { s, start, before, count ->
            if (s.toString().length > 3 && s.toString().contains(".")) {
                if (s.toString().length - s.toString().indexOf(".") > 3) {
                    mView!!.edtDiscPer.setText(s.toString().substring(0, s.toString().length -1))
                    mView!!.edtDiscPer.setSelection(mView!!.edtDiscPer.text!!.length)
                }

            }
            else  if (s.toString().length > 2 && !s.toString().contains(".")) {
                mView!!.edtDiscPer.setText(s.toString().substring(0, s.toString().length -1))
                mView!!.edtDiscPer.setSelection(mView!!.edtDiscPer.text!!.length)

            }
            /*else  if(s.toString().length>5){
                mView.perDisc.setText(s.toString().substring(0, s.toString().length -1))
                mView.perDisc.setSelection(mView.perDisc.text!!.length)
            }*/
            else if(s.toString().length==1 && s.toString().contains("0")){
                mView!!.edtDiscPer.setText(s.toString().substring(0, s.toString().length -1))
                mView!!.edtDiscPer.setSelection(mView!!.edtDiscPer.text!!.length)
            }
            else  if(s.toString().length==2 && s.toString().contains(".0")){
                mView!!.edtDiscPer.setText(s.toString().substring(0, s.toString().length -1))
                mView!!.edtDiscPer.setSelection(mView!!.edtDiscPer.text!!.length)
            }
        }


        mView!!.scrollView.setOnTouchListener { v, event ->
            mView!!.edtName.clearFocus()
            mView!!.edtItemDesc.clearFocus()
            mView!!.edtDiscPer.clearFocus()
            false
        }

        mView!!.btnUploadItem.setOnClickListener {
            mView!!.btnUploadItem.startAnimation(AlphaAnimation(1f, 0.5f))
            validateAndUpload()
        }
        setUploadPhotos()
       /* mView!!.rvImgVdo.layoutManager=LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        imageVideoAdapter= ImageVideoAdapter(requireContext(), galleryPhotos, object :ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
                *//*pathList.removeAt(pos)*//*
                imageVideoAdapter.notifyDataSetChanged()
                mView!!.imgAttach.alpha=1f
                mView!!.imgAttach.isEnabled=true
            }

        })
        mView!!.rvImgVdo.adapter=imageVideoAdapter
        imageVideoAdapter.notifyDataSetChanged()*/

        mView!!.rvList.layoutManager=LinearLayoutManager(requireContext())
        attributesAdapter= AttributesAdapter(requireContext(), attrList, object : ClickInterface.ClickJSonObjInterface{
            override fun clickJSonObj(obj: JSONObject) {
                attributeObj=obj
                Log.e("attributeObj", attributeObj.toString())
            }


        })
        mView!!.rvList.adapter=attributesAdapter

        AttributesAdapter.attrData= JSONArray()

        mView!!.btnSave.setOnClickListener {
            mView!!.btnSave.startAnimation(AlphaAnimation(1f, 0.5f))
            if (attributeObj.length() == 0){
                if(selectCatID=="0") {
                    LogUtils.shortToast(requireContext(), getString(R.string.please_select_a_category))
                }
                else{
                    LogUtils.shortToast(requireContext(), getString(R.string.please_select)+ " " + attrList[0].name)
                }
            }
            else  {
                if (attributeObj.length() == 0) {
                    LogUtils.shortToast(requireContext(), getString(R.string.please_select)+ " " + attrList[0].name)
                } else {
                    val dataArray = JSONArray(attributeObj.getString("data"))
                    if (attrList.size > dataArray.length()) {
                        if (dataArray.length() == 1) {
                            LogUtils.shortToast(requireContext(), getString(R.string.please_select)+ " "  + attrList[1].name)
                        } else if (dataArray.length() == 2) {
                            LogUtils.shortToast(requireContext(), getString(R.string.please_select)+ " "  + attrList[2].name)
                        }
                    } else {

                        /* val dataArray = JSONArray(attributeObj.getString("data"))
                     for (i in 0 until dataArray.length()) {

                         val obj = dataArray.getJSONObject(i)
                         val name = obj.getString("name")
                         if (TextUtils.isEmpty(name)) {
                             LogUtils.shortToast(requireContext(), "Please select " + name)
                             break
                         }
                     }*/

                        /*if (attributeObj.length() == 0) {
                    LogUtils.shortToast(requireContext(), "Please select " + attrList[0].name)
                } else if (attributeObj.length() != 0) {
                    for (k in 0 until attrList.size) {
                        if (TextUtils.isEmpty(attributeObj.getString(attrList[k].id.toString()))) {
                            LogUtils.shortToast(requireContext(), "Please select " + attrList[k].name)
                            break
                        }

                    }*/
                        when {
                            TextUtils.isEmpty(mView!!.edtItemPrice.text.toString()) -> {
                                LogUtils.shortToast(requireContext(), getString(R.string.please_select_price))
                            }
                            TextUtils.isEmpty(mView!!.edtItemQty.text.toString()) -> {
                                LogUtils.shortToast(requireContext(), getString(R.string.please_select_quantity))
                            }
                            else -> {
                                AttributesAdapter.attrData=JSONArray()
                                SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.AdapterClickCount)
                                LogUtils.shortToast(requireContext(), getString(R.string.attributes_saved_successfully))
                                SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.btnSave)
                                attributeObj.put("price", mView!!.edtItemPrice.text.toString())
                                attributeObj.put("quantity", mView!!.edtItemQty.text.toString())
                                attributeObj.put("id", attributeArray.length())
//                                attributeObj.put("sold_out", "0")
                                attributeArray.put(attributeObj)
                                LogUtils.e("attributeArray", attributeArray.toString())
                                mView!!.edtItemPrice.setText("")
                                mView!!.edtItemQty.setText("")
                                attributeObj= JSONObject()
                                attributesAdapter.notifyDataSetChanged()
                            }
                        }

                    }
                }
            }
            /*else if (attributeObj.length() != 0) {
            for(k in 0 until attrList.size) {
                    if (TextUtils.isEmpty(attributeObj.getString("data"))) {
                        LogUtils.shortToast(requireContext(), "Please select " + attrList[k].name)
                        break
                    }

            }
                when {
                    TextUtils.isEmpty(mView!!.edtItemPrice.text.toString()) -> {
                        LogUtils.shortToast(requireContext(), "Please select price")
                    }
                    TextUtils.isEmpty(mView!!.edtItemQty.text.toString()) -> {
                        LogUtils.shortToast(requireContext(), "Please select quantity")
                    }
                    else -> {
                        LogUtils.shortToast(requireContext(), "Attributes saved successfully")
                        SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.btnSave)
                        attributeObj.put("price", mView!!.edtItemPrice.text.toString())
                        attributeObj.put("quantity", mView!!.edtItemQty.text.toString())
                        attributeObj.put("id", attributeArray.length())
                        attributeArray.put(attributeObj)
                        LogUtils.e("attributeArray", attributeArray.toString())
                        mView!!.edtItemPrice.setText("")
                        mView!!.edtItemQty.setText("")
                        attributeObj= JSONObject()
                        attributesAdapter.notifyDataSetChanged()

                    }
                }
            }*/
        }

        mView!!.btnView.setOnClickListener {
            mView!!.btnView.startAnimation(AlphaAnimation(1f, 0.5f))
            if(attributeArray.length()==0){
                LogUtils.shortToast(requireContext(), "Please first save attributes")
            }
            else{
                att_count_1 = attrList.size
                val args=Bundle()
                args.putString("header", mView!!.edtName.text.toString())
                args.putInt("att_count", attrList.size)
               /* args.putString("attributeArray", attributeArray.toString())*/
                findNavController().navigate(R.id.action_addItemFragment_to_viewAttrFragment, args)
            }

        }

        mView!!.imgAttach.setOnClickListener {
            mView!!.imgAttach.startAnimation(AlphaAnimation(1f, 0.5f))
            requestToUploadProfilePhoto()
        }

        mView!!.switchAddOffer.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                add_offer=1
                mView!!.offerView.visibility=View.VISIBLE
            } else{
                add_offer=0
                mView!!.edtDiscPer.setText("")
                mView!!.fromTime.text = ""
                mView!!.toTime.text = ""
                mView!!.offerView.visibility=View.GONE
            }
        }
        mView!!.switchAllowCoupons.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                allow_coupans=1
            } else{
                allow_coupans=0
            }
        }

        mView!!.llFrom.setOnClickListener {
            mView!!.llFrom.startAnimation(AlphaAnimation(1f, 0.5f))
            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val from=String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year
                        val fromDate = SimpleDateFormat("dd/MM/yyyy").parse(from)
                        if(!TextUtils.isEmpty(mView!!.toTime.text.toString())) {
                            val toDate = SimpleDateFormat("dd/MM/yyyy").parse(mView!!.toTime.text.toString())
                            if(toDate.after(fromDate)){
                                mView!!.fromTime.text = from
                            } else{
                                LogUtils.shortToast(requireContext(), getString(R.string.please_select_valid_date))
                            }
                        }
                        else{
                            mView!!.fromTime.text = from
                        }
                        }, mYear, mMonth, mDay)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                datePickerDialog.show()

        }
        mView!!.llTo.setOnClickListener {
            mView!!.llTo.startAnimation(AlphaAnimation(1f, 0.5f))
            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val to=String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year
                        val toDate = SimpleDateFormat("dd/MM/yyyy").parse(to)
                        if(!TextUtils.isEmpty(mView!!.fromTime.text.toString())) {
                            val fromDate = SimpleDateFormat("dd/MM/yyyy").parse(mView!!.fromTime.text.toString())
                            if(toDate.after(fromDate)){
                                mView!!.toTime.text = to
                            } else{
                                LogUtils.shortToast(requireContext(), getString(R.string.please_select_valid_date))
                            }
                        }
                        else{
//                            mView!!.toTime.text = to
                            LogUtils.shortToast(requireContext(), getString(R.string.please_first_select_from_date))
                        }

                    }, mYear, mMonth, mDay)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()

        }

        adp_categories = CustomSpinnerAdapter(requireContext(), catSpinner)
        mView!!.spinnerCategory.adapter = adp_categories

        mView!!.spinnerCategory.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                attributeObj= JSONObject()
                if(p2 != 0) {
                    mView!!.llViewSave.visibility=View.VISIBLE
                    mView!!.llPriceQty.visibility=View.VISIBLE
                    selectCatID = catSpinner[p2].id.toString()
                    for (k in 0 until catNameList.size) {
                        if (catNameList[k].id == catSpinner[p2].id) {
                            val attributes = catNameList[k].attributes
                            attrList.clear()
                            for (m in 0 until attributes.length()) {
                                val obj = attributes.getJSONObject(m)
                                val a = Attributes()
                                a.id=obj.getInt("id")
                                a.name = obj.getString("name")
                                a.type = obj.getString("type")
                                a.value = obj.getJSONArray("value")
                                attrList.add(a)
                            }
                            attributesAdapter.notifyDataSetChanged()
                        }
                    }
                }
                else{
                    mView!!.llViewSave.visibility=View.GONE
                    mView!!.llPriceQty.visibility=View.GONE
                    selectCatID=catSpinner[p2].id.toString()
                    attrList.clear()
                    attributesAdapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                attributeObj=JSONObject()
                mView!!.llViewSave.visibility=View.GONE
                mView!!.llPriceQty.visibility=View.GONE
                selectCatID="0"
                attrList.clear()
                attributesAdapter.notifyDataSetChanged()
            }


        }
    }

    private fun validateAndUpload() {
        name=mView!!.edtName.text.toString()
        description=mView!!.edtItemDesc.text.toString()
        discount=mView!!.edtDiscPer.text.toString()
        val   from_time=mView!!.fromTime.text.toString()
        val to_time=mView!!.toTime.text.toString()
        if(!TextUtils.isEmpty(from_time)){
            from_date=SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("dd/MM/yyyy").parse(from_time))
        }
        if(!TextUtils.isEmpty(to_time)){
            to_date=SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("dd/MM/yyyy").parse(to_time))
        }


        if(selectCatID=="0"){
            LogUtils.shortToast(requireContext(), getString(R.string.please_select_a_category))
        }
        else if(TextUtils.isEmpty(name)){
//            mView!!.scrollView.scrollTo(0, 100)
            mView!!.edtName.requestFocus()
            mView!!.edtName.error=getString(R.string.please_enter_item_name)
        }
        else if(TextUtils.isEmpty(description)){
//            mView!!.scrollView.scrollTo(0, 120)
            mView!!.edtItemDesc.requestFocus()
            mView!!.edtItemDesc.error=getString(R.string.please_enter_item_name)
        }

        else if(attributeArray.length()==0) {
            if (attributeObj.length() == 0) {
                LogUtils.shortToast(requireContext(), getString(R.string.please_select) + " " + attrList[0].name)
            } else {
                val dataArray = JSONArray(attributeObj.getString("data"))
                if (attrList.size > dataArray.length()) {

                    if (dataArray.length() == 1) {
                        LogUtils.shortToast(requireContext(), getString(R.string.please_select) + " " + attrList[1].name)
                    } else if (dataArray.length() == 2) {
                        LogUtils.shortToast(requireContext(), getString(R.string.please_select) + " " + attrList[2].name)
                    }
                } else {

                    /* val dataArray = JSONArray(attributeObj.getString("data"))
                 for (i in 0 until dataArray.length()) {

                     val obj = dataArray.getJSONObject(i)
                     val name = obj.getString("name")
                     if (TextUtils.isEmpty(name)) {
                         LogUtils.shortToast(requireContext(), "Please select " + name)
                         break
                     }
                 }*/

                    /*if (attributeObj.length() == 0) {
                LogUtils.shortToast(requireContext(), "Please select " + attrList[0].name)
            } else if (attributeObj.length() != 0) {
                for (k in 0 until attrList.size) {
                    if (TextUtils.isEmpty(attributeObj.getString(attrList[k].id.toString()))) {
                        LogUtils.shortToast(requireContext(), "Please select " + attrList[k].name)
                        break
                    }

                }*/
                    when {
                        TextUtils.isEmpty(mView!!.edtItemPrice.text.toString()) -> {
                            LogUtils.shortToast(requireContext(), getString(R.string.please_select_price))
                        }
                        TextUtils.isEmpty(mView!!.edtItemQty.text.toString()) -> {
                            LogUtils.shortToast(requireContext(), getString(R.string.please_select_quantity))
                        }
                        else -> {
                            LogUtils.shortToast(requireContext(), getString(R.string.please_save_the_attributes))
                        }
                    }

                }
            }
        }

        else if(add_offer==1){
            when {
                TextUtils.isEmpty(discount) -> {
//                    mView!!.scrollView.scrollTo(0, 150)
                    mView!!.edtDiscPer.requestFocus()
                    mView!!.edtDiscPer.error=getString(R.string.please_enter_discount_percentage)
                }
                TextUtils.isEmpty(from_date) -> {
                    /* mView!!.scrollView.scrollTo(0, 650)
                   mView!!.fromTime.requestFocus()*/
                    LogUtils.shortToast(requireContext(), getString(R.string.please_select_from_date))
                }
                TextUtils.isEmpty(to_date) -> {
                    /*mView!!.scrollView.scrollTo(0, 650)
                   mView!!.toTime.requestFocus()*/
                    LogUtils.shortToast(requireContext(), getString(R.string.please_select_to_date))

                }
                galleryPhotos.size==0 ->{
                    LogUtils.shortToast(requireContext(), getString(R.string.please_select_at_least_an_image_or_video))
                }
                else -> {
                    addItem()
                }
            }
        }
        else if(galleryPhotos.size==0){
            LogUtils.shortToast(requireContext(), getString(R.string.please_select_at_least_an_image_or_video))
        }
        else{
           addItem()
        }
       /* for (k in 0 until catNameList.size) {
            if (catNameList[k].id == selectCatID.toInt()) {
                val attributes = catNameList[k].attributes

                for (m in 0 until attributes.length()) {
                    val obj = attributes.getJSONObject(m)
                    val a = Attributes()
                    a.name = obj.getString("name")
                    a.value = obj.getJSONArray("value")
                    attrList.add(a)
                }

            }
        }*/
    }

    private fun addGalleryItem() {
        Log.e("size", ""+galleryPhotos.size)
      /*  if(TextUtils.isEmpty(discount)){
            discount="0"
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE*/

        /*for(i in 0 until galleryPhotos.size){
            val file = File(galleryPhotos[i])
            if(file.exists()){
                galleryfiles.put(galleryPhotos[i].substring(galleryPhotos[i].lastIndexOf('/') + 1))
            }
        }*/
    }

    private fun getCategories() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getCategories(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val categories = jsonObject.getJSONArray("categories")
                        catSpinner.clear()
                        val m = ModelForSpinner()
                        m.id=0
                        m.name=requireContext().getString(R.string.select_category)
                        catSpinner.add(m)

                        catNameList.clear()
                        for (i in 0 until categories.length()) {
                            val jsonObj = categories.getJSONObject(i)
                            if(jsonObj.getBoolean("checkCategoryStatus")) {
                                val m1 = ModelForSpinner()
                                m1.id = jsonObj.getInt("id")
                                m1.name = jsonObj.getString("name")
                                catSpinner.add(m1)

                                val c = CategoryName()
                                c.id = jsonObj.getInt("id")
                                c.name = jsonObj.getString("name")
                                c.attributes= jsonObj.getJSONArray("attributes")
                                catNameList.add(c)
                            }
                        }
                        adp_categories.notifyDataSetChanged()


                        if(pos!=-1){
                            setEditData()
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
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    private fun setEditData() {
        type=2
        mView!!.header.text = getString(R.string.update_item)
        mView!!.btnUploadItem.text = requireContext().getString(R.string.save_changes)
        Log.e("response_edit", responseBody)
        if(!TextUtils.isEmpty(responseBody)) {
            val jsonObject = JSONObject(responseBody)
            if (jsonObject.getInt("response") == 1) {
                val products = jsonObject.getJSONArray("products")
                galleryPhotos.clear()
                for (i in 0 until products.length()) {
                    if (i == pos) {
                        val obj = products.getJSONObject(i)
                        product_id=obj.getInt("id")
                        mView!!.edtName.setText(obj.getString("name"))
                        mView!!.edtItemDesc.setText(obj.getString("description"))
                        val m1 = ModelForSpinner()
                        m1.id = obj.getInt("category_id")
                        m1.name = obj.getString("category_name")
                        for(i in 0 until catSpinner.size){
                            if(catSpinner[i].name.equals(obj.getString("category_name"))){
                                mView!!.spinnerCategory.setSelection(i)
                                break
                            }
                        }

                        mView!!.spinnerCategory.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                attributeObj= JSONObject()
                                if(p2 != 0) {
                                    mView!!.llViewSave.visibility=View.VISIBLE
                                    mView!!.llPriceQty.visibility=View.VISIBLE
                                    selectCatID = catSpinner[p2].id.toString()
                                    for (k in 0 until catNameList.size) {
                                        if (catNameList[k].id == catSpinner[p2].id) {
                                            val attributes = catNameList[k].attributes
                                            attrList.clear()
                                            for (m in 0 until attributes.length()) {
                                                val obj = attributes.getJSONObject(m)
                                                val a = Attributes()
                                                a.id=obj.getInt("id")
                                                a.name = obj.getString("name")
                                                a.type = obj.getString("type")
                                                a.value = obj.getJSONArray("value")
                                                attrList.add(a)
                                            }
                                            attributesAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                                else{
                                    mView!!.llViewSave.visibility=View.GONE
                                    mView!!.llPriceQty.visibility=View.GONE
                                    selectCatID=catSpinner[p2].id.toString()
                                    attrList.clear()
                                    attributesAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                                attributeObj=JSONObject()
                                mView!!.llViewSave.visibility=View.GONE
                                mView!!.llPriceQty.visibility=View.GONE
                                selectCatID="0"
                                attrList.clear()
                                attributesAdapter.notifyDataSetChanged()
                            }


                        }

                        catSpinner.add(m1)
                        adp_categories.notifyDataSetChanged()

                        val attributes=obj.getString("attributes")
                        if(!TextUtils.isEmpty(attributes)){
                            attributeArray= JSONArray(attributes)

                        }
                        if(obj.getInt("allow_coupans")==1){
                            mView!!.switchAllowCoupons.isChecked=true
                        }
                        if(obj.getInt("add_offer")==1){
                            mView!!.switchAddOffer.isChecked=true
                            mView!!.edtDiscPer.setText(obj.getString("discount"))
                            mView!!.fromTime.text = obj.getString("from_date")
                            mView!!.toTime.text = obj.getString("to_date")
                        }
                        val all_files=obj.getJSONArray("all_files")
                        for(m in 0 until all_files.length()){
                            var photo_Data = PhotoData()
                            photo_Data.status = "old"
                            photo_Data.path = all_files.getString(m)
                            galleryPhotos.add(photo_Data)
//                            galleryPhotos.add(all_files[m].toString())
//                            galleryPhotos.add(all_files[m].toString())
//                            remainingImages.put(galleryPhotos[m].substring(galleryPhotos[i].lastIndexOf('/') + 1))
                        }


                        mView!!.edtItemPrice.setText(obj.getString("price"))
                        if (obj.getInt("quantity")==1){
                            mView!!.edtItemQty.setText(obj.getInt("quantity").toString())
                        }else{
                            mView!!.edtItemQty.setText(obj.getInt("quantity").toString())
                        }

                        setUploadPhotos()
                        galleryItemListSize =galleryPhotos.size
                        imageVideoAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    private fun addItem() {
        Log.e("size", ""+galleryPhotos.size)
        if(TextUtils.isEmpty(discount)){
            discount="0"
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE


        remainingImages = JSONArray()

        for(i in 0 until galleryPhotos.size){
            remainingImages.put(galleryPhotos[i].path.substring(galleryPhotos[i].path.lastIndexOf('/') + 1))
            /*val file = File(galleryPhoto[i])
            if(file.exists()){
                remainingImages.put(galleryPhoto[i].substring(galleryPhoto[i].lastIndexOf('/') + 1))
            }*/
        }

        /*for(i in 0 until galleryPhotos.size){
            val file = File(galleryPhotos[i])
            if(file.exists()){
                galleryfiles.put(galleryPhotos[i].substring(galleryPhotos[i].lastIndexOf('/') + 1))
            }
        }*/


        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("user_id", "name", "category_id", "description"
                , "allow_coupans", "add_offer", "discount", "from_date", "to_date", "attributes", "product_id", "type", "remaining_images","lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), name, selectCatID
                        , description, allow_coupans.toString(), add_offer.toString(), discount, from_date, to_date
                        , attributeArray.toString(), product_id.toString(), type.toString(),
                        remainingImages.toString(),
                        SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

     /*for(i in 0 until galleryPhotos.size){
         val file = File(galleryPhoto[i])
         if(file.exists()){
             val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
             builder!!.addFormDataPart("remaining_images", file.name, requestBody)
         }
     }*/
        var count = 0
        for (i in 0 until galleryPhotos.size){
                if (galleryPhotos[i].status == "new"){
                    count+=1
                    val file = File(galleryPhotos[i].path)
                    val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                    builder!!.addFormDataPart("files[]", file.name, requestBody)
                }

        }
      /*  if(count==0){
            builder!!.addFormDataPart("files[]", "")
        }*/



        val call = apiInterface.addItem(builder!!.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
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
                mView!!.progressBar.visibility = View.GONE
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
        val builder = AlertDialog.Builder(requireActivity())
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


    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }

    fun getOutputMediaFileUri(type: Int): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID.toString() + ".provider", getOutputMediaFile(type)!!)
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


    private fun chooseImage() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
//        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        intent.action = Intent.ACTION_GET_CONTENT
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
//        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)
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
                    LogUtils.shortToast(requireContext(), requireContext().getString(R.string.please_grant_both_camera_and_storage_permissions))

                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                LogUtils.shortToast(requireContext(), requireContext().getString(R.string.please_grant_both_camera_and_storage_permissions))
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        } else if (requestCode == PICK_DOC && resultCode == Activity.RESULT_OK && data != null) {
            val files: java.util.ArrayList<MediaFile> =
                    data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)!!
            if (files.size != 0) {
                for (i in 0 until files.size) {
                    val filePath = FetchPath.getPath(requireContext(), files[i].uri)
                    pathList.add(filePath.toString())
                    val photoData = PhotoData()
                    photoData.status = "new"
                    photoData.path = filePath.toString()
                    galleryPhotos.add(photoData)
                }
                if (pathList.size == 5) {
                    mView!!.imgAttach.alpha = 0.5f
                    mView!!.imgAttach.isEnabled = false
                } else {
                    mView!!.imgAttach.alpha = 1f
                    mView!!.imgAttach.isEnabled = true
                }
                mView!!.rvImgVdo.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                imageVideoAdapter = ImageVideoAdapter(requireContext(), galleryPhotos, object : ClickInterface.ClickPosInterface {
                    override fun clickPostion(pos: Int) {
                        pathList.removeAt(pos)
                        imageVideoAdapter.notifyDataSetChanged()
                        galleryItemListSize=galleryItemListSize-1
                        mView!!.imgAttach.alpha = 1f
                        mView!!.imgAttach.isEnabled = true
                    }

                })
                mView!!.rvImgVdo.adapter = imageVideoAdapter
                imageVideoAdapter.notifyDataSetChanged()
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) { //
                // previewCapturedImage();
                    if (galleryPhotos.size<5){
                        if (uri != null) {
                            imagePath = ""
                            Log.e("uri", uri.toString())
                            imagePath = uri!!.path!!
                            val photoData = PhotoData()
                            photoData.status = "new"
                            photoData.path = uri!!.path!!
                            galleryPhotos.add(photoData)
                            setUploadPhotos()
                        }
                    }else if(galleryPhotos.size==5){
                        LogUtils.shortToast(requireActivity(), requireContext().getString(R.string.something_went_wrong))
                    } else {
                        LogUtils.shortToast(requireActivity(), requireContext().getString(R.string.something_went_wrong))
                    }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
//            pathList.clear()

            imagePath = ""
            val clipdata = data.clipData
            if (clipdata != null) {
                val count: Int = data.clipData!!.itemCount
                if (count+galleryPhotos.size<=5){
                    for (i in 0 until clipdata.itemCount) {
                        val uri = clipdata.getItemAt(i).uri
                        /* if (uri.toString().startsWith("content")) {
                             imagePath = getRealPath(uri)!!
                         } else {
                             imagePath = uri.getPath()!!
                         }*/
                        imagePath = if (uri.toString().startsWith("content")) {
                            FetchPath.getPath(requireActivity(), uri!!)!!
                        } else {
                            uri!!.path!!
                        }
                        val photoData = PhotoData()
                        photoData.status = "new"
                        photoData.path = imagePath
                        galleryPhotos.add(photoData)
                    }
                    setUploadPhotos()
                }else{
                    LogUtils.shortToast(requireContext(), requireContext().getString(R.string.only_five_items_can_be_selected))
                }

            } else { // handle single photo
                if (galleryPhotos.size<5){
                    val imageURI = data.data!!
     /*               imagePath = FetchPath.getPath(requireActivity(), uri)!!
                    *//*imagePath = if (uri.toString().startsWith("content")) {
                        FetchPath.getPath(requireActivity(), uri!!)!!
                    } else {
                        uri!!.path!!
                    }*/
                    try {
                        if (imageURI.toString().startsWith("content")) {
                            imagePath = imageURI?.let { it1 ->
                                FetchPath.getPath(requireContext(),
                                    it1
                                )
                            }!!
                        } else {
                            imagePath = imageURI!!.getPath()!!
                        }

                    }catch (e: Exception){
                        Log.e("exception", e.message.toString())
                    }
//                    imagePath = uri!!.path!!
                    val photoData = PhotoData()
                    photoData.status = "new"
                    photoData.path = imagePath
                    galleryPhotos.add(photoData)
                    setUploadPhotos()
                }else if (galleryPhotos.size==5){
                    LogUtils.shortToast(requireContext(), requireContext().getString(R.string.only_five_items_can_be_selected))
                }else{
                    LogUtils.shortToast(requireContext(), requireContext().getString(R.string.only_five_items_can_be_selected))
                }

            }
        }
    }

    private fun getRealPath(ur: Uri?): String? {
        var realpath = ""
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        // Get the cursor
        val cursor: Cursor = requireContext().contentResolver.query(ur!!,
                filePathColumn, null, null, null
        )!!
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        //Log.e("columnIndex", String.valueOf(MediaStore.Images.Media.DATA));
        realpath = cursor.getString(columnIndex)
        cursor.close()
        return realpath
    }

    private fun setUploadPhotos() {
        mView!!.rvImgVdo.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        imageVideoAdapter= ImageVideoAdapter(requireContext(), galleryPhotos, object :ClickInterface.ClickPosInterface{
            override fun clickPostion(position: Int) {
                /*galleryPhotos.removeAt(pos)
                mView!!.imgAttach.alpha=1f
                mView!!.imgAttach.isEnabled=(galleryPhotos.size<5)
                mView!!.rvImgVdo.adapter = imageVideoAdapter
                imageVideoAdapter.notifyDataSetChanged()*/
                if(galleryPhotos[position].status.equals("old")) {
                    val alert = android.app.AlertDialog.Builder(requireContext())
                    alert.setMessage(requireContext().getString(R.string.delete_message))
                    alert.setCancelable(false)
                    alert.setPositiveButton(getString(R.string.yes)) { dialog, i ->
                        dialog.cancel()
                        galleryPhotos.removeAt(position)
                        mView!!.imgAttach.alpha=1f
                        mView!!.imgAttach.isEnabled=(galleryPhotos.size<5)
                        mView!!.rvImgVdo.adapter = imageVideoAdapter
                        imageVideoAdapter.notifyDataSetChanged()
                    }
                    alert.setNegativeButton(getString(R.string.no)) { dialog, i ->
                        dialog.cancel()
                    }
                    alert.show()
                }
                else {
                    Log.e("check", "" + position)
                    galleryPhotos.removeAt(position)
                    mView!!.imgAttach.alpha=1f
                    mView!!.imgAttach.isEnabled=(galleryPhotos.size<5)
                    mView!!.rvImgVdo.adapter = imageVideoAdapter
                    imageVideoAdapter.notifyDataSetChanged()
                }
                mView!!.imgAttach.alpha=1f
                mView!!.imgAttach.isEnabled = true
            }
        })
        mView!!.rvImgVdo.adapter=imageVideoAdapter
        imageVideoAdapter.notifyDataSetChanged()
    }

    companion object {
        lateinit var attributeArray:JSONArray
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}