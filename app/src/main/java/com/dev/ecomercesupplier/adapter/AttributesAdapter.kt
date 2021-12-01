package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Attributes
import kotlinx.android.synthetic.main.item_attributes.view.*
import org.json.JSONArray
import org.json.JSONObject


class AttributesAdapter(private val context: Context, private val data: ArrayList<Attributes>, private val clickInstance: ClickInterface.ClickJSonObjInterface): RecyclerView.Adapter<AttributesAdapter.MyViewHolder>() {
    var secondaryList=ArrayList<String>()
    var primaryList=ArrayList<String>()
    var thirdList=ArrayList<String>()
    var primaryAttr:String=""
    var secondAttr:String=""
    var thirdAttr:String=""

    companion object{
        lateinit var attrData:JSONArray
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_attributes, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.name.text=data[position].name

        if(position==0) {
            if (data[position].type.equals("2", false)) {
                primaryList.clear()
                for (k in 0 until data[position].value.length()) {
                    primaryList.add(data[position].value[k].toString())
                }
                val colorsAdapter = ColorsAdapter(context, primaryList, position+1, object : ClickInterface.ClickPosInterface {
                    override fun clickPostion(pos: Int) {
                        primaryAttr = primaryList[pos]
                        val obj = JSONObject()
                        /*obj.put(data[0].id.toString(), primaryAttr)
                        if(data.size>=2){
                            obj.put(data[1].id.toString(), secondAttr)
                        }
                        if(data.size>=3){
                            obj.put(data[2].id.toString(), thirdAttr)
                        }*/
                        /*for( m in 0 until  data.size){
                             val obj1=JSONObject()
                             obj1.put("id", data[m].id)
                             obj1.put("name", data[m].name)
                             if(m==0) {
                                 obj1.put("value", primaryAttr)
                             }
                             else if(m==1) {
                                 obj1.put("value", secondAttr)
                             }
                             else if(m==1) {
                                 obj1.put("value", thirdAttr)
                             }
                             attrData.put(obj1)
                         }*/
                        var itemAdd=false
                        if(attrData.length()!=0){
                            for(k in 0 until attrData.length()){
                                val obj=attrData.getJSONObject(k)
                                if(obj.length()!=0){
                                    if(data[position].id==obj.getInt("id")){
                                        val obj1=JSONObject()
                                        obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                                        obj1.put("value", primaryAttr)
                                        attrData.put(position, obj1)
                                        itemAdd=true
                                        break
                                    }
                                }
                            }
                        }
                        if(!itemAdd){
                            val obj1=JSONObject()
                             obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                            obj1.put("value", primaryAttr)
                            attrData.put(obj1)
                        }
                       /* if (attrData.length() == data.size) {
                            attrData= JSONArray()
                        }

                        val obj1 = JSONObject()
                         obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                        obj1.put("value", primaryAttr)
                        attrData.put(obj1)*/

                        obj.put("data", attrData)
                        clickInstance.clickJSonObj(obj)
                    }

                })
                holder.itemView.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                holder.itemView.rvList.adapter = colorsAdapter

            } else {
                primaryList.clear()
                for (k in 0 until data[position].value.length()) {
                    primaryList.add(data[position].value[k].toString())
                }
                val textsAdapter = TextsAdapter(context, primaryList, position+1,object : ClickInterface.ClickPosInterface {
                    override fun clickPostion(pos: Int) {
                        primaryAttr = primaryList[pos]
                        val obj = JSONObject()
                        /*obj.put(data[0].id.toString(), primaryAttr)
                        if(data.size>=2){
                            obj.put(data[1].id.toString(), secondAttr)
                        }
                        if(data.size>=3){
                            obj.put(data[2].id.toString(), thirdAttr)
                        }*/
                        /*for( m in 0 until  data.size){
                            val obj1=JSONObject()
                            obj1.put("id", data[m].id)
                            obj1.put("name", data[m].name)
                            if(m==0) {
                                obj1.put("value", primaryAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", secondAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", thirdAttr)
                            }
                            attrData.put(obj1)
                        }*/
                        var itemAdd=false
                       if(attrData.length()!=0){
                            for(k in 0 until attrData.length()){
                                val obj=attrData.getJSONObject(k)
                                if(obj.length()!=0){
                                    if(data[position].id==obj.getInt("id")){
                                        /*attrData.remove(position)*/
                                        val obj1=JSONObject()
                                         obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                                        obj1.put("value", primaryAttr)
                                        attrData.put(position, obj1)
                                        itemAdd=true
                                        break
                                    }
                                }
                            }
                        }
                       if(!itemAdd){
                           val obj1=JSONObject()
                            obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                           obj1.put("value", primaryAttr)
                           attrData.put(obj1)
                       }
                        /*if (attrData.length() == data.size) {
                            attrData= JSONArray()
                        }
                        val obj1=JSONObject()
                          obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                        obj1.put("value", primaryAttr)
                        attrData.put(obj1)*/

                        obj.put("data", attrData)
                        clickInstance.clickJSonObj(obj)
                    }

                })
                holder.itemView.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                holder.itemView.rvList.adapter = textsAdapter
            }

        }
        else if(position==1) {
            if (data[position].type.equals("2", false)) {
                secondaryList.clear()
                for (k in 0 until data[position].value.length()) {
                    secondaryList.add(data[position].value[k].toString())
                }
                val colorsAdapter = ColorsAdapter(context, secondaryList, position+1, object : ClickInterface.ClickPosInterface {
                    override fun clickPostion(pos: Int) {
                        secondAttr=secondaryList[pos]
                        val obj=JSONObject()

                        /*obj.put(data[0].id.toString(), primaryAttr)
                        obj.put(data[1].id.toString(), secondAttr)
                        if(data.size>=3){
                            obj.put(data[2].id.toString(), thirdAttr)
                        }
                        clickInstance.clickJSonObj(obj)*/
                      /*  for( m in 0 until  data.size){
                            val obj1=JSONObject()
                            obj1.put("id", data[m].id)
                            obj1.put("name", data[m].name)

                            if(m==0) {
                                obj1.put("value", primaryAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", secondAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", thirdAttr)
                            }
                            attrData.put(obj1)
                        }*/
                        var itemAdd=false
                       if(attrData.length()!=0){
                            for(k in 0 until attrData.length()){
                                val obj=attrData.getJSONObject(k)
                                if(obj.length()!=0){
                                    if(data[position].id==obj.getInt("id")){
                                        val obj1=JSONObject()
                                         obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                                        obj1.put("value", secondAttr)
                                        attrData.put(position, obj1)
                                        itemAdd=true
                                        break
                                    }
                                }
                            }
                        }
                       if(!itemAdd){
                           val obj1=JSONObject()
                            obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                           obj1.put("value", secondAttr)
                           attrData.put(obj1)
                       }
                       /* if (attrData.length() == data.size) {
                            attrData= JSONArray()
                        }
                        val obj1=JSONObject()
                          obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                        obj1.put("value", secondAttr)
                        attrData.put(obj1)*/

                        obj.put("data", attrData)
                        clickInstance.clickJSonObj(obj)
                    }

                })
                holder.itemView.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                holder.itemView.rvList.adapter = colorsAdapter

            } else {
                secondaryList.clear()
                for (k in 0 until data[position].value.length()) {
                    secondaryList.add(data[position].value[k].toString())
                }
                val textsAdapter = TextsAdapter(context, secondaryList, position+1, object : ClickInterface.ClickPosInterface {
                    override fun clickPostion(pos: Int) {
                        secondAttr=secondaryList[pos]
                        val obj=JSONObject()
                        /*obj.put(data[0].id.toString(), primaryAttr)
                        obj.put(data[1].id.toString(), secondAttr)
                        if(data.size>=3){
                            obj.put(data[2].id.toString(), thirdAttr)
                        }
                        clickInstance.clickJSonObj(obj)*/
                        /*for( m in 0 until  data.size){
                            val obj1=JSONObject()
                            obj1.put("id", data[m].id)
                            obj1.put("name", data[m].name)
                            if(m==0) {
                                obj1.put("value", primaryAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", secondAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", thirdAttr)
                            }
                            attrData.put(obj1)
                        }*/
                        var itemAdd=false
                       if(attrData.length()!=0){
                            for(k in 0 until attrData.length()){
                                val obj=attrData.getJSONObject(k)
                                if(obj.length()!=0){
                                    if(data[position].id==obj.getInt("id")){
                                        val obj1=JSONObject()
                                         obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                                        obj1.put("value", secondAttr)
                                        attrData.put(position, obj1)
                                        itemAdd=true
                                        break
                                    }
                                }
                            }

                        }
                        if(!itemAdd){
                           val obj1=JSONObject()
                            obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                           obj1.put("value", secondAttr)
                           attrData.put(obj1)
                       }
                       /* if (attrData.length() == data.size) {
                            attrData= JSONArray()
                        }
                        val obj1=JSONObject()
                          obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                        obj1.put("value", secondAttr)
                        attrData.put(obj1)*/

                        obj.put("data", attrData)
                        clickInstance.clickJSonObj(obj)
                    }

                })
                holder.itemView.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                holder.itemView.rvList.adapter = textsAdapter
            }
        }
        else if(position==2) {
            if (data[position].type.equals("2", false)) {
                thirdList.clear()
                for (k in 0 until data[position].value.length()) {
                    thirdList.add(data[position].value[k].toString())
                }
                val colorsAdapter = ColorsAdapter(context, thirdList, position+1, object : ClickInterface.ClickPosInterface {
                    override fun clickPostion(pos: Int) {
                        thirdAttr=thirdList[pos]
                        val obj=JSONObject()
                        /*obj.put(data[0].id.toString(), primaryAttr)
                        obj.put(data[1].id.toString(), secondAttr)
                        obj.put(data[2].id.toString(), thirdAttr)
                        clickInstance.clickJSonObj(obj)*/
                      /*  for( m in 0 until  data.size){
                            val obj1=JSONObject()
                            obj1.put("id", data[m].id)
                            obj1.put("name", data[m].name)
                            if(m==0) {
                                obj1.put("value", primaryAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", secondAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", thirdAttr)
                            }
                            attrData.put(obj1)
                        }*/
                        var itemAdd=false
                       if(attrData.length()!=0){
                            for(k in 0 until attrData.length()){
                                val obj=attrData.getJSONObject(k)
                                if(obj.length()!=0){
                                    if(data[position].id==obj.getInt("id")){
                                        val obj1=JSONObject()
                                         obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                                        obj1.put("value", thirdAttr)
                                        attrData.put(position, obj1)
                                        itemAdd=true
                                        break
                                    }
                                }
                            }
                        }
                       if(!itemAdd){
                           val obj1=JSONObject()
                            obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                           obj1.put("value", thirdAttr)
                           attrData.put(obj1)
                       }
                      /*  if (attrData.length() == data.size) {
                            attrData= JSONArray()
                        }
                        val obj1=JSONObject()
                          obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                        obj1.put("value", thirdAttr)
                        attrData.put(obj1)*/

                        obj.put("data", attrData)
                        clickInstance.clickJSonObj(obj)
                    }

                })
                holder.itemView.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                holder.itemView.rvList.adapter = colorsAdapter

            } else {
                thirdList.clear()
                for (k in 0 until data[position].value.length()) {
                    thirdList.add(data[position].value[k].toString())
                }
                val textsAdapter = TextsAdapter(context, thirdList, position+1, object : ClickInterface.ClickPosInterface {
                    override fun clickPostion(pos: Int) {
                        thirdAttr=thirdList[pos]
                        val obj=JSONObject()
                      /*  obj.put(data[0].id.toString(), primaryAttr)
                        obj.put(data[1].id.toString(), secondAttr)
                        obj.put(data[2].id.toString(), thirdAttr)
                        clickInstance.clickJSonObj(obj)*/
                       /* for( m in 0 until  data.size){
                            val obj1=JSONObject()
                            obj1.put("id", data[m].id)
                            obj1.put("name", data[m].name)
                            if(m==0) {
                                obj1.put("value", primaryAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", secondAttr)
                            }
                            else if(m==1) {
                                obj1.put("value", thirdAttr)
                            }
                            attrData.put(obj1)
                        }*/
                        var itemAdd=false
                       if(attrData.length()!=0){
                            for(k in 0 until attrData.length()){
                                val obj=attrData.getJSONObject(k)
                                if(obj.length()!=0){
                                    if(data[position].id==obj.getInt("id")){
                                        val obj1=JSONObject()
                                         obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                                        obj1.put("value", thirdAttr)
                                        attrData.put(position, obj1)
                                        itemAdd=true
                                        break
                                    }
                                }
                            }
                        }
                       if(!itemAdd){
                           val obj1=JSONObject()
                            obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                           obj1.put("value", thirdAttr)
                           attrData.put(obj1)
                       }
                       /* if (attrData.length() == data.size) {
                            attrData= JSONArray()
                        }
                        val obj1=JSONObject()
                          obj1.put("id", data[position].id)
                                        obj1.put("name", data[position].name)
                                        obj1.put("type", data[position].type)
                        obj1.put("value", thirdAttr)
                        attrData.put(obj1)*/

                        obj.put("data", attrData)
                        clickInstance.clickJSonObj(obj)
                    }

                })
                holder.itemView.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                holder.itemView.rvList.adapter = textsAdapter
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}