package com.dev.ecomercesupplier.adapter

import android.content.Context
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
import com.dev.ecomercesupplier.model.Coupons
import kotlinx.android.synthetic.main.item_my_coupons.view.*

class MyCouponsAdapter(private val context: Context, private val data: ArrayList<Coupons>, private val clickInstance: ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<MyCouponsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_my_coupons, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val requestOptions: RequestOptions =
            RequestOptions().error(R.drawable.default_img).centerCrop()

        Glide.with(context)
            .load(data[position].picture)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.couponsProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.couponsProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions).into(holder.itemView.img)
        holder.itemView.title.text=data[position].title
        holder.itemView.discount.text=data[position].percentage +"% " + context.getString(R.string.discount)
        holder.itemView.fromTo.text=context.getString(R.string.from)+" "+data[position].from_date+ " - "+data[position].to_date
        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "3")
        }
        holder.itemView.editCoupon.setOnClickListener {
            holder.itemView.editCoupon.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "2")
        }
        holder.itemView.deleteCoupon.setOnClickListener {
            holder.itemView.deleteCoupon.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "4")
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}