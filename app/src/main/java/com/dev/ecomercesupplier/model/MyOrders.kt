package com.dev.ecomercesupplier.model

import org.json.JSONArray
import org.json.JSONObject

class MyOrders {
    var id:Int=0
    var accept_reject:Int=0
    var status:Int=0
    lateinit var order_id:String
    lateinit var subtotal:String
    lateinit var user_name:String
    lateinit var user_image:String
    lateinit var phone_number:String
    lateinit var shipping_fee:String
    lateinit var taxes:String
    lateinit var total_discount:String
    lateinit var coupon_name:String
    lateinit var total_price:String
    lateinit var delivery_date:String
    lateinit var order_data:JSONArray
}