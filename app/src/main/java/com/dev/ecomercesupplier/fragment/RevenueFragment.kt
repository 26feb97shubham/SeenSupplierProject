package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CategoryListAdapter
import com.dev.ecomercesupplier.adapter.CategoryRevenueAdapter
import com.dev.ecomercesupplier.adapter.RevenueAdapter
import com.dev.ecomercesupplier.custom.Utility.Companion.apiInterface
import com.dev.ecomercesupplier.custom.Utility.Companion.isSelectAll
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.CategoryName
import com.dev.ecomercesupplier.model.Revenue
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_revenue.*
import kotlinx.android.synthetic.main.fragment_revenue.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import android.widget.DatePicker

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import java.text.ParseException
import java.text.SimpleDateFormat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RevenueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RevenueFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView:View
    var catNameList=ArrayList<CategoryName>()
    lateinit var categoryListAdapter: CategoryListAdapter
    lateinit var categoryRevenueAdapter: CategoryRevenueAdapter
    lateinit var revenueAdapter: RevenueAdapter
    var catIDArray=JSONArray()
    var fromDate = ""
    var toDate = ""
    var revenueList = ArrayList<Revenue>()
    var category_clicked = false
    var by_default_selected = true
    var calendar: Calendar = Calendar.getInstance()
    var year: Int = calendar.get(Calendar.YEAR)
    var month: Int = calendar.get(Calendar.MONTH)
    var dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
    var dfDate: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

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
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_revenue, container, false)
        setUpViews()
        getCategories()
        getRevenues(by_default_selected)
        return mView
    }


    private fun setUpViews() {
        mView.cl_filtered_result.visibility = View.VISIBLE
        requireActivity().other_frag_backImg.visibility= View.VISIBLE
        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView!!.rv_categories.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        categoryRevenueAdapter= CategoryRevenueAdapter(requireContext(), catNameList, object: ClickInterface.ClickArrayInterface{
            override fun clickArray(idArray: JSONArray) {
                catIDArray= JSONArray()
                catIDArray=idArray
//                txtSelect.text = catIDArray.length().toString()+" " +getString(R.string.categories_selected)
            }
        })
        mView!!.rv_categories.adapter=categoryRevenueAdapter
        categoryRevenueAdapter.notifyDataSetChanged()

        mView!!.mtv_filter.setOnClickListener {
            mView.ll_filter.visibility=View.GONE
            by_default_selected = false
            getRevenues(by_default_selected)
        }

        mView.llFrom.setOnClickListener {
            by_default_selected = false
            DatePickerDialog(requireContext(),
                { datePicker, year, month, day ->
                    var d = ""
                    if (day>=1&&day<=9){
                        d = ("0"+day)
                    }else{
                        d = ""+day
                    }
                    mView.fromTime.setText(""+year + "-" + (month + 1) + "-" + d)
                    fromDate = mView.fromTime.text.toString()
                }, year, month, dayOfMonth
            ).show()
        }

        mView.llTo.setOnClickListener {
            by_default_selected = false
            DatePickerDialog(requireContext(),
                { datePicker, year, month, day ->
                    var d = ""
                    if (day>=1&&day<=9){
                        d = ("0"+day)
                    }else{
                        d = ""+day
                    }
                    mView.toTime.setText(""+year + "-" + (month + 1) + "-" + d)
                    toDate = mView.toTime.text.toString()
                    if (checkDates(fromDate, toDate)){
                        mView.cl_filtered_result.visibility = View.VISIBLE
                        getRevenues(by_default_selected)
                    }else{
                        mView.cl_filtered_result.visibility = View.GONE
                        LogUtils.shortToast(requireContext(),"To date is before than from date")
                    }
                }, year, month, dayOfMonth
            ).show()
        }

        mView.ll_category_filter.setOnClickListener {
            if (!category_clicked){
                category_clicked=true
                mView.ll_filter.visibility=View.VISIBLE
            }else{
                category_clicked=false
                mView.ll_filter.visibility=View.GONE
            }
        }

        mView!!.mtv_select_all.setOnClickListener {
            isSelectAll = true
            rv_categories.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            categoryRevenueAdapter= CategoryRevenueAdapter(requireContext(), catNameList, object: ClickInterface.ClickArrayInterface{
                override fun clickArray(idArray: JSONArray) {
                    catIDArray= JSONArray()
                    catIDArray=idArray
//                txtSelect.text = catIDArray.length().toString()+" " +getString(R.string.categories_selected)
                }
            })
            rv_categories.adapter=categoryRevenueAdapter
            categoryRevenueAdapter.notifyDataSetChanged()
        }

    }

    fun checkDates(fromDate : String, toDate : String) : Boolean{
        if (dfDate.parse(fromDate).before(dfDate.parse(toDate))){
            return true
        }else if (dfDate.parse(fromDate).equals(dfDate.parse(toDate))){
            return true
        }else{
            return false
        }
    }

    private fun getRevenues(by_default_selected: Boolean) {
        if (by_default_selected){
            val builder = ApiClient.createBuilder(arrayOf("supplier_id", "from_date", "to_date", "category_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                    fromDate,
                    toDate, "", SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))
            val call = apiInterface.revenues(builder.build())
            call!!.enqueue(object : Callback<ResponseBody?>{
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.body()!=null){
                        val jsonObject = JSONObject(response.body()!!.string())
                        val respstatus = jsonObject.getInt("response")
                        if (respstatus==1){
                            val carts = jsonObject.getJSONArray("carts")
                            revenueList?.clear()
                            if(carts.length()==0){
                                Log.e("ABBC", "No Carts found")
                                mView.rv_revenues.visibility = View.GONE
                            }else{
                                mView.rv_revenues.visibility = View.VISIBLE
                                for (i in 0 until carts.length()){
                                    val obj = carts.getJSONObject(i)
                                    val revenue = Revenue()
                                    revenue.order_id = obj.getString("order_id")
                                    revenue.user_name = obj.getString("user_name")
                                    revenue.total_price = obj.getString("total_price")
                                    revenue.delivered_date = obj.getString("delivered_date")
                                    val order_data = obj.getJSONArray("order_data")
                                    for (i in 0 until order_data.length()){
                                        val ob = order_data.getJSONObject(i)
                                        revenue.category_name = ob.getString("category_name")
                                    }
                                    revenue.date = jsonObject.getString("date")
                                    revenueList?.add(revenue)
                                }
                                mView.rv_revenues.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                if(revenueList?.size==0){
                                    LogUtils.shortToast(requireContext(), "No Revenue")
                                }else{
                                    revenueAdapter = revenueList?.let {
                                        RevenueAdapter(requireContext(),
                                            it
                                        )
                                    }!!
                                    mView.rv_revenues.adapter = revenueAdapter
                                    revenueAdapter.notifyDataSetChanged()
                                }
                            }
                            mView.mtv_from_to.text = jsonObject.getString("date")
                            mView.mtv_total_revenue.text = "AED ${jsonObject.getInt("total_revenues")}"

                        }else{
                            revenueList?.clear()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                    LogUtils.e("msg", throwable.message)
                }
            })
        }else{
            val builder = ApiClient.createBuilder(arrayOf("supplier_id", "from_date", "to_date", "category_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                    fromDate,
                    toDate, catIDArray.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))
            val call = apiInterface.revenues(builder.build())
            call!!.enqueue(object : Callback<ResponseBody?>{
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.body()!=null){
                        val jsonObject = JSONObject(response.body()!!.string())
                        val respstatus = jsonObject.getInt("response")
                        if (respstatus==1){
                            val carts = jsonObject.getJSONArray("carts")
                            revenueList?.clear()
                            if(carts.length()==0){
                                Log.e("ABBC", "No Carts found")
                            }else{
                                for (i in 0 until carts.length()){
                                    val obj = carts.getJSONObject(i)
                                    val revenue = Revenue()
                                    revenue.order_id = obj.getString("order_id")
                                    revenue.user_name = obj.getString("user_name")
                                    revenue.total_price = obj.getString("total_price")
                                    revenue.delivered_date = obj.getString("delivered_date")
                                    val order_data = obj.getJSONArray("order_data")
                                    for (i in 0 until order_data.length()){
                                        val ob = order_data.getJSONObject(i)
                                        revenue.category_name = ob.getString("category_name")
                                    }
                                    revenue.date = jsonObject.getString("date")
                                    revenueList?.add(revenue)
                                }
                                mView.rv_revenues.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                if(revenueList?.size==0){
                                    LogUtils.shortToast(requireContext(), "No Revenue")
                                }else{
                                    revenueAdapter = revenueList?.let {
                                        RevenueAdapter(requireContext(),
                                            it
                                        )
                                    }!!
                                    mView.rv_revenues.adapter = revenueAdapter
                                    revenueAdapter.notifyDataSetChanged()
                                }
                            }
                            mView.mtv_total_revenue.text = "AED ${jsonObject.getInt("total_revenues")}"

                        }else{
                            revenueList?.clear()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                    LogUtils.e("msg", throwable.message)
                }

            })
        }
    }

    private fun getCategories() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getCategories(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val categories = jsonObject.getJSONArray("categories")
                        catNameList.clear()
                        for (i in 0 until categories.length()) {
                            val jsonObj = categories.getJSONObject(i)
                            val c = CategoryName()
                            c.id=jsonObj.getInt("id")
                            c.name=jsonObj.getString("name")
                            catNameList.add(c)
                        }
                        catIDArray= JSONArray()
                        for(i in 0 until catNameList.size){
                            if(catNameList[i].checkCategoryStatus){
                                catIDArray.put(catNameList[i].id)
                            }
                        }
                        //mView.txtSelect.text = catIDArray.length().toString()+" " +getString(R.string.categories_selected)
                        mView.rv_categories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        categoryListAdapter = CategoryListAdapter(requireContext(), catNameList, object : ClickInterface.ClickArrayInterface{
                            override fun clickArray(idArray: JSONArray) {
                                catIDArray= JSONArray()
                                catIDArray=idArray
                            }

                        })
                        categoryListAdapter.notifyDataSetChanged()

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
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RevenueFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}