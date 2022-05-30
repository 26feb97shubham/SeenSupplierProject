package com.dev.ecomercesupplier.model

import org.json.JSONArray

class Attributes {
    var id:Int=0
    var type:String=""
    var name:String=""
    var name_ar:String=""
    var price:String=""
    var quantity:String=""
    var sold_out:String=""
    var length:String=""
    var width:String=""
    var height:String=""
    var weight:String=""
    lateinit var value:JSONArray
}