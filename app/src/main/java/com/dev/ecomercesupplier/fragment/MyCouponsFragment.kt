package com.dev.ecomercesupplier.fragment

import android.app.AlertDialog
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
import com.dev.ecomercesupplier.adapter.MyCouponsAdapter
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Coupons
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_my_coupons.view.*
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
 * Use the [MyCouponsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyCouponsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    var couponsList=ArrayList<Coupons>()
    lateinit var myCouponsAdapter: MyCouponsAdapter

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
        mView = inflater.inflate(R.layout.fragment_my_coupons, container, false)
        setUpViews()
        getMyCoupons(false)
        return mView
    }
    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility = View.VISIBLE


        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView.swipeRefresh.setOnRefreshListener {
            getMyCoupons(true)
        }

        mView.rvList.layoutManager= LinearLayoutManager(requireContext())
        myCouponsAdapter= MyCouponsAdapter(requireContext(), couponsList, object: ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="4"){
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(getString(R.string.delete))
                    builder.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_coupon))
                    builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        couponDelete(pos)
                    }
                    builder.setNegativeButton(getString(R.string.cancel), null)

                    val dialog = builder.create()
                    dialog.show()

                }
                else {
                    val args = Bundle()
                    args.putString("type", type)
                    args.putInt("id", couponsList[pos].id)
                    args.putString("title", couponsList[pos].title)
                    args.putString("description", couponsList[pos].description)
                    args.putString("code", couponsList[pos].code)
                    args.putString("from_date", couponsList[pos].from_date)
                    args.putString("to_date", couponsList[pos].to_date)
                    args.putString("min_price", couponsList[pos].min_price)
                    args.putString("max_discount_price", couponsList[pos].max_discount_price)
                    args.putString("percentage", couponsList[pos].percentage)
                    args.putString("picture", couponsList[pos].picture)
                    findNavController().navigate(R.id.action_myCouponsFragment_to_addCouponFragment, args)
                }
            }

        })
        mView.rvList.adapter=myCouponsAdapter

        mView.btnAddCoupon.setOnClickListener {
            mView.btnAddCoupon.startAnimation(AlphaAnimation(1f, 0.5f))
           /* val args= Bundle()
            args.putString("type", "1")
            args.putString("params", "")
            args.putString("pos", "")*/
            findNavController().navigate(R.id.action_myCouponsFragment_to_addCouponFragment)
        }
    }
    private fun getMyCoupons(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.getMyCoupons(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if(jsonObject.getInt("response")==1){
                            val coupons= jsonObject.getJSONArray("coupons")
                            couponsList.clear()

                            for(i in 0 until coupons.length()){
                                val obj = coupons.getJSONObject(i)
                                val c = Coupons()
                                c.id = obj.getInt("id")
                                c.title = obj.getString("title")
                                c.description = obj.getString("description")
                                c.percentage = obj.getString("percentage")
                                c.from_date = obj.getString("from_date")
                                c.to_date = obj.getString("to_date")
                                c.min_price = obj.getString("min_price")
                                c.max_discount_price = obj.getString("max_discount_price")
                                c.picture = obj.getString("picture")
                                c.code = obj.getString("code")
                                couponsList.add(c)
                            }


                        }
                        if(couponsList.size==0){
                            mView.txtNoCoupons.visibility=View.VISIBLE
                            mView.rvList.visibility=View.GONE
                        }
                        else{
                            mView.txtNoCoupons.visibility=View.GONE
                            mView.rvList.visibility=View.VISIBLE
                        }
                        myCouponsAdapter.notifyDataSetChanged()


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
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
            }
        })
    }
    private fun couponDelete(pos: Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("coupon_id", "lang"),
                arrayOf(couponsList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.couponDelete(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            myCouponsAdapter.notifyItemRemoved(pos)
                            couponsList.removeAt(pos)
                            myCouponsAdapter.notifyDataSetChanged()

                            if(couponsList.size==0){
                                mView.txtNoCoupons.visibility=View.VISIBLE
                                mView.rvList.visibility=View.GONE
                            }
                            else{
                                mView.txtNoCoupons.visibility=View.GONE
                                mView.rvList.visibility=View.VISIBLE
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
                mView.progressBar.visibility = View.GONE
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
         * @return A new instance of fragment MyCouponsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyCouponsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}