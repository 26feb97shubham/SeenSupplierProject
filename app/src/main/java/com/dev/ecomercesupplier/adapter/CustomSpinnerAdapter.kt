package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.model.ModelForSpinner
import java.util.ArrayList

class CustomSpinnerAdapter(private val context: Context?, private val items: ArrayList<ModelForSpinner>) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size //returns total of items in the list
    }

    override fun getItem(position: Int): Any? {
        return items.get(position) //returns list item at the specified position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
            /* if (layoutSelector == 0) {
                 convertView = LayoutInflater.from(context).inflate(R.layout.subject_list_spinner_text_layout, parent, false)
             } else if (layoutSelector == 1) {
                 convertView = LayoutInflater.from(context).inflate(R.layout.add_subject_spinner_text_layout, parent, false)
             }*/
        }
        if (items.size > 0) {
            val userText = convertView!!.findViewById<TextView>(R.id.txt_spinner)
            if (position == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    userText.setTextColor(context!!.getColor(R.color.gray))
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    userText.setTextColor(context!!.getColor(R.color.black))
                }
            }
            userText.text = items.get(position).name
        }
        return convertView
    }


}
