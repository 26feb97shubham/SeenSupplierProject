package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_texts.view.*


class TextsAdapter(private val context: Context, private val data: ArrayList<String>, private val adapterNum:Int, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<TextsAdapter.MyViewHolder>() {
    var selectedPos=-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_texts, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.name.text=data[position]
        holder.itemView.name.isSelected = selectedPos==position
        holder.itemView.setOnClickListener {

            if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.AdapterClickCount, 0] >= adapterNum - 1) {
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.AdapterClickCount, adapterNum)
                selectedPos=position
                notifyDataSetChanged()
                clickInstance.clickPostion(position)
            }


        }


    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}