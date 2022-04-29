package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CategoryListAdapter
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.custom.Utility.Companion.direction
import com.dev.ecomercesupplier.custom.Utility.Companion.signUpcatIDList
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.CategoryName
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_choose_categories.*
import kotlinx.android.synthetic.main.activity_choose_categories.btnContinue
import kotlinx.android.synthetic.main.activity_choose_categories.progressBar
import kotlinx.android.synthetic.main.activity_choose_categories.rvList
import kotlinx.android.synthetic.main.fragment_choose_categories.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ChooseCategoriesActivity : AppCompatActivity() {
    var doubleClick:Boolean=false
    var catNameList=ArrayList<CategoryName>()
    lateinit var categoryListAdapter: CategoryListAdapter
    var catIDArray=JSONArray()
    var user_id: String=""
    var isClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_categories)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        getCategories()
    }


    private fun setUpViews() {
        if(intent.extras != null){
            user_id=intent.getStringExtra("user_id").toString()
        }
//        rvList.layoutManager=GridLayoutManager(this, 3)
        direction = "Register"
        rvList.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        categoryListAdapter= CategoryListAdapter(this, catNameList, object:ClickInterface.ClickArrayInterface{
            override fun clickArray(idArray: JSONArray, nameArrayType : JSONArray) {
                isClicked = true
                catIDArray= JSONArray()
                catIDArray=idArray
//                txtSelect.text = catIDArray.length().toString()+" " +getString(R.string.categories_selected)
            }


        })
        rvList.adapter=categoryListAdapter



        btnContinue.setOnClickListener {
            btnContinue.isEnabled=false
            btnContinue.startAnimation(AlphaAnimation(1f, 0.5f))

            if(!isClicked){
                catIDArray = signUpcatIDList
            }

            if(catIDArray.length()==0){
                LogUtils.shortToast(this, getString(R.string.please_select_categories))
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    btnContinue.isEnabled=true
                }, 2000)
            }
            else{
                btnContinue.isEnabled=true
                chooseCategories()
            }
        }
    }
    private fun getCategories() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(user_id, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))
        val call = apiInterface.getCategories(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
                            c.catNameAr=jsonObj.getString("name_ar")
                            c.image=jsonObj.getString("image")
                            c.checkCategoryStatus=jsonObj.getBoolean("checkCategoryStatus")
                            catNameList.add(c)
                        }
                        catIDArray= JSONArray()
                        for(i in 0 until catNameList.size){
                            if(catNameList[i].checkCategoryStatus){
                                catIDArray.put(catNameList[i].id)
                            }
                        }
//                        txtSelect.text = catIDArray.length().toString()+" " +getString(R.string.categories_selected)
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
                LogUtils.shortToast(this@ChooseCategoriesActivity, getString(R.string.check_internet))
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun chooseCategories() {

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "category_array", "lang"),
            arrayOf(user_id, catIDArray.toString() , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.chooseCategories(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if (jsonObject.getInt("response") == 1){
                            direction = ""
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, user_id.toInt())
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                            startActivity(Intent(this@ChooseCategoriesActivity, HomeActivity::class.java))

                        }
                        else{
                            LogUtils.shortToast(this@ChooseCategoriesActivity, jsonObject.getString("message"))
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
                LogUtils.shortToast(this@ChooseCategoriesActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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