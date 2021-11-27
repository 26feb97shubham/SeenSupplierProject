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
import com.dev.ecomercesupplier.adapter.NewOrderAdapter
import com.dev.ecomercesupplier.dialog.RejectionDialog
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.MyOrders
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_my_orders.view.*
import kotlinx.android.synthetic.main.fragment_receive_orders.view.*
import kotlinx.android.synthetic.main.fragment_receive_orders.view.progressBar
import kotlinx.android.synthetic.main.fragment_receive_orders.view.rvList
import kotlinx.android.synthetic.main.fragment_receive_orders.view.swipeRefresh
import kotlinx.android.synthetic.main.fragment_receive_orders.view.txtNoDataFound
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
 * Use the [ReceivedOrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReceivedOrdersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    lateinit var newOrderAdapter: NewOrderAdapter
    var newOrderList=ArrayList<MyOrders>()
    var type="new"
    var accept_reject:String=""
    var message:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_receive_orders, container, false)
        setUpViews()
        receiveOrders(false)
        return mView
    }
    private fun setUpViews() {

        requireActivity().other_frag_backImg.visibility = View.VISIBLE


        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView!!.swipeRefresh.setOnRefreshListener {
            receiveOrders(true)
        }

        mView!!.rvList.layoutManager=LinearLayoutManager(requireContext())
        newOrderAdapter= NewOrderAdapter(requireContext(), newOrderList, object : ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="Reject"){
                    val rejectionDialog= RejectionDialog()
                    rejectionDialog.isCancelable=false
                    rejectionDialog.setDataCompletionCallback(object : RejectionDialog.RejectionInterface{
                        override fun complete(reason: String) {
                            accept_reject="2"
                            message=reason
                            acceptReject(pos)
                        }

                    })
                    rejectionDialog.show(requireActivity().supportFragmentManager, "ReceivedOrdersFragment")
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
                    bundle.putInt("id", newOrderList[pos].id)
                    bundle.putInt("accept_reject", newOrderList[pos].accept_reject)
                    bundle.putInt("status", newOrderList[pos].status)
                    bundle.putString("total_discount", newOrderList[pos].total_discount)
                    bundle.putString("coupon_name", newOrderList[pos].coupon_name)
                    findNavController().navigate(R.id.action_receivedOrdersFragment_to_orderDetailsFragment, bundle)
                }
            }

        })
        mView!!.rvList.adapter=newOrderAdapter

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
                            newOrderList.clear()

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
                                m.total_discount = obj.getString("total_discount")
                                m.coupon_name = obj.getString("coupon_name")
                                m.taxes = obj.getString("taxes")
                                m.total_price = obj.getString("total_price")
                                m.delivery_date = obj.getString("delivery_date")

                                newOrderList.add(m)
                            }

                        }

                        else {
                            newOrderList.clear()
                            mView!!.txtNoDataFound.visibility=View.VISIBLE
                            mView!!.rvList.visibility=View.GONE
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        }
                        newOrderAdapter.notifyDataSetChanged()

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
         * @return A new instance of fragment ReceiveOrdersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ReceivedOrdersFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}