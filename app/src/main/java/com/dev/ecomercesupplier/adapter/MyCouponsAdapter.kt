package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Coupons
import kotlinx.android.synthetic.main.item_my_coupons.view.*

class MyCouponsAdapter(private val context: Context, private val data: ArrayList<Coupons>, private val clickInstance: ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<MyCouponsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_my_coupons, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(data[position].picture).placeholder(R.drawable.default_icon).into(holder.itemView.img)
        holder.itemView.title.text=data[position].title
        holder.itemView.discount.text=data[position].percentage +"% " + context.getString(R.string.discount)
        holder.itemView.fromTo.text="From "+data[position].from_date+ " - "+data[position].to_date
        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "3")
        }
        holder.itemView.editCoupon.setOnClickListener {
            holder.itemView.editCoupon.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "2")
        }
        holder.itemView.deleteCoupon.setOnClickListener {
            holder.itemView.deleteCoupon.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "4")
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}