package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Galleries
import kotlinx.android.synthetic.main.item_small_gallery.view.*


class GalleryListAdapter(private val context: Context, private val data: ArrayList<Galleries>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<GalleryListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_small_gallery, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.itemView.layoutParams = RecyclerView.LayoutParams(1, 2)
        if(data[position].files!=data[position].thumbnail){
            holder.itemView.imgPlay.visibility=View.VISIBLE
        }
        else{
            holder.itemView.imgPlay.visibility=View.GONE
        }
        Glide.with(context).load(data[position].thumbnail).centerCrop().placeholder(R.drawable.default_icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.itemView.smallView)

        /*val requestOption: RequestOptions = RequestOptions().placeholder(R.drawable.default_icon).error(R.drawable.default_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)


        Glide.with(context).load(thumbnail)
                .apply(requestOption)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(object : SimpleTarget<Drawable?>() {
                    fun onResourceReady(resource: Drawable?, transition: Transition<in Drawable?>?) {
                        mHolder.assets_image.setImageDrawable(resource)
                    }

                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                        mHolder.assets_image.setImageDrawable(placeholder)
                    }

                    override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
                        mHolder.assets_image.setImageDrawable(errorDrawable)
                    }

                    override fun onLoadStarted(@Nullable placeholder: Drawable?) {
                        mHolder.assets_image.setImageDrawable(placeholder)
                    }
                })*/
        holder.itemView.setOnClickListener {
            clickInstance.clickPostion(position)
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}