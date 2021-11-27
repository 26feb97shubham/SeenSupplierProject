package com.dev.ecomercesupplier.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.activity.ChooseLoginSignUpActivity
import com.dev.ecomercesupplier.activity.HomeActivity
import com.dev.ecomercesupplier.adapter.CategoryBannerAdapter
import com.dev.ecomercesupplier.adapter.GalleryListAdapter
import com.dev.ecomercesupplier.dialog.LogoutDialog
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Categories
import com.dev.ecomercesupplier.model.Galleries
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
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
    lateinit var categoryBannerAdapter: CategoryBannerAdapter
    lateinit var galleryListAdapter: GalleryListAdapter
    var galleryList=ArrayList<Galleries>()
    var catList=ArrayList<Categories>()
    var accessValue:Int=0
    var userId:Int=0
    var receiveOrdersCount:Int=0
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
        getHomes(false)
        return mView
    }

    private fun setUpViews() {

        if(!TextUtils.isEmpty(HomeActivity.type)){
            manageNotificationRedirection()
        }

        mView.mainView.visibility=View.GONE
//        requireActivity().other_frag_toolbar.visibility=View.GONE
//        requireActivity().fragment_home_toolbar.visibility=View.VISIBLE
        requireActivity().homebackImg.visibility=View.GONE


        userId= SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]

        setBottomView()
        requireActivity().itemHome.setImageResource(R.drawable.home_icon_1)


        clickOnDrawer()

        setGalleryAdapter()

        setCategoryAdapter()

        clickOnBottomViewItems()

        clickOnHomeItems()

        mView.swipeRefresh.setOnRefreshListener {
            getHomes(true)
        }

        /* pagerAdapter = ScreenSlidePagerAdapter(this)
        mView.viewPager2.adapter = pagerAdapter
        TabLayoutMediator(mView.tabLayout,   mView.viewPager2){ tab, position ->

        }.attach()*/

       /* mView.viewPager2.setPageTransformer(ZoomOutPageTransformer())*/




        mView.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_DRAGGING) mView.swipeRefresh.isEnabled = false
                if (newState == SCROLL_STATE_IDLE) mView.swipeRefresh.isEnabled = true
            }
        })

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
        mView.viewAllCat.setOnClickListener {
            mView.viewAllCat.startAnimation(AlphaAnimation(1f, 0.5f))
            findNavController().navigate(R.id.action_homeFragment_to_chooseCategoriesFragment)
        }

        mView.imgAttach.setOnClickListener {
            mView.imgAttach.startAnimation(AlphaAnimation(1f, 0.5f))
            findNavController().navigate(R.id.action_homeFragment_to_uploadImageVideoFragment)

        }

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
//            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        findNavController().navigate(R.id.notificationsFragment)
    }

    private fun clickOnBottomViewItems() {
        requireActivity().itemRevenue.setOnClickListener {
           itemrevenueclick()

        }
        requireActivity().itemMyOrders.setOnClickListener {
           itemordersclick()

        }
        requireActivity().itemHome.setOnClickListener {
           itemhomeclick()
        }

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
            requireActivity().itemMyOrders1.setImageResource(R.drawable.check_list)
            findNavController().navigate(R.id.myOrdersFragment)
        }
    }

    private fun itemhomeclick() {
        if (findNavController().currentDestination?.id != R.id.homeFragment) {
            requireActivity().itemHome1.startAnimation(AlphaAnimation(1f, 0.5f))
//            setBottomView()
            requireActivity().itemHome1.setImageResource(R.drawable.home_icon_1)
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun itemrevenueclick() {
        if(findNavController().currentDestination?.id!=R.id.revenueFragment){
            requireActivity().itemRevenue1.startAnimation(AlphaAnimation(1f, 0.5f))
            setBottomView()
            requireActivity().itemRevenue1.setImageResource(R.drawable.wallet)
            findNavController().navigate(R.id.revenueFragment)
        }
    }

    private fun setCategoryAdapter() {
        mView.rvListCat.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryBannerAdapter= CategoryBannerAdapter(requireContext(), catList)
        mView.rvListCat.adapter=categoryBannerAdapter

        mView.rvListCat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_DRAGGING) mView.swipeRefresh.isEnabled = false
                if (newState == SCROLL_STATE_IDLE) mView.swipeRefresh.isEnabled = true
            }
        })
    }

    private fun setGalleryAdapter() {
        /* val mLayoutManager=  GridLayoutManager(activity, 3)
        mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 ->{
                        SpanSize(1, 1)
                        1
                    }
                    3, accessValue -> {
                        accessValue=position+6
                        Log.d("accessValue", accessValue.toString())
                        SpanSize(2, 2)
                        2

                    }

                    else -> {
                        SpanSize(1, 1)
                        1

                    }
                }
            }
        }
        mView.rvList.layoutManager=mLayoutManager*/




        val spannedGridLayoutManager = SpannedGridLayoutManager(
            orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
            spans = 3)
        spannedGridLayoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
            when (position) {
                0 ->{
                    SpanSize(1, 1)
                }
                3, accessValue -> {
                    accessValue=position+6
                    Log.d("accessValue", accessValue.toString())
                    SpanSize(2, 2)
                }
                else -> SpanSize(1, 1)
            }
        }
        spannedGridLayoutManager.itemOrderIsStable = true
        mView.rvList.layoutManager =spannedGridLayoutManager


        galleryListAdapter= GalleryListAdapter(requireContext(), galleryList, object : ClickInterface.ClickPosInterface {
            override fun clickPostion(pos: Int) {
                val bundle=Bundle()
                bundle.putString("thumbnail", galleryList[pos].thumbnail)
                bundle.putString("files", galleryList[pos].files)
                findNavController().navigate(R.id.action_homeFragment_to_playerViewFragment, bundle)
            }

        })

