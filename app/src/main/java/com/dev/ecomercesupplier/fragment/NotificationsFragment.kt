package com.dev.ecomercesupplier.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.NotificationsAdapter
import com.dev.ecomercesupplier.custom.SwipeToDeleteCallback
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Notifications
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.android.synthetic.main.fragment_notifications.view.progressBar
import kotlinx.android.synthetic.main.fragment_notifications.view.rvList
import kotlinx.android.synthetic.main.fragment_notifications.view.swipeRefresh
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
 * Use the [NotificationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView:View
    lateinit var notificationsAdapter: NotificationsAdapter
    var notificationList=ArrayList<Notifications>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_notifications, container, false)
        Utility.setLanguage(requireContext(), SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        getNotifications(false)
        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility= View.VISIBLE


        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView.swipeRefresh.setOnRefreshListener {
            getNotifications(true)
        }

        mView.rvList.layoutManager=LinearLayoutManager(requireContext())
        notificationsAdapter= NotificationsAdapter(requireContext(), notificationList, object :ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
                if(notificationList[pos].type=="receive_order"){
                    findNavController().navigate(R.id.receivedOrdersFragment)
                }
            }

        })
        mView.rvList.adapter=notificationsAdapter

        swipeLeftToDeleteItem()

    }
    private fun getNotifications(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.getNotifications(builder.build())
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
                            val notifications= jsonObject.getJSONArray("notifications")
                            notificationList.clear()

                            for(i in 0 until notifications.length()){
                                val obj = notifications.getJSONObject(i)
                                val n = Notifications()
                                n.id = obj.getInt("id")
                                n.title = obj.getString("title")
                                n.type = obj.getString("type")
                                n.time = obj.getString("time")
                                n.message = obj.getString("message")
                                n.seen = obj.getInt("seen")
                                notificationList.add(n)
                            }


                        }
                        if(notificationList.size==0){
                            mView.noNotificationView.visibility=View.VISIBLE
                            mView.rvList.visibility=View.GONE
                        }
                        else{
                            mView.noNotificationView.visibility=View.GONE
                            mView.rvList.visibility=View.VISIBLE
                        }
                        notificationsAdapter.notifyDataSetChanged()


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
    private fun swipeLeftToDeleteItem() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition //get position which is swipe
                if (direction == ItemTouchHelper.LEFT) {    //if swipe left
                    val builder = AlertDialog.Builder(requireContext()) //alert for confirm to delete
                    builder.setTitle(getString(R.string.delete)) //set message
                    builder.setMessage(getString(R.string.are_you_sure_you_want_to_delete_the_notification)) //set message
                    builder.setPositiveButton(getString(R.string.delete), DialogInterface.OnClickListener { dialog, which ->
                        notificationDelete(position)
                        //when click on DELETE
                        /*notificationsAdapter.notifyItemRemoved(position) //item removed from recylcerview
                        *//* sqldatabase.execSQL("delete from " + TABLE_NAME.toString() + " where _id='" + (position + 1).toString() + "'") //query for delete*//*
                        notificationList.removeAt(position) //then remove item*/
                        return@OnClickListener
                    }).setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which ->

                        //not removing items if cancel is done
                        notificationsAdapter.notifyItemRemoved(position + 1) //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                        notificationsAdapter.notifyItemRangeChanged(position, notificationsAdapter.getItemCount()) //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                        return@OnClickListener
                    }).show().setCanceledOnTouchOutside(false) //show alert dialog
                }
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(mView.rvList)
    }
    private fun notificationDelete(pos: Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("notification_id", "user_id", "lang"),
                arrayOf(notificationList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.notificationDelete(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            notificationsAdapter.notifyItemRemoved(pos)
                            notificationList.removeAt(pos)
                            notificationsAdapter.notifyDataSetChanged()
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
         * @return A new instance of fragment NotificationsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                NotificationsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().other_frag_notificationImg.visibility = View.GONE
        requireActivity().other_frag_toolbar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().other_frag_notificationImg.visibility = View.VISIBLE
        requireActivity().other_frag_toolbar.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().other_frag_notificationImg.visibility = View.VISIBLE
        requireActivity().other_frag_toolbar.visibility = View.VISIBLE
    }
}