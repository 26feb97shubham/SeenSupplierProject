package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_colors.view.*

class ColorsAdapter(private val context: Context, private val data: ArrayList<String>, private val adapterNum:Int, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<ColorsAdapter.MyViewHolder>() {
    var selectPos=-1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_colors, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.colorName.setBackgroundColor(Color.parseColor(data[position]))
        if(selectPos==position){
            holder.itemView.viewBorder.visibility=View.VISIBLE
        }
        else{
            holder.itemView.viewBorder.visibility=View.GONE
        }
        holder.itemView.colorName.setOnClickListener {
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.AdapterClickCount, 0]>=adapterNum-1){
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.AdapterClickCount, adapterNum)
                selectPos=position
                notifyDataSetChanged()
                clickInstance.clickPostion(position)
            }



           /* if(!holder.itemView.viewBorder.isSelected){
                holder.itemView.viewBorder.isSelected=true
                notifyDataSetChanged()
                clickInstance.clickPostion(position)
            }*/


        }


    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}