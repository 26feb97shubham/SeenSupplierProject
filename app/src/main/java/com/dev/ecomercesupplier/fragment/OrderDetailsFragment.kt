package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.ProductListForOrderAdapter
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.dialog.RejectionDialog
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.*
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_order_details.view.*
import kotlinx.android.synthetic.main.fragment_order_details.view.progressBar
import kotlinx.android.synthetic.main.fragment_order_details.view.rvList
import kotlinx.android.synthetic.main.fragment_order_details.view.tabLayout
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var orderDataList=ArrayList<OrderData>()
    lateinit var productListForOrderAdapter: ProductListForOrderAdapter
    var mView: View?=null
    var type:String=""
    var order_data:String=""
    var delivery_date:String=""
    var shipping_fee:String=""
    var subtotal:String=""
    var taxes:String=""
    var total_price:String=""
    var order_id:String=""
    var price:String=""
    var files:String=""
    var coupon_name:String=""
    var total_discount:String=""
    var user_image:String=""
    var phone_number:String=""
    var user_name:String=""
    var country:String=""
    var address:String=""
    var AWBNumber:String=""
    var quantity=0
    var id_order=0
    var accept_reject=0
    var status=0
    var productFiles=ArrayList<String>()
    lateinit var pagerAdapter: ScreenSlidePagerAdapter
    var attributes=JSONArray()
    var accept_reject_string:String=""
    var myOrderData : MyOrders?=null
    var message:String=""
    var myTrackStatus = 0 //1 for
    var myTaxes = 0 //1 for
    var myDiscount = 0 //1 for
    var myShippingFee = 0 //1 for
    var myTotalPrice = 0 //1 for

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString("type").toString()
            order_data = it.getString("order_data").toString()
            delivery_date = it.getString("delivery_date").toString()
            shipping_fee = it.getString("shipping_fee").toString()
            myShippingFee = Integer.parseInt(shipping_fee)
            subtotal = it.getString("subtotal").toString()
            taxes = it.getString("taxes").toString()
            myTaxes = Integer.parseInt(taxes)
            total_price = it.getString("total_price").toString()
            order_id = it.getString("order_id").toString()
            user_name = it.getString("user_name").toString()
            user_image = it.getString("user_image").toString()
            phone_number = it.getString("phone_number").toString()
            total_discount = it.getString("total_discount").toString()
            myDiscount = Integer.parseInt(total_discount)
            coupon_name = it.getString("coupon_name").toString()
            AWBNumber = it.getString("AWBNumber").toString()
            id_order = it.getInt("id", 0)
            accept_reject = it.getInt("accept_reject", 0)
            status = it.getInt("status", 0)
            myOrderData = it.getSerializable("OrderData") as MyOrders?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_order_details, container, false)
        Utility.setLanguage(requireContext(), SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        setData()
        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility = View.VISIBLE


        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        pagerAdapter = ScreenSlidePagerAdapter(this)
        mView!!.viewPager2.adapter = pagerAdapter
        TabLayoutMediator(mView!!.tabLayout,   mView!!.viewPager2){ tab, position ->

        }.attach()


       productListForOrderAdapter= ProductListForOrderAdapter(requireContext(), orderDataList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
            }

        })
       mView!!.rvList.layoutManager= LinearLayoutManager(context)
       mView!!.rvList.adapter=productListForOrderAdapter

        mView!!.accept.setOnClickListener {
            mView!!.accept.startAnimation(AlphaAnimation(1f, .5f))
            message=""
            accept_reject_string="1"
            acceptReject()
        }
        mView!!.reject.setOnClickListener {
            mView!!.reject.startAnimation(AlphaAnimation(1f, .5f))
            val rejectionDialog= RejectionDialog()
            rejectionDialog.isCancelable=false
            rejectionDialog.setDataCompletionCallback(object : RejectionDialog.RejectionInterface{
                override fun complete(reason: String) {
                    accept_reject_string="2"
                    message=reason
                    acceptReject()
                }

            })
            rejectionDialog.show(requireActivity().supportFragmentManager, "ReceivedOrdersFragment")
        }

        when (accept_reject) {
            1 -> {
               mView!!.llDeliveredDate.visibility = View.VISIBLE
               mView!!.view5.visibility = View.VISIBLE
               mView!!.rejected.visibility = View.GONE
                mView!!.ratingTxt.visibility = View.GONE
                mView!!.ratingUserProfile.visibility = View.GONE
                mView!!.ratingUserName.visibility = View.GONE
                mView!!.ratingVal.visibility = View.GONE
                mView!!.ratingBar.visibility = View.GONE
            }
            2 -> {
                mView!!.llDeliveredDate.visibility = View.GONE
                mView!!.view5.visibility = View.GONE
                mView!!.ratingTxt.visibility = View.GONE
                mView!!.ratingUserProfile.visibility = View.GONE
                mView!!.ratingUserName.visibility = View.GONE
                mView!!.ratingVal.visibility = View.GONE
                mView!!.ratingBar.visibility = View.GONE
                mView!!.rejected.visibility = View.VISIBLE
            }
            else -> {
                mView!!.llDeliveredDate.visibility = View.GONE
                mView!!.rejected.visibility = View.GONE
                mView!!.ratingTxt.visibility = View.GONE
                mView!!.ratingUserProfile.visibility = View.GONE
                mView!!.ratingUserName.visibility = View.GONE
                mView!!.ratingVal.visibility = View.GONE
                mView!!.ratingBar.visibility = View.GONE
            }
        }
    }

    private fun setData() {
        manageDataForparticularSCreen()
        manageCoupon()
        setOrdersData()

        mView!!.orderNum.text=getString(R.string.order_hess)+order_id
        mView!!.userName.text=user_name
        mView!!.couponCode.text=coupon_name
        mView!!.mobNum.text=phone_number
        mView!!.couponCode.text=coupon_name
        mView!!.taxValue.text="AED"+" "+taxes
        mView!!.shippingCharge.text="AED"+" "+shipping_fee
        mView!!.couponValue.text="AED"+" "+total_discount
        var myPrice = 0
        for (i in 0 until orderDataList.size){
            myPrice += Integer.parseInt(orderDataList[i].price)
        }
        myTotalPrice = myPrice+myTaxes+myShippingFee-myDiscount
        mView!!.totalValue.text="AED"+" "+myTotalPrice
        Glide.with(requireContext()).load(SharedPreferenceUtility.getInstance().get("profile_picture", "")).placeholder(R.drawable.user).into(mView!!.userProfile)


      /*  Glide.with(requireContext()).load(order_data).placeholder(R.drawable.user).into(mView!!.userProfile)
        mView!!.orderName.text=product_name
        mView!!.price.text=getString(R.string.aed)+price
//        mView!!.orderDesc.text=

                for(i in 0 until attributes.length()){
                    val obj=attributes.getJSONObject(i)
                    if(i==0){
                        if(obj.getString("type")=="1"){
                            mView!!.attr1Txt.text=obj.getString("name")
                             mView!!.attr1.text=obj.getString("value")
                        }
                        else{
                             mView!!.attr1Txt.text=obj.getString("name")
                             mView!!.attr1.setBackgroundColor(Color.parseColor(obj.getString("value")))
                        }

                    }
                    else if(i==1){
                         mView!!.attr2Txt.visibility=View.VISIBLE
                         mView!!.attr2.visibility=View.VISIBLE
                        if(obj.getString("type")=="1"){
                             mView!!.attr2Txt.text=obj.getString("name")
                             mView!!.attr2.text=obj.getString("value")
                        }
                        else{
                             mView!!.attr2Txt.text=obj.getString("name")
                             mView!!.attr2.setBackgroundColor(Color.parseColor(obj.getString("value")))
                        }

                    }
                    else if(i==2){
                         mView!!.attr3Txt.visibility=View.VISIBLE
                         mView!!.attr3.visibility=View.VISIBLE
                        if(obj.getString("type")=="1"){
                             mView!!.attr3Txt.text=obj.getString("name")
                             mView!!.attr3.text=obj.getString("value")
                        }
                        else{
                             mView!!.attr3Txt.text=obj.getString("name")
                             mView!!.attr3.setBackgroundColor(Color.parseColor(obj.getString("value")))
                        }

                    }
                }*/
    }

    private fun setOrdersData() {
        if(!TextUtils.isEmpty(order_data)){
            val orderDataArray=JSONArray(order_data)
            orderDataList.clear()
            productFiles.clear()
            for(i in 0 until orderDataArray.length()){
                val obj1 = orderDataArray.getJSONObject(i)
                val o = OrderData()
                o.quantity = obj1.getInt("quantity")
                o.id = obj1.getInt("product_id")
                o.country = obj1.getString("country")
                o.address = obj1.getString("address")
                o.price = obj1.getString("price")
                o.files = obj1.getString("files")
                o.product_name = obj1.getString("product_name")
                o.product_description = obj1.getString("product_description")
                o.attributes = obj1.getJSONArray("attributes")

                orderDataList.add(o)

                productFiles.add(obj1.getString("files"))
            }
            productListForOrderAdapter.notifyDataSetChanged()
            pagerAdapter.notifyDataSetChanged()
        }
    }

    private fun manageCoupon() {
        if(!TextUtils.isEmpty(coupon_name)){
            mView!!.view2.visibility=View.VISIBLE
            mView!!.coupons_dicounts.visibility=View.VISIBLE
            mView!!.couponCode.visibility=View.VISIBLE
            mView!!.coupons_dicounts_price.visibility=View.VISIBLE
            mView!!.couponValue.visibility=View.VISIBLE
        }
        else{
            mView!!.view2.visibility=View.GONE
            mView!!.coupons_dicounts.visibility=View.GONE
            mView!!.couponCode.visibility=View.GONE
            mView!!.coupons_dicounts_price.visibility=View.GONE
            mView!!.couponValue.visibility=View.GONE
        }
    }

    private fun manageDataForparticularSCreen() {
        when (type) {
            "new" -> {
                mView!!.acceptRejectLayout.visibility=View.VISIBLE
                mView!!.deliveredLayout.visibility=View.GONE
                mView!!.deliveryStatusLayout.visibility=View.GONE
            }
            "past" -> {
                mView!!.acceptRejectLayout.visibility=View.GONE
                mView!!.deliveredLayout.visibility=View.VISIBLE
                mView!!.deliveryStatusLayout.visibility=View.GONE
            }
            else -> {

                mView!!.acceptRejectLayout.visibility=View.GONE
                mView!!.deliveredLayout.visibility=View.GONE
                mView!!.deliveryStatusLayout.visibility=View.VISIBLE

                trackProduct(AWBNumber)
            }
        }
    }

    inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int{
            return productFiles.size
        }

        override fun createFragment(position: Int): Fragment {

            val fragment= ProductViewPagerFragment(productFiles[position])
            return fragment
        }
    }

    private fun acceptReject() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("order_id", "user_id", "accept_reject", "message", "lang"),
                arrayOf(id_order.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
                        , accept_reject_string, message, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.acceptReject(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){

                            //Call create booking api
                                createBooking(newOrderData =myOrderData , id_order.toString())

                            findNavController().popBackStack()
                        }
                        else{
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

    private fun trackProduct(AWBNumber: String) {
        if(AWBNumber.isEmpty()){
            LogUtils.longToast(requireContext(), getString(R.string.your_tracking_number_is_empty_or_invalid))
            myTrackStatus = 0
            when (myTrackStatus) {
                1 -> {
                    mView!!.pickedUpImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                    mView!!.onTheWayImg.setImageResource(R.drawable.black_circle_ring)
                    mView!!.deliveredImg.setImageResource(R.drawable.black_circle_ring)
                }
                2 -> {
                    mView!!.pickedUpImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                    mView!!.onTheWayImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                    mView!!.deliveredImg.setImageResource(R.drawable.black_circle_ring)
                }
                3 -> {
                    mView!!.pickedUpImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                    mView!!.onTheWayImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                    mView!!.deliveredImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                }
                else -> {
                    LogUtils.longToast(requireContext(), getString(R.string.your_tracking_number_is_empty_or_invalid))
                    mView!!.pickedUpImg.setImageResource(R.drawable.black_circle_ring)
                    mView!!.onTheWayImg.setImageResource(R.drawable.black_circle_ring)
                    mView!!.deliveredImg.setImageResource(R.drawable.black_circle_ring)
                }
            }
        }else{
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mView!!.progressBar.visibility= View.VISIBLE

            val apiInterface = ApiClient.emiratesPostTrackGetClient()!!.create(ApiInterface::class.java)

            val call = apiInterface.getTrackDetails(AWBNumber)

            call!!.enqueue(object : Callback<TrackFinalResultResponse?> {
                override fun onResponse(call: Call<TrackFinalResultResponse?>, response: Response<TrackFinalResultResponse?>) {
                    mView!!.progressBar.visibility = View.GONE
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    try {
                        if (response.body() != null) {
                            val trackFinalResultArrayList = response.body()!!.trackFinalResult
                            if (trackFinalResultArrayList.isEmpty()){
                                myTrackStatus = 0
                            }else{
                                val subStatusCode = trackFinalResultArrayList[0].subStatusCode
                                myTrackStatus = when(subStatusCode){
                                    "B" -> 1
                                    "CLC9" -> 2
                                    "CLC11" -> 2
                                    "CLC12" -> 3
                                    "CLC30" -> 2
                                    "CLC55" -> 1
                                    else -> 0
                                }
                            }

                            when (myTrackStatus) {
                                1 -> {
                                    mView!!.pickedUpImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                                    mView!!.onTheWayImg.setImageResource(R.drawable.black_circle_ring)
                                    mView!!.deliveredImg.setImageResource(R.drawable.black_circle_ring)
                                }
                                2 -> {
                                    mView!!.pickedUpImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                                    mView!!.onTheWayImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                                    mView!!.deliveredImg.setImageResource(R.drawable.black_circle_ring)
                                }
                                3 -> {
                                    mView!!.pickedUpImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                                    mView!!.onTheWayImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                                    mView!!.deliveredImg.setImageResource(R.drawable.black_border_gold_bg_circle)
                                }
                                else -> {
                                    mView!!.pickedUpImg.setImageResource(R.drawable.black_circle_ring)
                                    mView!!.onTheWayImg.setImageResource(R.drawable.black_circle_ring)
                                    mView!!.deliveredImg.setImageResource(R.drawable.black_circle_ring)
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

                override fun onFailure(call: Call<TrackFinalResultResponse?>, throwable: Throwable) {
                    LogUtils.e("msg", throwable.message)
                    LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                    mView!!.progressBar.visibility = View.GONE
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            })
        }

    }




    private fun createBooking(newOrderData: MyOrders?, order_id: String) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE


        val apiInterface = ApiClient.emiratesPostGetClient()!!.create(ApiInterface::class.java)
        val receiverContactName = newOrderData!!.user_name
        val supplierContactName = newOrderData!!.supplierName
        var supplierCity = newOrderData!!.supplierCity
        val supplierMobileNumber = newOrderData!!.supplierMobileNumber
        val receiverPhoneArray = newOrderData.phone_number
        val receiverContactNumber = newOrderData.phone_number
        val receiverEmail = newOrderData.receiveremail
        val supplierEmail = newOrderData.supplierEmail
        val receiverLat = newOrderData.order_data.getJSONObject(0).getString("latitude")
        val receiverLong = newOrderData.order_data.getJSONObject(0).getString("longitude")
        var receiverCity = newOrderData.order_data.getJSONObject(0).getString("city")
        val receiverCityId = getCityId(receiverCity)
        val supplierCityId = getCityId(receiverCity)
        val userId = newOrderData!!.user_id
        val supplierId = newOrderData!!.supplier_id
        val numberOfPieces = newOrderData.order_data.length()
        val orderDate = newOrderData.orderBookingDate


        var myLength = 0
        var myWidth = 0
        var myHieght = 0
        var myWeight = 0
        for (i in 0 until newOrderData.order_data.length()){
            val myAttributes = newOrderData.order_data.getJSONObject(i).getJSONArray("attributes").get(0) as JSONObject
            myLength += Integer.parseInt(myAttributes.getString("length"))
            myWidth += Integer.parseInt(myAttributes.getString("width"))
            myHieght += Integer.parseInt(myAttributes.getString("height"))
            myWeight += Integer.parseInt(myAttributes.getString("weight"))
        }

        val iFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val oFormatter: DateFormat = SimpleDateFormat("dd-MMM-yyyy")
        val myCurrentBookingDate = oFormatter.format(iFormatter.parse(orderDate))
        val calendar = Calendar.getInstance()
        val date : Date = oFormatter.parse(myCurrentBookingDate)
        calendar.time = date
        calendar.add(Calendar.DATE, 2)
        val output: String = oFormatter.format(calendar.time)
        println(output)


        if (receiverCityId==1){
            receiverCity = "Dubai"
        }

        if (supplierCityId==1){
            supplierCity = "Dubai"
        }


        val myDataModel: MyJsonDataModel = MyJsonDataModel()
        val subDataModel: MyJsonDataModel.MyBookingRequest = MyJsonDataModel.MyBookingRequest()
        subDataModel.SenderContactName = supplierContactName
        subDataModel.SenderAddress = supplierCity
        subDataModel.SenderCity = supplierCityId
        subDataModel.SenderContactMobile = "05" + supplierMobileNumber
        subDataModel.SenderContactPhone = supplierMobileNumber
        subDataModel.SenderEmail = supplierEmail
        subDataModel.SenderState = supplierCity
        subDataModel.SenderCountry = 971
        subDataModel.ReceiverContactName = receiverContactName
        subDataModel.ReceiverAddress = receiverCity
        subDataModel.ReceiverCity = receiverCityId
        subDataModel.ReceiverContactMobile = "05" + receiverContactNumber
        subDataModel.ReceiverContactPhone = receiverContactNumber
        subDataModel.ReceiverEmail = receiverEmail
        subDataModel.ReceiverState = receiverCity
        subDataModel.ReceiverCountry = 971
        subDataModel.ContentTypeCode = "NonDocument"
        subDataModel.NatureType = 11
        subDataModel.Service = "Domestic"
        subDataModel.ShipmentType = "Express"
        subDataModel.DeleiveryType = "Door to Door"
        subDataModel.Registered = "No"
        subDataModel.PaymentType = "Credit"
        subDataModel.Pieces = numberOfPieces
        subDataModel.Weight = myWeight
        subDataModel.WeightUnit = "Grams"
        subDataModel.Length = myLength
        subDataModel.Width = myWidth
        subDataModel.Height = myHieght
        subDataModel.DimensionUnit = "Centimetre"
        subDataModel.SendMailToSender = "No"
        subDataModel.SendMailToReceiver = "No"
        subDataModel.PreferredPickupDate = output
        subDataModel.PreferredPickupTimeFrom = "10:00"
        subDataModel.PreferredPickupTimeTo = "19:00"
        subDataModel.IsReturnService = "No"
        subDataModel.PrintType = "AWBOnly"
        subDataModel.Latitude = receiverLat
        subDataModel.Longitude = receiverLong
        subDataModel.AWBType = "EAWB"
        subDataModel.RequestType = "Booking"

        myDataModel.BookingRequest = subDataModel


        val hashMap = HashMap<String, String>()
        hashMap.put("AccountNo", "C175120")
        hashMap.put("Password", "C175120")
        hashMap.put("Accept", "*/*")
        hashMap.put("Content-Type", "application/json")
        hashMap.put("Accept-Encoding", "gzip, deflate, br")
        hashMap.put("Connection", "keep-alive")

        val call = apiInterface.createBooking(hashMap, myDataModel)
        call!!.enqueue(object : Callback<BookingResultResponse?> {
            override fun onResponse(
                call: Call<BookingResultResponse?>,
                response: Response<BookingResultResponse?>
            ) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val BookingResponse = response.body()!!.BookingResponse
                        val AWBNumber = BookingResponse!!.AWBNumber

                        saveAWBNumber(AWBNumber, userId, supplierId,order_id)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<BookingResultResponse?>, t: Throwable) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Log.e("Ress", "Response4")
            }

        })

    }

    private fun saveAWBNumber(awbNumber: String?, userId: Int, supplierId: Int, order_id: String) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE


        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(
            arrayOf("AWBNumber", "user_id", "supplier_id", "order_id"),
            arrayOf(awbNumber!!.toString(), userId.toString(), supplierId.toString(), order_id)
        )

        val call = apiInterface.createbooking(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        Log.e("Ress", "Response5")
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

    private fun getCityId(cityname: String): Int {
        when {
            cityname.equals("Abu Dhabi", ignoreCase = false) -> return 2
            cityname.equals("Ajman", ignoreCase = false) -> return 4
            cityname.equals("Al Ain", ignoreCase = false) -> return 12
            cityname.equals("Dubai", ignoreCase = false) -> return 1
            cityname.equals("Fujairah", ignoreCase = false) -> return 5
            cityname.equals("Ras Al Khaimah", ignoreCase = false) -> return 6
            cityname.equals("Sharjah", ignoreCase = false) -> return 3
            cityname.equals("Umm Al Quwain", ignoreCase = false) -> return 7
            else -> return 1
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                OrderDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}