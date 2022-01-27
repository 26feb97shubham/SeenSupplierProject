package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility.Companion.isSelectAll
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.CategoryName
import kotlinx.android.synthetic.main.item_revenue_category.view.*
import org.json.JSONArray

class CategoryRevenueAdapter(
    private val context: Context,
    private val data:ArrayList<CategoryName>,
    private val clickInst: ClickInterface.ClickArrayInterface
) : RecyclerView.Adapter<CategoryRevenueAdapter.CategoryRevenueAdapterVH>() {
    var catIDList=JSONArray()
    var isSelected = false
    inner class CategoryRevenueAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRevenueAdapterVH {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_revenue_category, parent, false)
        return CategoryRevenueAdapterVH(view)
    }

    override fun onBindViewHolder(holder: CategoryRevenueAdapterVH, position: Int) {
        holder.itemView.mtv_title.text = data[position].name
        holder.itemView.mtv_title.setOnClickListener {
            holder.itemView.mtv_title.startAnimation(AlphaAnimation(1f, 0.5f))
            if (isSelected){
                isSelected = false
                holder.itemView.shapeable_tick.visibility = View.GONE
                for(i in 0 until catIDList.length()){
                    if(catIDList[i]==data[position].id){
                        catIDList.remove(i)
                        clickInst.clickArray(catIDList)
                        break
                    }
                }
            }else{
                isSelected = true
                holder.itemView.shapeable_tick.visibility = View.VISIBLE
                catIDList.put(data[position].id)
                clickInst.clickArray(catIDList)
            }
        }

        if (isSelectAll){
            holder.itemView.shapeable_tick.visibility = View.VISIBLE
            catIDList.put(data[position].id)
            clickInst.clickArray(catIDList)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}