package com.dev.ecomercesupplier.fragment

import android.content.Intent
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
import com.dev.ecomercesupplier.activity.PackageDetailsActivity
import com.dev.ecomercesupplier.adapter.PackagePlanAdapter
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.dialog.PackagePlanDialog
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.PackagePlans
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_upgrade_package_plan.view.*
import kotlinx.android.synthetic.main.item_package_plan.view.*
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
 * Use the [UpgradePackagePlanFragment.newInstance] factory method to
 * create an instance of requireContext() fragment.
 */
class UpgradePackagePlanFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    lateinit var packagePlanAdapter: PackagePlanAdapter
    var packList=ArrayList<PackagePlans>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for requireContext() fragment
        mView = inflater.inflate(R.layout.fragment_upgrade_package_plan, container, false)
        Utility.setLanguage(requireContext(), SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        getPackages(false)
        return mView
    }
    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility= View.VISIBLE


        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }
        mView.rvList.layoutManager= LinearLayoutManager(requireContext())
        packagePlanAdapter= PackagePlanAdapter(requireContext(), packList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
                val amt=packList[pos].country_currency+ " " +packList[pos].amount
                var packPlan = ""
                var detail = ""
                if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                    //packPlan=packList[pos].number.toString()+ " " +packList[pos].month_year_type_ar+ " " +packList[pos].string_ar + " "+requireContext().getString(R.string.plan)

                    if(packList[pos].number==3){
                        packPlan = "حزمة 3 شهور"
                    }else if (packList[pos].number==6){
                        packPlan = " حزمة 6 شهور"
                    }else if (packList[pos].number==1){
                        packPlan = "حزمة عام1"
                    }
                    detail=packList[pos].detail_ar
                }else{
                    packPlan=packList[pos].number.toString()+ " " +packList[pos].month_year_type+ " " +packList[pos].string + " "+requireContext().getString(R.string.plan)
                    detail=packList[pos].detail
                }
                val pack_id=packList[pos].id

                val packagePlanDialog = PackagePlanDialog(amt, packPlan, detail)
                packagePlanDialog.isCancelable = false
                packagePlanDialog.setDataCompletionCallback(object : PackagePlanDialog.PackagePlanInterface {
                    override fun complete() {
                        val args=Bundle()
                        args.putString("amount", amt)
                        args.putString("packagePlan", packPlan)
                        args.putString("detail", detail)
                        args.putInt("pack_id", pack_id)
                        findNavController().navigate(R.id.action_upgradePackagePlanFragment_to_paymentDetailsFragment, args)

                    }
                })
                packagePlanDialog.show(requireActivity().supportFragmentManager, "PackagePlanActivity")
            }

        })
        mView.rvList.adapter=packagePlanAdapter

        mView.swipeRefresh.setOnRefreshListener {
            getPackages(true)
        }

    }
    private fun getPackages(isReferesh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isReferesh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getPackages(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val packages = jsonObject.getJSONArray("packages")
                        packList.clear()
                        for (i in 0 until packages.length()) {
                            val jsonObj = packages.getJSONObject(i)
                            val pack = PackagePlans()
                            pack.id=jsonObj.getInt("id")
                            pack.number=jsonObj.getInt("number")
                            pack.amount=jsonObj.getString("amount")
                            pack.country_currency=jsonObj.getString("country_currency")
                            pack.detail=jsonObj.getString("detail")
                            pack.detail_ar=jsonObj.getString("detail_ar")
                            pack.string=jsonObj.getString("string")
                            pack.string_ar=jsonObj.getString("string_ar")
                            pack.month_year_type=jsonObj.getString("month_year_type")
                            pack.month_year_type_ar=jsonObj.getString("month_year_type_ar")
                            pack.color_code=jsonObj.getString("color_code")
                            pack.checkPackagePlan=jsonObj.getBoolean("checkPackagePlan")
                            packList.add(pack)
                        }
                        packagePlanAdapter.notifyDataSetChanged()

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
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    companion object {
        /**
         * Use requireContext() factory method to create a new instance of
         * requireContext() fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UpgradePackagePlanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                UpgradePackagePlanFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}