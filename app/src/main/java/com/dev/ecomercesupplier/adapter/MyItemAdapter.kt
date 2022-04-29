package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
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
import com.dev.ecomercesupplier.model.Products
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_my_item.view.*

class MyItemAdapter(private val context: Context, private val data: ArrayList<Products>, private val clickInstance: ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<MyItemAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_my_item, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
                    holder.itemView.myItemProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.myItemProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions).into(holder.itemView.img)
        holder.itemView.name.text=data[position].name
//        holder.itemView.category.text=data[position].category
        holder.itemView.price.text=context.getString(R.string.aed)+" "+data[position].price
        if(data[position].quantity.equals("0")){
            if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                holder.itemView.stockCount.text = " "+context.getString(R.string.out_of_stock)
            }else{
                holder.itemView.stockCount.text = " "+context.getString(R.string.out_of_stock)
            }
        }else{
            holder.itemView.stockCount.text=" "+data[position].quantity+" "+context.getString(R.string.pcs) + " "+context.getString(R.string.in_stock)
        }


        holder.itemView.setOnClickListener {
            Log.e("data", data[position].toString())

            holder.itemView.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "1")
        }

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