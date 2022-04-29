package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.PackagePlanAdapter
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.dialog.PackagePlanDialog
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.PackagePlans
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_package_plan.*
import kotlinx.android.synthetic.main.activity_package_plan.progressBar
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PackagePlanActivity : AppCompatActivity() {
    var user_id: String=""
    var doubleClick:Boolean=false
    lateinit var packagePlanAdapter: PackagePlanAdapter
    var packList=ArrayList<PackagePlans>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_plan)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        getPackages(false)
    }

    private fun setUpViews() {
        if(intent.extras != null){
            user_id = intent.extras!!.getString("user_id").toString()
        }
        rvList.layoutManager=LinearLayoutManager(this)
        packagePlanAdapter= PackagePlanAdapter(this, packList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
                val amt=packList[pos].country_currency+ " " +packList[pos].amount
                var packPlan = ""
                var detail = ""
                if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                    if(packList[pos].number==3){
                        packPlan = "حزمة 3 شهور"
                    }else if (packList[pos].number==6){
                        packPlan = " حزمة 6 شهور"
                    }else if (packList[pos].number==1){
                        packPlan = "حزمة عام1"
                    }
                    detail=packList[pos].detail_ar
                }else{
                    packPlan=packList[pos].number.toString()+ " " +packList[pos].month_year_type+ " " +packList[pos].string + " "+getString(R.string.plan)
                    detail=packList[pos].detail
                }

                val pack_id=packList[pos].id

                val packagePlanDialog = PackagePlanDialog(amt, packPlan, detail)
                packagePlanDialog.isCancelable = true
                packagePlanDialog.setDataCompletionCallback(object : PackagePlanDialog.PackagePlanInterface {
                    override fun complete() {
                        startActivity(Intent(this@PackagePlanActivity, PackageDetailsActivity::class.java).putExtra("amount", amt)
                                .putExtra("packagePlan", packPlan).putExtra("detail", detail).putExtra("pack_id", pack_id).putExtra("user_id", user_id))
                    }
                })
                packagePlanDialog.show(supportFragmentManager, "PackagePlanActivity")
            }

        })
        rvList.adapter=packagePlanAdapter

        swipeRefresh.setOnRefreshListener {
            getPackages(true)
        }

    }
    private fun getPackages(isReferesh: Boolean) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isReferesh){
            progressBar.visibility= View.VISIBLE
        }


        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(user_id, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))
        val call = apiInterface.getPackages(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(swipeRefresh.isRefreshing){
                    swipeRefresh.isRefreshing=false
                }
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
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(this@PackagePlanActivity, getString(R.string.check_internet))
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(swipeRefresh.isRefreshing){
                    swipeRefresh.isRefreshing=false
                }
            }
        })
    }
    override fun onBackPressed() {
        exitApp()
    }
    private fun exitApp() {
        val toast = Toast.makeText(
                this,
                getString(R.string.please_click_back_again_to_exist),
                Toast.LENGTH_SHORT
        )

        if(doubleClick){
            finishAffinity()
            doubleClick=false
        }
        else{

            doubleClick=true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                toast.show()
                doubleClick=false
            }, 300)
        }
    }
}