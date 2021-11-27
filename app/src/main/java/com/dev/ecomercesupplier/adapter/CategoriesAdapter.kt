package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Categories
import kotlinx.android.synthetic.main.categories_list.view.*
import kotlinx.android.synthetic.main.item_category.view.*

class CategoriesAdapter(private val context: Context, private val data: ArrayList<Categories>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view=LayoutInflater.from(context).inflate(R.layout.categories_list, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.name.text=data[position].name
        holder.itemView.setOnClickListener {
            clickInstance.clickPostion(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}