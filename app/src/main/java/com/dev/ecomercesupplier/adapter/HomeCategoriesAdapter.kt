package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.HomeCategories
import kotlinx.android.synthetic.main.item_home_categories.view.*

class HomeCategoriesAdapter(private val context:Context, private val data:ArrayList<HomeCategories>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<HomeCategoriesAdapter.MyViewHolder>() {

    var selectPos:Int=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_home_categories, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(selectPos==position){
            holder.itemView.iconView.setBackgroundResource(R.drawable.gold_border_black_bg_circle)
        }
        else{
            holder.itemView.iconView.setBackgroundResource(R.drawable.white_border_black_bg_circle)
        }
        holder.itemView.txtCatName.text=data[position].name
        Glide.with(context).load(data[position].icon).placeholder(R.drawable.ic_launcher_foreground).into(holder.itemView.img)

        holder.itemView.setOnClickListener {
            clickInstance.clickPostion(position)
            selectPos=position
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
       return data.size
    }
    class MyViewHolder(itemView:View): RecyclerView.ViewHolder(itemView)
}


