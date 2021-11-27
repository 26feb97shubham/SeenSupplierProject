package com.dev.ecomercesupplier.fragment

import android.os.Bundle
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
import com.dev.ecomercesupplier.dialog.RejectionDialog
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.MyOrders
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_my_orders.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyOrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyOrdersFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    lateinit var newOrderAdapter: NewOrderAdapter
    lateinit var currentOrderAdapter: CurrentOrderAdapter
    var newOrderList=ArrayList<MyOrders>()
    var currentOrderList=ArrayList<MyOrders>()
    var pastOrderList=ArrayList<MyOrders>()
    lateinit var pastOrderAdapter: PastOrderAdapter
    var type:String="past"
    var accept_reject:String=""
    var message:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*  if(mView==null) {*/
        mView = inflater.inflate(R.layout.fragment_my_orders, container, false)
        setUpViews()
        setPastTab()

//        }

        return mView
    }
    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility= View.VISIBLE
        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }
        mView!!.swipeRefresh.setOnRefreshListener {
            if(mView!!.newView.isSelected) {
                type="new"
                receiveOrders(true)
            }
            else if(mView!!.currentView.isSelected) {
                type="current"
                receiveOrders(true)
            }
            else{
                type="past"
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
        if(!isRefresh){
            mView!!.progressBar.visibility= View.VISIBLE
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("supplier_id", "type", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), type, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.receiveOrders(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView!!.swipeRefresh.isRefreshing){
                    mView!!.swipeRefresh.isRefreshing=false
                }
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            val carts=jsonObject.getJSONArray("carts")
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

                            mView!!.txtNoDataFound.visibility=View.GONE
                            mView!!.rvList.visibility=View.VISIBLE
                            for(i in 0  until carts.length()){
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
                                m.shipping_fee = obj.getString("shipping_fee")
                                m.taxes = obj.getString("taxes")
                                m.total_discount = obj.getString("total_discount")
                                m.coupon_name = obj.getString("coupon_name")
                                m.total_price = obj.getString("total_price")
                                m.delivery_date = obj.getString("delivery_date")

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

                        }

                        else {
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
                            mView!!.txtNoDataFound.visibility=View.VISIBLE
                            mView!!.rvList.visibility=View.GONE
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
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView!!.swipeRefresh.isRefreshing){
                    mView!!.swipeRefresh.isRefreshing=false
                }
            }
        })


    }

    private fun setPastTab() {
        mView!!.pastView.isSelected=true
        mView!!.newView.isSelected=false
        mView!!.currentView.isSelected=false
        pastOrderAdapter= PastOrderAdapter(requireContext(), pastOrderList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
                val bundle=Bundle()
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
                findNavController().navigate(R.id.action_myOrdersFragment_to_orderDetailsFragment, bundle)
            }

        })
        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView!!.rvList.adapter=pastOrderAdapter

        type="past"
        receiveOrders(false)

//        myOrder()

//        getCategories(false)

    }

    private fun setNewTab() {
        mView!!.newView.isSelected=true
        mView!!.currentView.isSelected=false
        mView!!.pastView.isSelected=false
        newOrderAdapter= NewOrderAdapter(requireContext(), newOrderList, object : ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="Reject"){
                    val rejectionDialog=RejectionDialog()
                    rejectionDialog.isCancelable=false
                    rejectionDialog.setDataCompletionCallback(object : RejectionDialog.RejectionInterface{
                        override fun complete(reason: String) {
                            accept_reject="2"
                            message=reason
                            acceptReject(pos)
                        }

                    })
                    rejectionDialog.show(requireActivity().supportFragmentManager, "MyOrders")
                }
                else if(type=="Accept"){
                    message=""
                    accept_reject="1"
                    acceptReject(pos)
                }
                else{
                    val bundle=Bundle()
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
                findNavController().navigate(R.id.action_myOrdersFragment_to_orderDetailsFragment, bundle)
                }
            }

        })

        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView!!.rvList.adapter=newOrderAdapter

        type="new"
        receiveOrders(false)

//        myOrder()

    }

    private fun setCurrentTab() {
        mView!!.currentView.isSelected=true
        mView!!.newView.isSelected=false
        mView!!.pastView.isSelected=false
        currentOrderAdapter= CurrentOrderAdapter(requireContext(), currentOrderList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
                val bundle=Bundle()
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
                bundle.putInt("status", currentOrderList[pos].status)
                findNavController().navigate(R.id.action_myOrdersFragment_to_orderDetailsFragment, bundle)
            }

        })

        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView!!.rvList.adapter=currentOrderAdapter

        type="current"
        receiveOrders(false)

    }
    private fun acceptReject(pos: Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("order_id", "user_id", "accept_reject", "message", "lang"),
                arrayOf(newOrderList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
                        , accept_reject, message, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.acceptReject(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            newOrderList.removeAt(pos)
                            newOrderAdapter.notifyDataSetChanged()
                            if(newOrderList.size==0){
                                mView!!.txtNoDataFound.visibility=View.VISIBLE
                                mView!!.rvList.visibility=View.GONE
                            }
                            else{
                                mView!!.txtNoDataFound.visibility=View.GONE
                                mView!!.rvList.visibility=View.VISIBLE
                            }

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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeMadeSuppliersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeMadeSuppliersFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}