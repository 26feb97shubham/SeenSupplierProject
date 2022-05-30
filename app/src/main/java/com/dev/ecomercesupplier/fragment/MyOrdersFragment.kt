package com.dev.ecomercesupplier.fragment

import android.icu.number.IntegerWidth
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CurrentOrderAdapter
import com.dev.ecomercesupplier.adapter.NewOrderAdapter
import com.dev.ecomercesupplier.adapter.PastOrderAdapter
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.dialog.RejectionDialog
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.BookingResultResponse
import com.dev.ecomercesupplier.model.MyCurrentOrderDataModel
import com.dev.ecomercesupplier.model.MyJsonDataModel
import com.dev.ecomercesupplier.model.MyOrders
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.google.common.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_my_orders.view.*
import okhttp3.ResponseBody
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
import com.google.gson.Gson
import java.lang.reflect.Type


class MyOrdersFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var mView: View? = null
    lateinit var newOrderAdapter: NewOrderAdapter
    lateinit var currentOrderAdapter: CurrentOrderAdapter
    var newOrderList = ArrayList<MyOrders>()
    var currentOrderList = ArrayList<MyOrders>()
    var pastOrderList = ArrayList<MyOrders>()
    lateinit var pastOrderAdapter: PastOrderAdapter
    var type: String = "past"
    var accept_reject: String = ""
    var message: String = ""

    private var myCurrentOrderDataList : ArrayList<MyCurrentOrderDataModel>?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*  if(mView==null) {*/
        mView = inflater.inflate(R.layout.fragment_my_orders, container, false)
        Utility.setLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        setPastTab()

//        }

        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility = View.VISIBLE
        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance()
                .hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }


        val gson = Gson()
        val json: String? = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.MyCurrentOrderData, null)
        var myType: Type = object : TypeToken<ArrayList<MyCurrentOrderDataModel?>?>() {}.getType()

        if (gson.fromJson<MyCurrentOrderDataModel>(json, myType) as ArrayList<MyCurrentOrderDataModel> == null) {
            myCurrentOrderDataList =ArrayList<MyCurrentOrderDataModel>()
        }else{
            myCurrentOrderDataList = gson.fromJson<MyCurrentOrderDataModel>(json, myType) as ArrayList<MyCurrentOrderDataModel>
        }

        mView!!.swipeRefresh.setOnRefreshListener {
            if (mView!!.newView.isSelected) {
                type = "new"
                receiveOrders(true)
            } else if (mView!!.currentView.isSelected) {
                type = "current"
                receiveOrders(true)
            } else {
                type = "past"
                receiveOrders(true)
            }
        }

        mView!!.newLayout.setOnClickListener {
            mView!!.newLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setNewTab()
        }
        mView!!.currentLayout.setOnClickListener {
            mView!!.currentLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setCurrentTab()
        }
        mView!!.pastLayout.setOnClickListener {
            mView!!.pastLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setPastTab()
        }

    }

    private fun receiveOrders(isRefresh: Boolean) {
        if (!isRefresh) {
            mView!!.progressBar.visibility = View.VISIBLE
        }
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(
            arrayOf("supplier_id", "type", "lang"),
            arrayOf(
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                type,
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]
            )
        )

        val call = apiInterface.receiveOrders(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (mView!!.swipeRefresh.isRefreshing) {
                    mView!!.swipeRefresh.isRefreshing = false
                }
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
                            val carts = jsonObject.getJSONArray("carts")
                            when (type) {
                                "past" -> {
                                    pastOrderList.clear()
                                }
                                "current" -> {
                                    currentOrderList.clear()
                                }
                                else -> {
                                    newOrderList.clear()
                                }
                            }

                            mView!!.txtNoDataFound.visibility = View.GONE
                            mView!!.rvList.visibility = View.VISIBLE
                            for (i in 0 until carts.length()) {
                                val obj = carts.getJSONObject(i)
                                val m = MyOrders()
                                m.id = obj.getInt("id")
                                m.accept_reject = obj.getInt("accept_reject")
                                m.status = obj.getInt("status")
                                m.order_id = obj.getString("order_id")
                                m.order_data = obj.getJSONArray("order_data")
                                m.subtotal = obj.getString("subtotal")
                                m.user_name = obj.getString("user_name")
                                m.user_image = obj.getString("user_image")
                                m.phone_number = obj.getString("phone_number")
                                m.receiveremail = obj.getString("email")
                                m.AWBNumber = obj.getString("AWBNumber")
                                m.shipping_fee = obj.getString("shipping_fee")
                                m.taxes = obj.getString("taxes")
                                m.total_discount = obj.getString("total_discount")
                                m.coupon_name = obj.getString("coupon_name")
                                m.total_price = obj.getString("total_price")
                                m.AWBNumber = obj.getString("AWBNumber")
                                m.orderBookingDate = obj.getString("created").substring(0,10)
                                m.delivery_date = obj.getString("delivery_date")
                                m.supplierName = obj.getString("supplier_user_name")
                                m.supplierMobileNumber = obj.getString("supplier_phone_number")
                                m.supplierEmail = obj.getString("supplier_email")
                                m.supplierCity = obj.getString("supplier_city")
                                m.user_id = obj.getInt("user_id")
                                m.supplier_id = obj.getInt("supplier_id")


                                when (type) {
                                    "past" -> {
                                        pastOrderList.add(m)
                                    }
                                    "current" -> {
                                        currentOrderList.add(m)
                                    }
                                    else -> {
                                        newOrderList.add(m)
                                    }
                                }
                            }


                        } else {
                            when (type) {
                                "past" -> {
                                    pastOrderList.clear()
                                }
                                "current" -> {
                                    currentOrderList.clear()
                                }
                                else -> {
                                    newOrderList.clear()
                                }
                            }
                            mView!!.txtNoDataFound.visibility = View.VISIBLE
                            mView!!.rvList.visibility = View.GONE
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        }
                        when (type) {
                            "past" -> {
                                pastOrderAdapter.notifyDataSetChanged()
                            }
                            "current" -> {
                                currentOrderAdapter.notifyDataSetChanged()
                            }
                            else -> {
                                newOrderAdapter.notifyDataSetChanged()
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
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (mView!!.swipeRefresh.isRefreshing) {
                    mView!!.swipeRefresh.isRefreshing = false
                }
            }
        })


    }

    private fun setPastTab() {
        mView!!.pastView.isSelected = true
        mView!!.newView.isSelected = false
        mView!!.currentView.isSelected = false
        pastOrderAdapter = PastOrderAdapter(
            requireContext(),
            pastOrderList,
            object : ClickInterface.ClickPosInterface {
                override fun clickPostion(pos: Int) {
                    val bundle = Bundle()
                    bundle.putString("type", "past")
                    bundle.putString("order_data", pastOrderList[pos].order_data.toString())
                    bundle.putString("delivery_date", pastOrderList[pos].delivery_date)
                    bundle.putString("shipping_fee", pastOrderList[pos].shipping_fee)
                    bundle.putString("subtotal", pastOrderList[pos].subtotal)
                    bundle.putString("taxes", pastOrderList[pos].taxes)
                    bundle.putString("total_price", pastOrderList[pos].total_price)
                    bundle.putString("order_id", pastOrderList[pos].order_id)
                    bundle.putString("user_name", pastOrderList[pos].user_name)
                    bundle.putString("user_image", pastOrderList[pos].user_image)
                    bundle.putString("phone_number", pastOrderList[pos].phone_number)
                    bundle.putString("total_discount", pastOrderList[pos].total_discount)
                    bundle.putString("coupon_name", pastOrderList[pos].coupon_name)
                    bundle.putInt("id", pastOrderList[pos].id)
                    bundle.putInt("accept_reject", pastOrderList[pos].accept_reject)
                    bundle.putInt("status", pastOrderList[pos].status)
                    bundle.putString("AWBNumber", pastOrderList[pos].AWBNumber)
                    val myOrder = pastOrderList[pos] as MyOrders
                    bundle.putSerializable("OrderData",myOrder)
                    findNavController().navigate(
                        R.id.action_myOrdersFragment_to_orderDetailsFragment,
                        bundle
                    )
                }

            })
        mView!!.rvList.layoutManager = LinearLayoutManager(requireContext())
        mView!!.rvList.adapter = pastOrderAdapter

        type = "past"
        receiveOrders(false)

//        myOrder()

//        getCategories(false)

    }

    private fun setNewTab() {
        mView!!.newView.isSelected = true
        mView!!.currentView.isSelected = false
        mView!!.pastView.isSelected = false
        newOrderAdapter = NewOrderAdapter(
            requireContext(),
            newOrderList,
            object : ClickInterface.ClickPosTypeInterface {
                override fun clickPostionType(pos: Int, type: String) {
                    if (type == "Reject") {
                        val rejectionDialog = RejectionDialog()
                        rejectionDialog.isCancelable = false
                        rejectionDialog.setDataCompletionCallback(object :
                            RejectionDialog.RejectionInterface {
                            override fun complete(reason: String) {
                                accept_reject = "2"
                                message = reason
                                acceptReject(pos)
                            }

                        })
                        rejectionDialog.show(requireActivity().supportFragmentManager, "MyOrders")
                    } else if (type == "Accept") {
                        message = ""
                        accept_reject = "1"
                        acceptReject(pos)
                    } else {
                        val bundle = Bundle()
                        bundle.putString("type", "new")
                        bundle.putString("order_data", newOrderList[pos].order_data.toString())
                        bundle.putString("delivery_date", newOrderList[pos].delivery_date)
                        bundle.putString("shipping_fee", newOrderList[pos].shipping_fee)
                        bundle.putString("subtotal", newOrderList[pos].subtotal)
                        bundle.putString("taxes", newOrderList[pos].taxes)
                        bundle.putString("total_price", newOrderList[pos].total_price)
                        bundle.putString("order_id", newOrderList[pos].order_id)
                        bundle.putString("user_name", newOrderList[pos].user_name)
                        bundle.putString("user_image", newOrderList[pos].user_image)
                        bundle.putString("phone_number", newOrderList[pos].phone_number)
                        bundle.putString("total_discount", newOrderList[pos].total_discount)
                        bundle.putString("coupon_name", newOrderList[pos].coupon_name)
                        bundle.putInt("id", newOrderList[pos].id)
                        bundle.putInt("accept_reject", newOrderList[pos].accept_reject)
                        bundle.putInt("status", newOrderList[pos].status)
                        bundle.putString("AWBNumber", newOrderList[pos].AWBNumber)
                        val myOrder = newOrderList[pos] as MyOrders
                        bundle.putSerializable("OrderData",myOrder)
                        findNavController().navigate(
                            R.id.action_myOrdersFragment_to_orderDetailsFragment,
                            bundle
                        )
                    }
                }

            })

        mView!!.rvList.layoutManager = LinearLayoutManager(requireContext())
        mView!!.rvList.adapter = newOrderAdapter

        type = "new"
        receiveOrders(false)

    }

    private fun setCurrentTab() {
        mView!!.currentView.isSelected = true
        mView!!.newView.isSelected = false
        mView!!.pastView.isSelected = false
        currentOrderAdapter = CurrentOrderAdapter(
            requireContext(),
            currentOrderList,
            object : ClickInterface.ClickPosInterface {
                override fun clickPostion(pos: Int) {
                    val bundle = Bundle()
                    bundle.putString("type", "current")
                    bundle.putString("order_data", currentOrderList[pos].order_data.toString())
                    bundle.putString("delivery_date", currentOrderList[pos].delivery_date)
                    bundle.putString("shipping_fee", currentOrderList[pos].shipping_fee)
                    bundle.putString("subtotal", currentOrderList[pos].subtotal)
                    bundle.putString("taxes", currentOrderList[pos].taxes)
                    bundle.putString("total_price", currentOrderList[pos].total_price)
                    bundle.putString("order_id", currentOrderList[pos].order_id)
                    bundle.putString("user_name", currentOrderList[pos].user_name)
                    bundle.putString("user_image", currentOrderList[pos].user_image)
                    bundle.putString("phone_number", currentOrderList[pos].phone_number)
                    bundle.putString("total_discount", currentOrderList[pos].total_discount)
                    bundle.putString("coupon_name", currentOrderList[pos].coupon_name)
                    bundle.putInt("id", currentOrderList[pos].id)
                    bundle.putInt("accept_reject", currentOrderList[pos].accept_reject)
                    var AWBNumber = ""

                    for (i in 0 until myCurrentOrderDataList!!.size){
                        if (currentOrderList[pos].order_id.equals(myCurrentOrderDataList!![i].orderId)){
                            AWBNumber = myCurrentOrderDataList!![i].AWBNumber!!.toString()
                        }
                    }

                    bundle.putString("AWBNumber", AWBNumber)
                    bundle.putInt("status", currentOrderList[pos].status)
                    val myOrder = currentOrderList[pos] as MyOrders
                    bundle.putSerializable("OrderData",myOrder)
                    findNavController().navigate(
                        R.id.action_myOrdersFragment_to_orderDetailsFragment,
                        bundle
                    )
                }

            })

        mView!!.rvList.layoutManager = LinearLayoutManager(requireContext())
        mView!!.rvList.adapter = currentOrderAdapter

        type = "current"
        receiveOrders(false)
    }


    private fun acceptReject(pos: Int) {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        mView!!.progressBar.visibility = View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(
            arrayOf("order_id", "user_id", "accept_reject", "message", "lang"),
            arrayOf(
                newOrderList[pos].id.toString(),
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                accept_reject,
                message,
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]
            )
        )


        val call = apiInterface.acceptReject(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if (jsonObject.getInt("response") == 1) {

                            createBooking(newOrderList[pos], newOrderList[pos].order_id.toString(), pos)


                        } else {
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

    private fun createBooking(newOrderData: MyOrders?, orderId: String, pos: Int) {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        mView!!.progressBar.visibility = View.VISIBLE

        val apiInterface = ApiClient.emiratesPostGetClient()!!.create(ApiInterface::class.java)
        val receiverContactName = newOrderData!!.user_name
        val supplierContactName = newOrderData!!.supplierName
        var supplierCity = newOrderData!!.supplierCity
        val supplierMobileNumber = newOrderData!!.supplierMobileNumber
        val receiverContactNumber = newOrderData.phone_number
        val receiverEmail = newOrderData.receiveremail
        val supplierEmail = newOrderData.supplierEmail
        val receiverLat = newOrderData.order_data.getJSONObject(0).getString("latitude")
        val receiverLong = newOrderData.order_data.getJSONObject(0).getString("longitude")
        var receiverCity = newOrderData.order_data.getJSONObject(0).getString("city")
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


        val receiverCityId = getCityId(receiverCity)
        val supplierCityId = getCityId(receiverCity)
        val userId = newOrderData!!.user_id
        val supplierId = newOrderData!!.supplier_id
        val numberOfPieces = newOrderData.order_data.length()
        val orderDate = newOrderData.orderBookingDate

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


        val myDataModel = MyJsonDataModel()
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

                        saveAWBNumber(AWBNumber, userId, supplierId,orderId, pos)
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

    private fun saveAWBNumber(
        awbNumber: String?,
        userId: Int,
        supplierId: Int,
        orderId: String,
        pos: Int
    ) {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        mView!!.progressBar.visibility = View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(
            arrayOf("AWBNumber", "user_id", "supplier_id", "order_id"),
            arrayOf(awbNumber!!.toString(), userId.toString(), supplierId.toString(), orderId)
        )

        val call = apiInterface.createbooking(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val gson = Gson()
                        val myCurrentOrderDataModel = MyCurrentOrderDataModel()
                        myCurrentOrderDataModel.orderId = orderId
                        myCurrentOrderDataModel.AWBNumber = awbNumber!!.toString()
                        myCurrentOrderDataList!!.add(myCurrentOrderDataModel)
                        val myCurrentOrderArrayListString = gson.toJson(myCurrentOrderDataList)
                        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.MyCurrentOrderData, myCurrentOrderArrayListString)
                        newOrderList.removeAt(pos)
                        newOrderAdapter.notifyDataSetChanged()
                        if (newOrderList.size == 0) {
                            mView!!.txtNoDataFound.visibility = View.VISIBLE
                            mView!!.rvList.visibility = View.GONE
                        } else {
                            mView!!.txtNoDataFound.visibility = View.GONE
                            mView!!.rvList.visibility = View.VISIBLE
                        }
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
}