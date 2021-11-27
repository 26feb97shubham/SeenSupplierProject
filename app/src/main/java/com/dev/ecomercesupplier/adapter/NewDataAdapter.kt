package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.OrderData
import kotlinx.android.synthetic.main.item_new_data.view.*

class NewDataAdapter(private val context: Context, private val data:ArrayList<OrderData>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<NewDataAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_new_data, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        for(i in 0 until data[position].attributes.length()){
            val obj=data[position].attributes.getJSONObject(i)
            if(i==0){
                if(obj.getString("type")=="1"){
                    holder.itemView.attr1Txt.text=obj.getString("name")
                    holder.itemView.attr1.text=obj.getString("value")
                }
                else{
                    holder.itemView.attr1Txt.text=obj.getString("name")
                    holder.itemView.attr1.setBackgroundColor(Color.parseColor(obj.getString("value")))
                }

            }
            else if(i==1){
                holder.itemView.attr2Txt.visibility=View.VISIBLE
                holder.itemView.attr2.visibility=View.VISIBLE
                if(obj.getString("type")=="1"){
                    holder.itemView.attr2Txt.text=obj.getString("name")
                    holder.itemView.attr2.text=obj.getString("value")
                }
                else{
                    holder.itemView.attr2Txt.text=obj.getString("name")
                    holder.itemView.attr2.setBackgroundColor(Color.parseColor(obj.getString("value")))
                }

            }
            else if(i==2){
                holder.itemView.attr3Txt.visibility=View.VISIBLE
                holder.itemView.attr3.visibility=View.VISIBLE
                if(obj.getString("type")=="1"){
                    holder.itemView.attr3Txt.text=obj.getString("name")
                    holder.itemView.attr3.text=obj.getString("value")
                }
                else{
                    holder.itemView.attr3Txt.text=obj.getString("name")
                    holder.itemView.attr3.setBackgroundColor(Color.parseColor(obj.getString("value")))
                }

            }
        }

          holder.itemView.productName.text = data[position].product_name.toString()
          holder.itemView.qty.text = data[position].quantity.toString()
          holder.itemView.country.text = data[position].country
          holder.itemView.totalPrice.text = "AED "+ data[position].price
          holder.itemView.address.text = data[position].address
          Glide.with(context).load(data[position].files).placeholder(R.drawable.default_icon)
                  .into(holder.itemView.img)

          holder.itemView.setOnClickListener {
              clickInstance.clickPostion(position)
          }

    }

    override fun getItemCount(): Int {
        return  data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}