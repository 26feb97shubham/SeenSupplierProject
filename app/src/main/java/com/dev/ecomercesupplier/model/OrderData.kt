package com.dev.ecomercesupplier.model

import org.json.JSONArray

class OrderData {
    var id:Int=0
    var quantity:Int=0
    lateinit var country:String
    lateinit var address:String
    lateinit var price:String
    lateinit var product_name:String
    lateinit var files:String
    lateinit var product_description:String
    lateinit var attributes: JSONArray
}