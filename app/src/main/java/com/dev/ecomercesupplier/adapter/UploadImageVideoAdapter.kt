package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import kotlinx.android.synthetic.main.item_uploaded_img_vdo.view.*


class UploadImageVideoAdapter(private val context: Context, private val data: ArrayList<String>, private val clickInstance: ClickInterface.ClickPosItemViewInterface): RecyclerView.Adapter<UploadImageVideoAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_uploaded_img_vdo, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(data[position]).placeholder(R.drawable.user).into(holder.itemView.img)
        if(data[position].contains(".mp4")){
            holder.itemView.imgPlay.visibility= View.VISIBLE
        }
        else{
            holder.itemView.imgPlay.visibility= View.GONE
        }
        /*holder.itemView.imgRemove.setOnClickListener {
            holder.itemView.imgRemove.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "Remove")
        }*/

        clickInstance.clickPosItemView(position, holder.itemView)


     /*   holder.itemView.imgRemove.setOnClickListener {
            holder.itemView.imgRemove.startAnimation(AlphaAnimation(1f, 0.5f))
            clickInstance.clickPostion(position)

        }*/



    }



    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}