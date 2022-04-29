package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.model.ModelForSpinner
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility

class CustomDropdownCountryNameAdapter(private val context: Context, private val countryList : ArrayList<String>) :
    BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return countryList.size
    }

    override fun getItem(p0: Int): Any {
       return countryList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.custom_spinner_item_countries, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }

        vh.label.text = countryList.get(position)

        return view
    }

    private class ItemHolder(row: View?) {
        val label: TextView

        init {
            label = row?.findViewById(R.id.tvCountryName) as TextView
        }
    }
}