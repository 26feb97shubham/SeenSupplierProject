package com.dev.ecomercesupplier.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.MyOrders
import com.dev.ecomercesupplier.model.OrderData
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.fragment_order_details.view.*
import kotlinx.android.synthetic.main.item_current_order.view.*
import kotlinx.android.synthetic.main.item_current_order.view.mobNum
import kotlinx.android.synthetic.main.item_current_order.view.orderNum
import kotlinx.android.synthetic.main.item_current_order.view.rvList
import kotlinx.android.synthetic.main.item_current_order.view.userName
import kotlinx.android.synthetic.main.item_current_order.view.userProfile

class CurrentOrderAdapter(private val context: Context, private val data:ArrayList<MyOrders>, private val clickInst: ClickInterface.ClickPosInterface): RecyclerView.Adapter<CurrentOrderAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_current_order, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.itemView.orderNum.text = context.getString(R.string.order_hess)+data[position].order_id
//         holder.itemView.deliveredDate.text = context.getString(R.string.delivery_date)+"  "+data[position].delivery_date
        holder.itemView.userName.text = data[position].user_name
        holder.itemView.mobNum.text = data[position].phone_number
        Glide.with(context).load(SharedPreferenceUtility.getInstance().get("profile_picture", "")).placeholder(R.drawable.user).into(holder.itemView.userProfile)

        holder.orderDataList.clear()
        for(i in 0 until data[position].order_data.length()){
            val obj1 = data[position].order_data.getJSONObject(i)
            val o = OrderData()
            o.quantity = obj1.getInt("quantity")
            o.id = obj1.getInt("product_id")
            o.country = obj1.getString("country")
            o.address = obj1.getString("address")
            o.category_name = obj1.getString("category_name")
            o.price = obj1.getString("price")
            o.files = obj1.getString("files")
            o.product_name = obj1.getString("product_name")
            o.product_description= obj1.getString("product_description")
            o.attributes = obj1.getJSONArray("attributes")

            holder.orderDataList.add(o)
        }

        holder.currentDataAdapter= CurrentDataAdapter(context,  holder.orderDataList, object :ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
                clickInst.clickPostion(position)
            }

        })
        holder.itemView.rvList.layoutManager= LinearLayoutManager(context)
        holder.itemView.rvList.adapter= holder.currentDataAdapter

        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, 0.5f))
            clickInst.clickPostion(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var orderDataList=ArrayList<OrderData>()
        lateinit var currentDataAdapter: CurrentDataAdapter

    }
}