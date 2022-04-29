package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.OrderData
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_current_data.view.*
import kotlinx.android.synthetic.main.item_past_data.view.*
import kotlinx.android.synthetic.main.item_past_data.view.address
import kotlinx.android.synthetic.main.item_past_data.view.attr1
import kotlinx.android.synthetic.main.item_past_data.view.attr1Txt
import kotlinx.android.synthetic.main.item_past_data.view.attr2
import kotlinx.android.synthetic.main.item_past_data.view.attr2Txt
import kotlinx.android.synthetic.main.item_past_data.view.attr3
import kotlinx.android.synthetic.main.item_past_data.view.attr3Txt
import kotlinx.android.synthetic.main.item_past_data.view.country
import kotlinx.android.synthetic.main.item_past_data.view.img
import kotlinx.android.synthetic.main.item_past_data.view.qty
import kotlinx.android.synthetic.main.item_past_data.view.totalPrice

class PastDataAdapter(private val context: Context, private val data:ArrayList<OrderData>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<PastDataAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
                LayoutInflater.from(context).inflate(R.layout.item_past_data, parent, false)
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
                        holder.itemView.attr2Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr2Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr2.text=obj.getString("value")
                }
                else{
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                        holder.itemView.attr2Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr2Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr2.setBackgroundColor(Color.parseColor(obj.getString("value")))
                }

            }
            else if(i==2){
                holder.itemView.attr3Txt.visibility=View.VISIBLE
                holder.itemView.attr3.visibility=View.VISIBLE
                if(obj.getString("type")=="1"){
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                        holder.itemView.attr3Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr3Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr3.text=obj.getString("value")
                }
                else{
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                        holder.itemView.attr3Txt.text=obj.getString("name_ar")
                    }else{
                        holder.itemView.attr3Txt.text=obj.getString("name")
                    }
                    holder.itemView.attr3.setBackgroundColor(Color.parseColor(obj.getString("value")))
                }

            }
        }

        holder.itemView.qty.text = data[position].quantity.toString()
        holder.itemView.country.text = data[position].country
        holder.itemView.totalPrice.text = "AED "+ data[position].price
        holder.itemView.address.text = data[position].address

        val requestOptions: RequestOptions =
            RequestOptions().error(R.drawable.default_img).centerCrop()

        Glide.with(context)
            .load(data[position].files)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.pastDataProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.pastDataProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions).into(holder.itemView.img)

        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, 0.5f))
            clickInstance.clickPostion(position)
        }

    }

    override fun getItemCount(): Int {
        return  data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}