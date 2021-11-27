package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.model.CategoryName
import kotlinx.android.synthetic.main.item_global_list.view.*

class GlobalItemListAdapter (private val context: Context, private val data:ArrayList<CategoryName>): RecyclerView.Adapter<GlobalItemListAdapter.MyViewHolder>() {

    var selectPos: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_global_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.name.text = data[position].name
        holder.itemView.category.text = data[position].catName
        holder.itemView.txtRating.text = data[position].rating.toString()
        holder.itemView.ratingBar.rating = data[position].rating.toFloat()
        holder.itemView.imgLike.setOnClickListener {
            if(holder.isLike){
                holder.isLike=false
                holder.itemView.imgLike.setImageResource(R.drawable.heart_white)
            }
            else{
                holder.isLike=true
                holder.itemView.imgLike.setImageResource(R.drawable.heart_red)
            }
        }
        /* Glide.with(context).load(data[position].icon).placeholder(R.drawable.ic_launcher_foreground)
             .into(holder.itemView.img)

         holder.itemView.setOnClickListener {
             selectPos = position
             notifyDataSetChanged()
         }*/

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var isLike:Boolean=false
    }
}