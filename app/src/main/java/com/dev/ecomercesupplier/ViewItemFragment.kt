package com.dev.ecomercesupplier

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.adapter.AttributesAdapter
import com.dev.ecomercesupplier.adapter.ProductImageAdapter
import com.dev.ecomercesupplier.adapter.ViewAttrAdapter
import com.dev.ecomercesupplier.adapter.ViewItemAttrAdapter
import com.dev.ecomercesupplier.fragment.AddItemFragment
import com.dev.ecomercesupplier.fragment.OrderDetailsFragment
import com.dev.ecomercesupplier.fragment.ProductViewPagerFragment
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Attributes
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_view_attr.view.*
import kotlinx.android.synthetic.main.fragment_view_item.*
import kotlinx.android.synthetic.main.fragment_view_item.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewItemFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mView : View?=null
    var responseBody:String=""
    var pos:Int=-1
    var attrList=ArrayList<Attributes>()
    var productFiles=ArrayList<String>()
    var pagerAdapter: ScreenSlidePageAdapter?= null
    lateinit var productImageAdapter: ProductImageAdapter
    lateinit var viewAttrAdapter: ViewAttrAdapter
    var att_count:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            responseBody = it.getString("responseBody").toString()
            pos = it.getInt("pos", -1)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_view_item, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("response_view", responseBody)

        requireActivity().other_frag_backImg.visibility = View.VISIBLE

        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }


        if (responseBody != null) {
            val jsonObject = JSONObject(responseBody)
            if (jsonObject.getInt("response") == 1){
//                val product=jsonObject.getJSONArray("products")
                var pro_obj = jsonObject.getJSONObject("products")
//                for (i in 0 until product.length()){
//                    pro_obj = product.getJSONObject(i)
//                }
                mView!!.mtv_item_name.text= pro_obj.getString("name")
               /* mView!!.txtRating_naqashat.text=pro_obj.getDouble("product_rating").toString()
                mView!!.ratingBar_naqashat.rating=pro_obj.getDouble("product_rating").toFloat()*/
                if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                    mView!!.mtv_item_category.text=pro_obj.getString("category_name_ar")
                }else{
                    mView!!.mtv_item_category.text=pro_obj.getString("category_name")
                }

                mView!!.productDesc.text=pro_obj.getString("description")
                val all_files=pro_obj.getJSONArray("all_files")
                productFiles.clear()
                for(i in 0 until all_files.length()){
                    productFiles.add(all_files.getString(i))
                }
                val linearLayoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                mView!!.rv_images_videos.layoutManager= linearLayoutManager
                productImageAdapter = ProductImageAdapter(requireContext(), productFiles)


                val linearSnapHelper = LinearSnapHelper()
                linearSnapHelper.attachToRecyclerView(mView!!.rv_images_videos)

                val timer = Timer()
                timer.schedule(object : TimerTask(){
                    override fun run() {
                        if (linearLayoutManager.findLastCompletelyVisibleItemPosition()<(productImageAdapter.itemCount) - 1){
                            linearLayoutManager.smoothScrollToPosition(mView!!.rv_images_videos, RecyclerView.State(),
                                linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1)
                        }else {
                            linearLayoutManager.smoothScrollToPosition(mView!!.rv_images_videos, RecyclerView.State(), 0)
                        }
                    }

                }, 0, 5000)


                mView!!.rv_images_videos.adapter = productImageAdapter
                productImageAdapter.notifyDataSetChanged()
                var attributes_array = JSONArray()

                val attributes=pro_obj.getString("attributes")
                val a = Attributes()
                var data = JSONArray()
                if (attributes.isNotEmpty()) {
                    attributes_array = JSONArray(attributes)
                    attrList.clear()
                    for (m in 0 until attributes_array.length()) {
                        val obj = attributes_array.getJSONObject(m)
                        data = obj.getJSONArray("data")
                        for (i in 0 until data.length()){
                            val obj_2 = data.getJSONObject(i)
                            a.id = obj_2.getInt("id")
                            a.name = obj_2.getString("name")
                            a.name_ar = obj_2.getString("name_ar")
                            a.type = obj_2.getString("type")
                            if(obj_2.getString("type").equals("2", false)){
                                a.value = JSONArray("["+obj_2.getString("value").substring(1)+"]")

                            }else{
                                a.value = JSONArray("["+obj_2.getString("value")+"]")
                            }
                            attrList.add(a)
                        }
                        a.price = obj.getString("price")
                        a.quantity = obj.getString("quantity")
                        a.sold_out = obj.getString("sold_out")
                        attrList.add(a)
                    }
                }

                Log.e("Size : ", ""+attrList.size)

                Log.e("attr_list", attrList.toString())


                viewAttrAdapter= ViewAttrAdapter(requireContext(), attributes_array, data.length(), object :ClickInterface.ClickPosInterface{
                    override fun clickPostion(pos: Int) {
                    }

                })
                mView!!.rvList_attributes.adapter=viewAttrAdapter
            }

            else {
                LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    inner class ScreenSlidePageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment)  {
        override fun getItemCount(): Int{
            return productFiles.size
        }

        override fun createFragment(position: Int): Fragment {

            val fragment= ProductViewPagerFragment(productFiles[position])
            return fragment
        }
    }
}