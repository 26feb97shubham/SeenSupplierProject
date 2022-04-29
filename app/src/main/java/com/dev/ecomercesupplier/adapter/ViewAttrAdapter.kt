package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Attributes
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_view_attr.view.*
import org.json.JSONArray

class ViewAttrAdapter(private val context: Context, private val attributeArray:JSONArray, private val count:Int, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<ViewAttrAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_view_attr, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (LogUtils.my_reference.equals("view")){
            holder.itemView.imgDelete.visibility = View.GONE
        }else{
            holder.itemView.imgDelete.visibility = View.VISIBLE
        }


        if(count != 3){
            holder.itemView.txtThird.visibility=View.GONE
            holder.itemView.txtThirdValue.visibility=View.GONE
        }
        else if(count != 2){
            holder.itemView.txtThird.visibility=View.GONE
            holder.itemView.txtThirdValue.visibility=View.GONE
            holder.itemView.txtSecondary.visibility=View.GONE
            holder.itemView.txtSecondaryValue.visibility=View.GONE
        }
        val obj= attributeArray.getJSONObject(position)
        holder.itemView.edtItemPrice.text="AED "+obj.getString("price")
        holder.itemView.edtItemQty.text=obj.getString("quantity")

        val data=obj.getJSONArray("data")
        for(i in 0 until data.length()){
            val obj1= data.getJSONObject(i)
            if(i==0) {
                if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                    if (obj1.getString("name_ar").equals("null")){
                        holder.itemView.txtPrimary.text = obj1.getString("name")
                    }else{
                        holder.itemView.txtPrimary.text = obj1.getString("name_ar")
                    }
                }else{
                    holder.itemView.txtPrimary.text = obj1.getString("name")
                }
                if(obj1.getString("type")=="1"){
                    holder.itemView.txtPrimaryValue.text = obj1.getString("value")
                }
                else{
                    holder.itemView.txtPrimaryValue.setBackgroundColor(Color.parseColor(obj1.getString("value")))
                }

            }
            else if(i==1) {
//                holder.itemView.txtSecondary.text = obj1.getString("name")
                if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                    if (obj1.getString("name_ar").equals("null")){
                        holder.itemView.txtSecondary.text = obj1.getString("name")
                    }else{
                        holder.itemView.txtSecondary.text = obj1.getString("name_ar")
                    }
                }else{
                    holder.itemView.txtSecondary.text = obj1.getString("name")
                }
                if(obj1.getString("type")=="1"){
                    holder.itemView.txtSecondaryValue.text = obj1.getString("value")
                }
                else{
                    holder.itemView.txtSecondaryValue.setBackgroundColor(Color.parseColor(obj1.getString("value")))
                }

            }
            else if(i==2) {
//                holder.itemView.txtThird.text = obj1.getString("name")
                if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                    if (obj1.getString("name_ar").equals("null")){
                        holder.itemView.txtThird.text = obj1.getString("name")
                    }else{
                        holder.itemView.txtThird.text = obj1.getString("name_ar")
                    }
                }else{
                    holder.itemView.txtThird.text = obj1.getString("name")
                }
                if(obj1.getString("type")=="1"){
                    holder.itemView.txtThirdValue.text = obj1.getString("value")
                }
                else{
                    holder.itemView.txtThirdValue.setBackgroundColor(Color.parseColor(obj1.getString("value")))
                }

            }
        }

        holder.itemView.imgDelete.setOnClickListener {
            holder.itemView.imgDelete.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostion(position)
        }


    }

    override fun getItemCount(): Int {
        return attributeArray.length()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}