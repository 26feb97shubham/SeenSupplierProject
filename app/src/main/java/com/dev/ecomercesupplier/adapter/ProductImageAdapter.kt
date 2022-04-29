package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.dev.ecomercesupplier.R
import kotlinx.android.synthetic.main.product_view_pager.view.*

class ProductImageAdapter(val context: Context, val productFiles: ArrayList<String>) : RecyclerView.Adapter<ProductImageAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.product_view_pager, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val requestOptions: RequestOptions =
            RequestOptions().error(R.drawable.default_img).centerCrop()

        Glide.with(context)
            .load(productFiles.get(position))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.productImageProgressbar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.productImageProgressbar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions).into(holder.itemView.img)
    }

    override fun getItemCount(): Int {
        return productFiles.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}