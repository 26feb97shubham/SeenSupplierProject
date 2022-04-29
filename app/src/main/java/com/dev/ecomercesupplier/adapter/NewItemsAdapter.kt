package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.model.Products
import com.dev.ecomercesupplier.utils.LogUtils.Companion.my_reference
import kotlinx.android.synthetic.main.layout_new_items.view.*
import org.json.JSONArray
import org.json.JSONObject

class NewItemsAdapter(
    private val context: Context,
    private val newItemsList: ArrayList<Products>,
    private val products: JSONArray,
    private val findNavController: NavController,
    private val responseBody: String
) : RecyclerView.Adapter<NewItemsAdapter.NewItemsAdapterVH>() {
    inner class NewItemsAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewItemsAdapterVH {
        val view= LayoutInflater.from(context).inflate(R.layout.layout_new_items, parent, false)
        return NewItemsAdapterVH(view)
    }

    override fun onBindViewHolder(holder: NewItemsAdapterVH, position: Int) {
        val newItem = newItemsList[position]
        val productImage = if (!newItem.files.equals("")){
            newItem.files.toString()
        }else{
            context.getDrawable(R.drawable.default_img).toString()
        }

        val requestOptions: RequestOptions =
            RequestOptions().error(R.drawable.default_img).centerCrop()

        Glide.with(context)
            .load(productImage)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.newItemsProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.newItemsProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions).into(holder.itemView.siv_product_image)


        holder.itemView.mtv_product_name.text = newItem.name
        holder.itemView.mtv_product_price.text = "AED "+newItem.price

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            val response_body = JSONObject()
            response_body.put("response", 1)
            response_body.put("message", "Data found.")
            response_body.put("products", products[position])
            bundle.putString("responseBody", response_body.toString())
        /*    bundle.putInt("pos", position)
            bundle.putString("responseBody", responseBody)*/
            my_reference = "view"
            findNavController.navigate(R.id.viewItemFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
       return newItemsList.size
    }
}