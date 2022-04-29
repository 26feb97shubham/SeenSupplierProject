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
    lateinit var value:JSONArray
}