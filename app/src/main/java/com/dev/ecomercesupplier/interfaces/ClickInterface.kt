package com.dev.ecomercesupplier.interfaces

import android.view.View
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
        fun clickArray(idArray:JSONArray)
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