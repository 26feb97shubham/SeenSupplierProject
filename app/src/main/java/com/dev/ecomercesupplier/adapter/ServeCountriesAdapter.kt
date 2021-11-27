package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Products
import com.dev.ecomercesupplier.model.ServeCountries
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.item_serve_countries.view.*

class ServeCountriesAdapter (private val context: Context, private val data: ArrayList<ServeCountries>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<ServeCountriesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_serve_countries, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(data[position].image).placeholder(R.drawable.default_icon).into(holder.itemView.imgCountry)
        holder.itemView.setOnClickListener {
            clickInstance.clickPostion(position)
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}