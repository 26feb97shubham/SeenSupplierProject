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
import com.dev.ecomercesupplier.model.Products
import kotlinx.android.synthetic.main.item_my_item.view.*

class MyItemAdapter(private val context: Context, private val data: ArrayList<Products>, private val clickInstance: ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<MyItemAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_my_item, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(data[position].files).placeholder(R.drawable.default_icon).into(holder.itemView.img)
        holder.itemView.name.text=data[position].name
//        holder.itemView.category.text=data[position].category
        holder.itemView.price.text=context.getString(R.string.aed)+" "+data[position].price
        holder.itemView.stockCount.text=data[position].quantity+" "+context.getString(R.string.pcs_in_stock)

        holder.itemView.imgEdit.setOnClickListener {
            holder.itemView.imgEdit.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "2")
        }
        holder.itemView.imgDelete.setOnClickListener {
            holder.itemView.imgEdit.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "3")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}