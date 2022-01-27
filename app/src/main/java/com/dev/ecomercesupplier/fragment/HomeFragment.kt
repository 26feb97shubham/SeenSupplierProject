package com.dev.ecomercesupplier.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.activity.HomeActivity
import com.dev.ecomercesupplier.activity.LoginActivity
import com.dev.ecomercesupplier.adapter.NewItemsAdapter
import com.dev.ecomercesupplier.dialog.LogoutDialog
import com.dev.ecomercesupplier.model.Products
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.side_menu_layout.*
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

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    var newItemsList=ArrayList<Products>()
    var accessValue:Int=0
    var userId:Int=0
    var receiveOrdersCount:Int=0
    var response_body = ""
    lateinit var newItemsAdapter: NewItemsAdapter


//    lateinit var pagerAdapter:ScreenSlidePagerAdapter

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
        mView = inflater.inflate(R.layout.fragment_home, container, false)
        setUpViews()
        getHomes()
        return mView
    }

    private fun setUpViews() {

        if(!TextUtils.isEmpty(HomeActivity.type)){
            manageNotificationRedirection()
        }
        requireActivity().homebackImg.visibility=View.GONE
        requireActivity().other_frag_backImg.visibility=View.GONE


        userId= SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]

        setBottomView()
        requireActivity().itemHome1.setImageResource(R.drawable.selected_home)


        clickOnDrawer()

        clickOnBottomViewItems()

        clickOnHomeItems()

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver((mMessageReceiver),
                IntentFilter("MyData")
        )

    }
    val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            receiveOrderCount()
        }

    }
    private fun manageNotificationRedirection() {
       if(HomeActivity.type.equals("receive_order", true)){
           findNavController().navigate(R.id.receivedOrdersFragment)
       }
        HomeActivity.type=""
    }

    private fun clickOnHomeItems() {

        requireActivity().notificationImg.setOnClickListener {
            openNotificationsFragment()
        }

        requireActivity().other_frag_notificationImg.setOnClickListener {
            openNotificationsFragment()
        }

        requireActivity().frag_profile_notificationImg.setOnClickListener {
            openNotificationsFragment()
        }

        mView.btnNewReceiveOrders.setOnClickListener {
            mView.btnNewReceiveOrders.startAnimation(AlphaAnimation(1f, .5f))
            findNavController().navigate(R.id.action_homeFragment_to_receivedOrdersFragment)
        }
    }

    private fun openNotificationsFragment() {
        requireActivity().notificationImg.startAnimation(AlphaAnimation(1f, 0.5f))
        findNavController().navigate(R.id.notificationsFragment)
    }

    private fun clickOnBottomViewItems() {
       /* requireActivity().itemRevenue.setOnClickListener {
           itemrevenueclick()

        }
        requireActivity().itemMyOrders.setOnClickListener {
           itemordersclick()

        }
        requireActivity().itemHome.setOnClickListener {
           itemhomeclick()
        }*/

        requireActivity().itemRevenue1.setOnClickListener {
            itemrevenueclick()
        }


        requireActivity().itemMyOrders1.setOnClickListener {
            itemordersclick()
        }
        requireActivity().itemHome1.setOnClickListener {
            itemhomeclick()
        }


    }

    private fun itemordersclick() {
        if(findNavController().currentDestination?.id!=R.id.myOrdersFragment){
            requireActivity().itemMyOrders1.startAnimation(AlphaAnimation(1f, 0.5f))
            setBottomView()
            requireActivity().itemMyOrders1.setImageResource(R.drawable.selected_orders)
            findNavController().navigate(R.id.myOrdersFragment)
        }
    }

    private fun itemhomeclick() {
        if (findNavController().currentDestination?.id != R.id.homeFragment) {
            requireActivity().itemHome1.startAnimation(AlphaAnimation(1f, 0.5f))
            setBottomView()
            requireActivity().itemHome1.setImageResource(R.drawable.selected_home)
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun itemrevenueclick() {
        if(findNavController().currentDestination?.id!=R.id.revenueFragment){
            requireActivity().itemRevenue1.startAnimation(AlphaAnimation(1f, 0.5f))
            setBottomView()
            requireActivity().itemRevenue1.setImageResource(R.drawable.selected_revenues)
            findNavController().navigate(R.id.revenueFragment)
        }
    }


    private fun setBottomView() {
        requireActivity().itemRevenue1.setImageResource(R.drawable.wallet)
        requireActivity().itemHome1.setImageResource(R.drawable.home_icon_1)
        requireActivity().itemMyOrders1.setImageResource(R.drawable.check_list)

    }
    private fun getHomes() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.home_progress_bar.visibility = View.VISIBLE
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id","lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getHomes(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                mView.home_progress_bar.visibility = View.GONE
                try {
                    if (response.body() != null) {

                        val jsonObject = JSONObject(response.body()!!.string())
                        val profile = jsonObject.getJSONObject("profile")
                        requireActivity().txtName.setTextColor(
                                ContextCompat.getColor(
                                        requireContext(),
                                        R.color.black
                                )
                        )
                        requireActivity().txtName.text = profile.getString("name")
                        requireActivity().txtEmail.text = profile.getString("email")
                        Glide.with(requireContext()).load(profile.getString("profile_picture"))
                                .placeholder(R.drawable.user).into(requireActivity().userIcon)

                        val products = jsonObject.getJSONArray("products")
                        val response_products = JSONObject()
                        response_products.put("products", products)
                        response_products.put("response", 1)
                        response_products.put("message", "Data found.")
                        response_body = response_products.toString()
                        if(products.length()==0){
                            mView!!.mtv_no_new_items.visibility = View.VISIBLE
                            mView!!.rv_new_items_home.visibility = View.GONE
                        }else{
                            mView!!.mtv_no_new_items.visibility = View.GONE
                            mView!!.rv_new_items_home.visibility = View.VISIBLE
                        }

                        newItemsList.clear()

                        for (i in 0 until products.length()){
                            val jsonObj = products.getJSONObject(i)
                            val p = Products()
                            p.name = jsonObj.getString("name")
                            p.files = jsonObj.getString("files")
                            p.price = jsonObj.getString("price")
                            newItemsList.add(p)
                        }

                        rv_new_items_home.layoutManager = GridLayoutManager(requireContext(), 3)
                        newItemsAdapter = NewItemsAdapter(requireContext(), newItemsList,products, findNavController(), response_body)
                        rv_new_items_home.adapter = newItemsAdapter
                        newItemsAdapter.notifyDataSetChanged()

                        receiveOrdersCount=jsonObject.getInt("orders_count")
                        if(receiveOrdersCount!=0){
                            mView.btnNewReceiveOrders.isClickable = true
                            mView.mtv_new_received_orders_count.visibility=View.VISIBLE
                            mtv_new_received_orders.text = requireContext().getString(R.string.new_received_orders)
                            if(receiveOrdersCount>100){
                                mView.mtv_new_received_orders_count.text="100+"
                            }
                            else{
                                mView.mtv_new_received_orders_count.text=receiveOrdersCount.toString()
                            }
                        }
                        else{
                            mtv_new_received_orders.text = requireContext().getString(R.string.no_received_orders)
                            mView.btnNewReceiveOrders.isClickable = false
                            mView.mtv_new_received_orders_count.visibility=View.GONE
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
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                mView.home_progress_bar.visibility = View.GONE
            }
        })
    }

    private fun receiveOrderCount() {
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id","lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getHomes(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {

                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        receiveOrdersCount=jsonObject.getInt("orders_count")
                        if(receiveOrdersCount!=0){
                            mView.btnNewReceiveOrders.isClickable = true
                            mView.mtv_new_received_orders_count.visibility=View.VISIBLE
                            mView.mtv_new_received_orders.text = requireContext().getString(R.string.new_received_orders)
                            if(receiveOrdersCount>100){
                                mView.mtv_new_received_orders_count.text="100+"
                            }
                            else{
                                mView.mtv_new_received_orders_count.text=receiveOrdersCount.toString()
                            }
                        }
                        else{
                            mView.btnNewReceiveOrders.isClickable = false
                            mView.mtv_new_received_orders.text = requireContext().getString(R.string.no_received_orders)
                            mView.mtv_new_received_orders_count.visibility=View.GONE
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
            }
        })
    }

    private fun clickOnDrawer() {
        requireActivity().userIcon.setOnClickListener {
            requireActivity().userIcon.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.homeFragment)
        }
        requireActivity().llMyOrders.setOnClickListener {
            requireActivity().llMyOrders.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            setBottomView()
            requireActivity().itemMyOrders1.setImageResource(R.drawable.selected_orders)
            findNavController().navigate(R.id.myOrdersFragment)
        }
        requireActivity().txtName.setOnClickListener {
            requireActivity().txtName.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.homeFragment)
        }
        requireActivity().llContactUs.setOnClickListener {
            requireActivity().llContactUs.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.contactUSFragment)
        }
        requireActivity().llAccount.setOnClickListener {
            requireActivity().llAccount.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.profileFragment)
        }
        requireActivity().llCategory.setOnClickListener {
            requireActivity().llCategory.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.chooseCategoriesFragment)
        }
        requireActivity().llMyItems.setOnClickListener {
            requireActivity().llMyItems.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.myItemsFragment)
        }
        requireActivity().llLanguage.setOnClickListener {
            requireActivity().llLanguage.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.selectLangFragment)
        }
        requireActivity().llGallery.setOnClickListener {
            requireActivity().llGallery.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.galleryFragment)
        }
        requireActivity().llMyCoupons.setOnClickListener {
            requireActivity().llMyCoupons.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.myCouponsFragment)
        }
        requireActivity().llBankDetails.setOnClickListener {
            requireActivity().llBankDetails.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.bankDetailsFragment)
        }
        requireActivity().llUpgradePackage.setOnClickListener {
            requireActivity().llUpgradePackage.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.upgradePackagePlanFragment)
        }
        requireActivity().llLogout.setOnClickListener {
            requireActivity().llLogout.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)


            val logoutDialog = LogoutDialog()
            logoutDialog.isCancelable = false
            logoutDialog.setDataCompletionCallback(object : LogoutDialog.LogoutInterface {
                override fun complete() {
                    SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.UserId)
                    SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.IsLogin)
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finishAffinity()
                }
            })
            logoutDialog.show(requireActivity().supportFragmentManager, "HomeFragment")

        }
        requireActivity().llAboutUs.setOnClickListener {
            requireActivity().llAboutUs.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            val args=Bundle()
            args.putString("title", getString(R.string.about_us))
            findNavController().navigate(R.id.cmsFragment, args)
        }
        requireActivity().llFAQ.setOnClickListener {
            requireActivity().llFAQ.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            val args=Bundle()
            args.putString("title", getString(R.string.faq))
            findNavController().navigate(R.id.faqFragment, args)
        }
        requireActivity().llPrivacyPolicy.setOnClickListener {
            requireActivity().llPrivacyPolicy.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            val args=Bundle()
            args.putString("title", getString(R.string.privacy_and_policy))
            findNavController().navigate(R.id.cmsFragment, args)
        }
        requireActivity().llTermsConditions.setOnClickListener {
            requireActivity().llTermsConditions.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
            val args=Bundle()
            args.putString("title", getString(R.string.terms_amp_conditions))
            findNavController().navigate(R.id.cmsFragment, args)
        }
    }

   /* inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {

        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }*/

 /*   override fun onStop() {
        super.onStop()
        if(mView.rvList != null){
            mView.rvList.recycledViewPool.clear()
        }
    }*/

    override fun onResume() {
        super.onResume()
        /*requireActivity().other_frag_toolbar.visibility=View.VISIBLE
        requireActivity().homebackImg.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE*/
        requireActivity().other_frag_toolbar.visibility=View.VISIBLE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().fragment_home_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
    }
 override fun onDestroy() {
     super.onDestroy()
     requireActivity().other_frag_toolbar.visibility=View.VISIBLE
     requireActivity().fragment_home_toolbar.visibility=View.GONE
     requireActivity().toolbar.visibility=View.GONE
     requireActivity().about_us_fragment_toolbar.visibility=View.GONE
     LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver)
 }

    override fun onStop() {
        super.onStop()
        requireActivity().other_frag_toolbar.visibility=View.VISIBLE
        requireActivity().fragment_home_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().bottomNavigationView1.visibility = View.VISIBLE
    }
}