package com.dev.ecomercesupplier.interfaces

import android.view.View
import com.dev.ecomercesupplier.model.CatListModel
import org.json.JSONArray
import org.json.JSONObject

interface ClickInterface {
    interface ClickPosInterface{
        fun clickPostion(pos:Int)
    }
    interface ClickPosTypeInterface{
        fun clickPostionType(pos:Int, type:String)
    }
    interface ClickArrayInterface{
        fun clickArray(idArray:JSONArray, nameArray:JSONArray)
    }

    interface ClickArrayInterface1{
        fun clickArray(catList : ArrayList<CatListModel>)
    }
    interface ClickJSonObjInterface{
        fun clickJSonObj(obj:JSONObject)
    }
    interface ClickPosItemViewInterface{
        fun clickPosItemView(pos:Int, itemView: View)
    }
    interface OnRecyclerItemClick{
        fun OnClickAction(position: Int)
    }
}