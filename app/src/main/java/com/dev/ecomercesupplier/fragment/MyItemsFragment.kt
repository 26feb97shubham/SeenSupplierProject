package com.dev.ecomercesupplier.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.MyItemAdapter
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Products
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.LogUtils.Companion.my_reference
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_my_items.view.*
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
 * Use the [MyItemsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyItemsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    var productList=ArrayList<Products>()
    var allItemList=ArrayList<Products>()

    lateinit var myItemAdapter: MyItemAdapter
    var responseBody:String=""
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
        mView = inflater.inflate(R.layout.fragment_my_items, container, false)
        //getItem(false)
        setUpViews()
        getItem(false)
        return mView
    }

    private fun setUpViews() {
//        HomeFragment.parentactivity!!.other_frag_toolbar.visibility = View.VISIBLE
//        HomeFragment.parentactivity!!.toolbar.visibility = View.GONE
       requireActivity().other_frag_backImg.visibility = View.VISIBLE

        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView.swipeRefresh.setOnRefreshListener {
            getItem(true)
        }

//        mView.edtSearch.addTextChangedListener(object : TextWatcher{
//            override fun afterTextChanged(s: Editable?) {
//                searchItem(s.toString())
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//        })

/*        mView.rvList.layoutManager=LinearLayoutManager(requireContext())
        myItemAdapter= MyItemAdapter(requireContext(), productList, object : ClickInterface.ClickPosTypeInterface {
            override fun clickPostionType(pos: Int, type: String) {
                if (type =="1"){
                    val bundle = Bundle()
                    bundle.putInt("pos", pos)
                    bundle.putString("responseBody", responseBody)
                    findNavController().navigate(R.id.viewItemFragment, bundle)
                }else if (type == "2") {
                    val bundle = Bundle()
                    bundle.putInt("pos", pos)
                    bundle.putString("responseBody", responseBody)
                    findNavController().navigate(R.id.action_myItemsFragment_to_addItemFragment, bundle)
                } else {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(getString(R.string.delete))
                    builder.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_product))
                    builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        deleteItem(pos)
                    }
                    builder.setNegativeButton(getString(R.string.cancel), null)

                    val dialog = builder.create()
                    dialog.show()

                }
            }

        })
        mView.rvList.adapter=myItemAdapter
        myItemAdapter.notifyDataSetChanged()*/

        mView.btnAddMore.setOnClickListener {
            mView.btnAddMore.startAnimation(AlphaAnimation(1f, 0.5f))
            my_reference = "add_edit"
            findNavController().navigate(R.id.action_myItemsFragment_to_addItemFragment)
        }
    }

    private fun searchItem(s: String) {
        if(!TextUtils.isEmpty(s)){
            val searchList=ArrayList<Products>()
            for(i in 0 until allItemList.size){
                if(allItemList[i].name.contains(s, true)){
                    searchList.add(allItemList[i])
                }
            }
            productList.clear()
            productList.addAll(searchList)
        }
        else{
            productList.clear()
            productList.addAll(allItemList)
        }
        myItemAdapter.notifyDataSetChanged()
    }

    private fun getItem(isRefresh: Boolean) {
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang", "type"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""], "1"))


        val call = apiInterface.getItem(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (mView.swipeRefresh.isRefreshing) {
                    mView.swipeRefresh.isRefreshing = false
                }
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        responseBody = response.body()!!.string()
//                        val jsonObject = JSONObject(response.body()!!.string())
                         val jsonObject = JSONObject(responseBody)
                        if (jsonObject.getInt("response") == 1) {
                            val products = jsonObject.getJSONArray("products")
                            productList.clear()

                            for (i in 0 until products.length()) {
                                val obj = products.getJSONObject(i)
                                /* val files_path=obj.getString("files_path")
                                var filekey=""
                                val files= obj.getJSONArray("files")
                               if(files.length()!=0){
                                   filekey= files[0].toString()
                               }*/
                                val p = Products()
                                p.id = obj.getInt("id")
                                p.name = obj.getString("name")
                                p.files = obj.getString("files")
                                p.price = obj.getString("price")
                                p.quantity = obj.getString("quantity")
                                p.category = obj.getString("category_name")
                                productList.add(p)
                            }


                            mView.rvList.layoutManager=LinearLayoutManager(requireContext())
                            myItemAdapter= MyItemAdapter(requireContext(), productList, object : ClickInterface.ClickPosTypeInterface {
                                override fun clickPostionType(pos: Int, type: String) {
                                    if (type =="1"){
                                        val bundle = Bundle()
                                        bundle.putInt("pos", pos)
                                        val response_body = JSONObject()
                                        response_body.put("response", 1)
                                        response_body.put("message", "Data found.")
                                        response_body.put("products", products[pos])
                                        bundle.putString("responseBody", response_body.toString())
                                        my_reference = "view"
                                        findNavController().navigate(R.id.viewItemFragment, bundle)
                                    }else if (type == "2") {
                                        val bundle = Bundle()
                                        bundle.putInt("pos", pos)
                                        bundle.putString("responseBody", responseBody)
                                        my_reference = "add_edit"
                                        findNavController().navigate(R.id.action_myItemsFragment_to_addItemFragment, bundle)
                                    } else {
                                        val builder = AlertDialog.Builder(requireContext())
                                        builder.setTitle(getString(R.string.delete))
                                        builder.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_product))
                                        builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                            deleteItem(pos)
                                        }
                                        builder.setNegativeButton(getString(R.string.cancel), null)

                                        val dialog = builder.create()
                                        dialog.show()

                                    }
                                }

                            })
                            mView.rvList.adapter=myItemAdapter
                            myItemAdapter.notifyDataSetChanged()


                        }
                        allItemList.clear()
                        allItemList.addAll(productList)
                        if (productList.size == 0) {
                            mView.txtNoItems.visibility = View.VISIBLE
                            mView.rvList.visibility = View.GONE
                        } else {
                            mView.txtNoItems.visibility = View.GONE
                            mView.rvList.visibility = View.VISIBLE
                        }


                       /* myItemAdapter.notifyDataSetChanged()*/

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
                if (mView.swipeRefresh.isRefreshing) {
                    mView.swipeRefresh.isRefreshing = false
                }
            }
        })
    }
    private fun deleteItem(pos: Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("product_id", "user_id", "lang"),
                arrayOf(productList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.deleteItem(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if (jsonObject.getInt("response") == 1) {
                            myItemAdapter.notifyItemRemoved(pos)
                            productList.removeAt(pos)
                            myItemAdapter.notifyDataSetChanged()
                            if (productList.size == 0) {
                                mView.txtNoItems.visibility = View.VISIBLE
                                mView.rvList.visibility = View.GONE
                            } else {
                                mView.txtNoItems.visibility = View.GONE
                                mView.rvList.visibility = View.VISIBLE
                            }

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
         * @return A new instance of fragment MyItemsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyItemsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}