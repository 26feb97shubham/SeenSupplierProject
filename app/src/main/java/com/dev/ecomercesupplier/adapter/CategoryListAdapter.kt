package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility.Companion.direction
import com.dev.ecomercesupplier.custom.Utility.Companion.signUpcatIDList
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.CategoryName
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_category_list.view.*
import org.json.JSONArray

class CategoryListAdapter(private val context: Context, private val data:ArrayList<CategoryName>, private val clickInst: ClickInterface.ClickArrayInterface): RecyclerView.Adapter<CategoryListAdapter.MyViewHolder>() {
    var catIDList=JSONArray()
    var catNameList=JSONArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_category_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val name =  if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang,"").equals("ar")){
            data[position].catNameAr
        }else{
            data[position].name
        }
        holder.itemView.name.text =name


        if (direction.equals("Register", false)){
            if(data[position].name.equals("Sport", false) ||
                data[position].name.equals("Travel", false) ||
                data[position].name.equals("Yoga", false)){
                holder.itemView.img.isSelected=true
                catIDList.put(data[position].id)
                signUpcatIDList = catIDList
            }else{
                holder.itemView.img.isSelected=false
            }
        }else{
            if(data[position].checkCategoryStatus){
                for(i in 0 until catIDList.length()){
                    if(catIDList[i]==data[position].id){
                        catIDList.remove(i)
                        break
                    }
                }
                holder.itemView.img.isSelected=true
                catIDList.put(data[position].id)
                signUpcatIDList = catIDList
            }
            else{
                holder.itemView.img.isSelected=false
            }
        }




        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, 0.5f))
            if(holder.itemView.img.isSelected){
                holder.itemView.img.isSelected=false
                for(i in 0 until catIDList.length()){
                    if(catIDList[i]==data[position].id){
                        catIDList.remove(i)
                        signUpcatIDList = catIDList
                        clickInst.clickArray(signUpcatIDList, catNameList)
                        break
                    }
                }
            }
            else{
                holder.itemView.img.isSelected=true
                catIDList.put(data[position].id)
                if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                    catNameList.put(data[position].name_ar)
                }else{
                    catNameList.put(data[position].name)
                }
                signUpcatIDList = catIDList
                clickInst.clickArray(signUpcatIDList, catNameList)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}