//        mView.rvList.adapter=galleryListAdapter
    }

    private fun setBottomView() {
        requireActivity().itemRevenue.setImageResource(R.drawable.wallet)
        requireActivity().itemHome.setImageResource(R.drawable.home_icon_1)
        requireActivity().itemMyOrders.setImageResource(R.drawable.check_list)

//        setHostFragment()

    }
    private fun getHomes(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
//            mView.progressBar.visibility = View.VISIBLE
            mView.mainView.visibility=View.GONE
            mView.shimmerLayout.visibility=View.VISIBLE
            mView.shimmerLayout.startShimmerAnimation()
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id","lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getHomes(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
//                mView.progressBar.visibility = View.GONE
                mView.mainView.visibility=View.VISIBLE
                mView.shimmerLayout.visibility=View.GONE
                mView.shimmerLayout.stopShimmerAnimation()
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val categories = jsonObject.getJSONArray("categories")
                        catList.clear()
                        for (i in 0 until categories.length()) {
                                val jsonObj = categories.getJSONObject(i)
                                val cate = Categories()
                                cate.id = jsonObj.getInt("id")
                                cate.name = jsonObj.getString("name")
                                cate.image = jsonObj.getString("image")
                                catList.add(cate)
                        }
                        /*pagerAdapter.notifyDataSetChanged()*/

                        mView.pageIndicator.attachTo(mView.rvListCat)
                        /*if(mView.rvList != null){
                            mView.rvList.setItemViewCacheSize(catList.size)
                        }*/
                        categoryBannerAdapter.notifyDataSetChanged()

                        val gallery = jsonObject.getJSONArray("gallery")
                        galleryList.clear()
                        for (i in 0 until gallery.length()) {
                            val jsonObj = gallery.getJSONObject(i)
                            val g = Galleries()
                            g.id=jsonObj.getInt("id")
                            g.files=jsonObj.getString("files")
                            g.thumbnail=jsonObj.getString("thumbnail")
                            galleryList.add(g)
                        }
                        if(galleryList.size==0){
                            mView.rlUploadImgVdo.visibility=View.VISIBLE
                            mView.rvList.visibility=View.GONE
                        }
                        else{
                            mView.rlUploadImgVdo.visibility=View.GONE
                            mView.rvList.visibility=View.VISIBLE
                        }
                        mView.rvList.adapter=galleryListAdapter
//                        galleryListAdapter.notifyDataSetChanged()

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

                        receiveOrdersCount=jsonObject.getInt("orders_count")
                        if(receiveOrdersCount!=0){
                            mView.viewReceiveOrders.visibility=View.VISIBLE
                            if(receiveOrdersCount>100){
                                mView.recivedCount.text="100+"
                            }
                            else{
                                mView.recivedCount.text=receiveOrdersCount.toString()
                            }

                        }
                        else{
                            mView.viewReceiveOrders.visibility=View.GONE
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
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
//                mView.progressBar.visibility = View.GONE
                mView.mainView.visibility=View.VISIBLE
                mView.shimmerLayout.visibility=View.GONE
                mView.shimmerLayout.stopShimmerAnimation()
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
                            mView.viewReceiveOrders.visibility=View.VISIBLE
                            if(receiveOrdersCount>100){
                                mView.recivedCount.text="100+"
                            }
                            else{
                                mView.recivedCount.text=receiveOrdersCount.toString()
                            }

                        }
                        else{
                            mView.viewReceiveOrders.visibility=View.GONE
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
            requireActivity().itemMyOrders.setImageResource(R.drawable.check_list)
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

           /* val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.logout_excl))
            builder.setMessage(getString(R.string.are_you_sure_you_want_to_logout))
            builder.setPositiveButton(R.string.ok) { dialog, which ->
                dialog.cancel()
                SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.UserId)
                SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.IsLogin)
                startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
                requireActivity().finishAffinity()
            }
            builder.setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.cancel()

            }
            builder.show()*/


            val logoutDialog = LogoutDialog()
            logoutDialog.isCancelable = false
            logoutDialog.setDataCompletionCallback(object : LogoutDialog.LogoutInterface {
                override fun complete() {
                    SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.UserId)
                    SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.IsLogin)
                    startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
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
        requireActivity().other_frag_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.VISIBLE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().bottomNavigationView1.visibility = View.GONE
    }
 override fun onDestroy() {
     super.onDestroy()
     requireActivity().other_frag_toolbar.visibility=View.VISIBLE
     requireActivity().fragment_home_toolbar.visibility=View.GONE
     requireActivity().toolbar.visibility=View.GONE
     LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver)
 }

    override fun onStop() {
        super.onStop()
        requireActivity().other_frag_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().bottomNavigationView1.visibility = View.VISIBLE
    }
}