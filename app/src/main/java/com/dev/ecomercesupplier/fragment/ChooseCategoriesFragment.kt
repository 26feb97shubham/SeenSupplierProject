package com.dev.ecomercesupplier.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CategoryListAdapter
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.CategoryName
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_choose_categories.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
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
 * Use the [ChooseCategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseCategoriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    var catNameList=ArrayList<CategoryName>()
    lateinit var categoryListAdapter: CategoryListAdapter
    var catIDArray= JSONArray()
    var user_id: String=""

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
        mView = inflater.inflate(R.layout.fragment_choose_categories, container, false)
        Utility.setLanguage(requireContext(), SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        getCategories()
        return mView
    }
    private fun setUpViews() {
        user_id=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
        requireActivity().main_View.setBackgroundResource(R.drawable.choose_categories_bg)
        requireActivity().other_frag_backImg.visibility= View.VISIBLE
        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView.btnContinue.setOnClickListener {
            mView.btnContinue.isEnabled=false
            mView.btnContinue.startAnimation(AlphaAnimation(1f, 0.5f))

            if(catIDArray.length()==0){
                LogUtils.shortToast(requireContext(), getString(R.string.please_select_categories))
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    mView.btnContinue.isEnabled=true
                }, 2000)
            }
            else{
                mView.btnContinue.isEnabled=true
                chooseCategories()
            }
        }
    }
    private fun getCategories() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getCategories(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
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
                            c.catNameAr=jsonObj.getString("name_ar")
                            c.image=jsonObj.getString("image")
                            c.checkCategoryStatus=jsonObj.getBoolean("checkCategoryStatus")
                            catNameList.add(c)
                        }

                        mView.rvList.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        categoryListAdapter= CategoryListAdapter(requireContext(), catNameList, object: ClickInterface.ClickArrayInterface{
                            override fun clickArray(idArray: JSONArray, nameArrayType : JSONArray) {
                                catIDArray= JSONArray()
                                catIDArray=idArray
                                mView.txtSelect.text = catIDArray.length().toString()+" " +getString(R.string.categories_selected)
                            }
                        })
                        mView.rvList.adapter=categoryListAdapter
                        catIDArray= JSONArray()
                        for(i in 0 until catNameList.size){
                            if(catNameList[i].checkCategoryStatus){
                                catIDArray.put(catNameList[i].id)
                            }
                        }
                        mView.txtSelect.text = catIDArray.length().toString()+" " +getString(R.string.categories_selected)
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
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun chooseCategories() {

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "category_array", "lang"),
            arrayOf(user_id, catIDArray.toString() , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.chooseCategories(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if (jsonObject.getInt("response") == 1){
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
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
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    override fun onResume() {
        super.onResume()
        requireActivity().main_View.setBackgroundResource(R.drawable.choose_categories_bg)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().main_View.setBackgroundColor(Color.WHITE)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().main_View.setBackgroundColor(Color.WHITE)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseCategoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ChooseCategoriesFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}