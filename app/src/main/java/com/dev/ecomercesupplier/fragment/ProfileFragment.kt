package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CategoriesAdapter
import com.dev.ecomercesupplier.adapter.ProductAdapter
import com.dev.ecomercesupplier.adapter.ServeCountriesAdapter
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Categories
import com.dev.ecomercesupplier.model.Products
import com.dev.ecomercesupplier.model.ServeCountries
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.frag_profile.view.*
import kotlinx.android.synthetic.main.side_top_view.*
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

class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView:View
    lateinit var productAdapter: ProductAdapter
    lateinit var categoryAdapter: CategoriesAdapter
    lateinit var serveCountriesAdapter: ServeCountriesAdapter
    var productList=ArrayList<Products>()
    var catList=ArrayList<Categories>()
    var serveCountriesList=ArrayList<ServeCountries>()
    var responseData:String=""
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
        mView = inflater.inflate(R.layout.frag_profile, container, false)
//        requireActivity().other_frag_toolbar.visibility=View.GONE
//        requireActivity().toolbar.visibility=View.GONE
        setUpViews()
        getProfile(false)
        return mView
    }
    private fun setUpViews() {
        requireActivity().frag_profile_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

//        mView.rvCategories.layoutManager=GridLayoutManager(requireContext(), 4)
        mView.categories_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter= CategoriesAdapter(requireContext(), catList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
            }
        })


        mView.swipeRefresh.setOnRefreshListener {
            getProfile(true)
        }

        mView.categories_rvlist.adapter=categoryAdapter


        mView.rvProducts.layoutManager=GridLayoutManager(requireContext(), 4)
        productAdapter= ProductAdapter(requireContext(), productList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {

            }

        })

        mView.rvProducts.adapter=productAdapter
//
//        mView.viewAllCat.setOnClickListener {
//            mView.viewAllCat.startAnimation(AlphaAnimation(1f, .5f))
//            findNavController().navigate(R.id.chooseCategoriesFragment)
//
//        }
        mView.viewAllProducts.setOnClickListener {
            mView.viewAllProducts.startAnimation(AlphaAnimation(1f, .5f))
            findNavController().navigate(R.id.myItemsFragment)

        }

        mView.countries_rvlist.layoutManager=LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
        serveCountriesAdapter= ServeCountriesAdapter(requireContext(), serveCountriesList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
            }

        })

        mView.countries_rvlist.adapter=serveCountriesAdapter

        requireActivity().tv_editprofile.setOnClickListener {
            requireActivity().tv_editprofile.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("responseData", responseData)
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment, bundle)
        }
    }
    private fun getProfile(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.getProfile(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                try {
                    if (response.body() != null) {
                        responseData=response.body()!!.string()
                        val jsonObject = JSONObject(responseData)

                        if(jsonObject.getInt("response")==1){
                            val data= jsonObject.getJSONObject("data")
                            mView.txtBrand.text= data.getString("name")
                            mView.txtSupplier.text= data.getString("account_name")
                            mView.txtBioContent.text= data.getString("bio")
                            mView.address.text= data.getString("country_name")
                            mView.txtRating.text= data.getDouble("rating").toString()
                            mView.ratingBar.rating= data.getDouble("rating").toFloat()
                            Glide.with(requireContext()).load(data.getString("profile_picture")).placeholder(R.drawable.user).into(requireActivity().img)

                            requireActivity().txtName.text= data.getString("name")
                            requireActivity().txtEmail.text= data.getString("email")
                            Glide.with(requireContext()).load(data.getString("profile_picture")).placeholder(R.drawable.user).into(requireActivity().userIcon)


                            val countries= data.getJSONArray("countries")
                            serveCountriesList.clear()
                            for(i in 0 until countries.length()) {
                                val obj = countries.getJSONObject(i)
                                if (obj.getInt("countries_to_be_served") == 1) {
                                    val s = ServeCountries()
                                    s.id = obj.getInt("id")
                                    s.country_name = obj.getString("country_name")
                                    s.image = obj.getString("image")
                                    serveCountriesList.add(s)
                                }
                            }
                            serveCountriesAdapter.notifyDataSetChanged()

                            val categories= jsonObject.getJSONArray("categories")
                            catList.clear()
                            for(i in 0 until categories.length()){
                                if(catList.size<4) {
                                    val obj = categories.getJSONObject(i)
                                    val c = Categories()
                                    c.id = obj.getInt("id")
                                    c.name = obj.getString("name")
                                    c.image = obj.getString("image")
                                    catList.add(c)
                                }
                            }
                           /* if(catList.size==0){
                                mView.txtNoCategory.visibility=View.VISIBLE
                                mView.rvCategories.visibility=View.GONE
                            }
                            else{
                                mView.txtNoCategory.visibility=View.GONE
                                mView.rvCategories.visibility=View.VISIBLE
                            }*/
                            categoryAdapter.notifyDataSetChanged()

                            val products= jsonObject.getJSONArray("products")
                            productList.clear()
                            for(i in 0 until products.length()){
                                if(productList.size<4) {
                                    val obj = products.getJSONObject(i)
                                    val p = Products()
                                    p.id = obj.getInt("id")
                                    p.name = obj.getString("name")
                                    p.files = obj.getString("files")
                                    p.price = obj.getString("price")
                                    p.quantity = obj.getString("quantity")
                                    p.category = obj.getString("category_name")
                                    productList.add(p)
                                }
                            }
                            if(productList.size==0){
                                mView.txtNoProduct.visibility=View.VISIBLE
                                mView.rvProducts.visibility=View.GONE
                            }
                            else{
                                mView.txtNoProduct.visibility=View.GONE
                                mView.rvProducts.visibility=View.VISIBLE
                            }
                            productAdapter.notifyDataSetChanged()

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
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
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
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().toolbar.visibility = View.GONE
        requireActivity().other_frag_toolbar.visibility = View.GONE
        requireActivity().profile_fragment_toolbar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().other_frag_toolbar.visibility = View.GONE
        requireActivity().profile_fragment_toolbar.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().other_frag_toolbar.visibility = View.VISIBLE
        requireActivity().profile_fragment_toolbar.visibility = View.GONE
        requireActivity().other_frag_toolbar.setBackgroundResource(R.color.white)
    }
}