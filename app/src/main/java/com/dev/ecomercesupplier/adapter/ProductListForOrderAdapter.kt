package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.OrderData
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_current_data.view.*
import kotlinx.android.synthetic.main.item_product_for_order.view.*
import kotlinx.android.synthetic.main.item_product_for_order.view.address
import kotlinx.android.synthetic.main.item_product_for_order.view.attr1
import kotlinx.android.synthetic.main.item_product_for_order.view.attr1Txt
import kotlinx.android.synthetic.main.item_product_for_order.view.attr2
import kotlinx.android.synthetic.main.item_product_for_order.view.attr2Txt
import kotlinx.android.synthetic.main.item_product_for_order.view.attr3
import kotlinx.android.synthetic.main.item_product_for_order.view.attr3Txt
import kotlinx.android.synthetic.main.item_product_for_order.view.country
import kotlinx.android.synthetic.main.item_product_for_order.view.qty

class ProductListForOrderAdapter(private val context: Context, private val data: ArrayList<OrderData>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<ProductListForOrderAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_product_for_order, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        for(i in 0 until data[position].attributes.length()){
            val obj=data[position].attributes.getJSONObject(i)
            if(i==0){
                if(obj.getString("type")=="1"){
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                        holder.itemView.attr1Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr1Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr1.text=obj.getString("value")
                }
                else{
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                        holder.itemView.attr1Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr1Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr1.setBackgroundColor(Color.parseColor(obj.getString("value")))
                }

            }
            else if(i==1){
                holder.itemView.attr2Txt.visibility=View.VISIBLE
                holder.itemView.attr2.visibility=View.VISIBLE
                if(obj.getString("type")=="1"){
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                        holder.itemView.attr1Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr1Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr2.text=obj.getString("value")
                }
                else{
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                        holder.itemView.attr1Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr1Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr2.setBackgroundColor(Color.parseColor(obj.getString("value")))
                }

            }
            else if(i==2){
                holder.itemView.attr3Txt.visibility=View.VISIBLE
                holder.itemView.attr3.visibility=View.VISIBLE
                if(obj.getString("type")=="1"){
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                        holder.itemView.attr1Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr1Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr3.text=obj.getString("value")
                }
                else{
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                        holder.itemView.attr1Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr1Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr3.setBackgroundColor(Color.parseColor(obj.getString("value")))
                }

            }
        }

        holder.itemView.orderName.text = data[position].product_name
        holder.itemView.orderDesc.text=data[position].product_description
        holder.itemView.qty.text = data[position].quantity.toString()
        holder.itemView.country.text = data[position].country
        holder.itemView.price.text = "AED"+" "+ data[position].price
        holder.itemView.address.text = data[position].address



    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}