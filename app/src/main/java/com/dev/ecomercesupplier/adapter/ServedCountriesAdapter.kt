package com.dev.ecomercesupplier.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.ServeCountries
import kotlinx.android.synthetic.main.item_served_countries.view.*
import org.json.JSONArray

class ServedCountriesAdapter(
    private val context: Context,
    private val data: ArrayList<ServeCountries>,
    private val is_back: Boolean,
    private val clickInstance: ClickInterface.ClickArrayInterface
): RecyclerView.Adapter<ServedCountriesAdapter.MyViewHolder>() {

    companion object{
        var servedCountries=JSONArray()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_served_countries, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (is_back){
            servedCountries = JSONArray()
            holder.itemView.chkCountry.isChecked = false
        }


        for(i in 0 until servedCountries.length()){
            if(data[position].id.toString()==servedCountries[i]) {
                holder.itemView.chkCountry.isChecked = true
                break
            }
        }
        holder.itemView.chkCountry.text=data[position].country_name
        holder.itemView.chkCountry.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    servedCountries.put(data[position].id.toString())
                    clickInstance.clickArray(servedCountries)
                }
                else{
                    for(i in 0 until servedCountries.length()){
                        if(servedCountries[i]==data[position].id.toString()){
                            servedCountries.remove(i)
                            clickInstance.clickArray(servedCountries)
                            break
                        }
                    }
                }

            }
        })
    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}