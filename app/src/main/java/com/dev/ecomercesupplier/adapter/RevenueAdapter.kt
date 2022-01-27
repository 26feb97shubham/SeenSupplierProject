package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.model.Revenue
import kotlinx.android.synthetic.main.layout_revenue_item.view.*

class RevenueAdapter(private val context: Context,
private val revenueList : ArrayList<Revenue>):
    RecyclerView.Adapter<RevenueAdapter.RevenueAdapterVH>() {
    inner class RevenueAdapterVH(itemView:View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RevenueAdapterVH {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_revenue_item, parent, false)
        return RevenueAdapterVH(view)
    }

    override fun onBindViewHolder(holder: RevenueAdapterVH, position: Int) {
        val revenue = revenueList[position]
        holder.itemView.mtv_user_name.text = revenue.user_name
        holder.itemView.mtv_order_id.text = "ORDER #${revenue.order_id}"
        holder.itemView.mtv_category_name.text = revenue.category_name
        holder.itemView.mtv_category_name.text = revenue.category_name
        holder.itemView.mtv_delivered_date.text = revenue.delivered_date
        holder.itemView.mtv_total_price.text = "AED ${revenue.total_price}"
    }

    override fun getItemCount(): Int {
        return revenueList.size
    }
}