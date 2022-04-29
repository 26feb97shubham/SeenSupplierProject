package com.dev.ecomercesupplier.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.custom.Utility.Companion.isSelectAll
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.CatListModel
import com.dev.ecomercesupplier.model.CategoryName
import com.dev.ecomercesupplier.rest.ApiUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_revenue_category.view.*
import org.json.JSONArray

class CategoryRevenueAdapter(
    private val context: Context,
    private val data:ArrayList<CategoryName>,
    private val clickInst: ClickInterface.ClickArrayInterface1
) : RecyclerView.Adapter<CategoryRevenueAdapter.CategoryRevenueAdapterVH>() {
    var catIDList=JSONArray()
    var catNameList = JSONArray()
    var catList = ArrayList<CatListModel>()
    var isSelected = false
    inner class CategoryRevenueAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRevenueAdapterVH {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_revenue_category, parent, false)
        return CategoryRevenueAdapterVH(view)
    }

    override fun onBindViewHolder(holder: CategoryRevenueAdapterVH, @SuppressLint("RecyclerView") position: Int) {
        var name = if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
            data[position].catNameAr
        }else{
            data[position].name
        }
        holder.itemView.mtv_title.text = name

        holder.itemView.mtv_title.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if (data[position].isCategorySelected){
                    data[position].isCategorySelected = false
                    holder.itemView.shapeable_tick.visibility = View.GONE
                    ApiUtils.isCatClicked=-1
                    for (i in 0 until catList.size){
                        if (catList[i].isCatSelected){
                            catList.removeAt(i)
                            break
                        }
                    }
                }else{
                    data[position].isCategorySelected = true
                    ApiUtils.isCatClicked=+1
                    holder.itemView.shapeable_tick.visibility = View.VISIBLE
                    catIDList.put(data[position].id)
                    var catListModel = CatListModel()
                    catListModel.catId = data[position].id
                    catListModel.catName = data[position].name
                    catListModel.catNameAr = data[position].catNameAr
                    catListModel.isCatSelected = true
                    catList.add(catListModel)
                }

                Utility.catListSaved = catList
            }

        })

        if (isSelectAll){
            holder.itemView.shapeable_tick.visibility = View.VISIBLE
            catList.clear()
            for (i in 0 until data.size){
                var catListModel = CatListModel()
                catListModel.catId = data[i].id
                catListModel.catName = data[i].name
                catListModel.catNameAr = data[i].catNameAr
                catListModel.isCatSelected = true
                catList.add(catListModel)
            }

            Utility.catListSaved = catList

            clickInst.clickArray(catList)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}