package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.PackagePlans
import kotlinx.android.synthetic.main.item_package_plan.view.*

class PackagePlanAdapter(private val context: Context, private val data:ArrayList<PackagePlans>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<PackagePlanAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_package_plan, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(data[position].checkPackagePlan){
            holder.itemView.llView.setBackgroundResource(R.drawable.black_selected_border)
        }
        else{
            holder.itemView.llView.setBackgroundResource(R.color.transparent)
        }
        holder.itemView.txtPrice.text = data[position].country_currency+ " " +data[position].amount
        holder.itemView.txtDuration.text = data[position].number.toString()+ " " +data[position].month_year_type +" "+data[position].string
        holder.itemView.packDesc.text = data[position].detail
//        holder.itemView.cardView.setCardBackgroundColor(Color.parseColor(data[position].color_code))
        holder.itemView.txtView.setOnClickListener {
            holder.itemView.txtView.startAnimation(AlphaAnimation(1f, 0.5f))
            clickInstance.clickPostion(position)

        }



    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}