package com.dev.ecomercesupplier.model

import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class MyOrders : Serializable {
    var id:Int=0
    var user_id:Int=0
    var supplier_id:Int=0
    var accept_reject:Int=0
    var status:Int=0
    lateinit var order_id:String
    lateinit var subtotal:String
    lateinit var user_name:String
    lateinit var user_image:String
    lateinit var phone_number:String
    lateinit var shipping_fee:String
    lateinit var taxes:String
    lateinit var AWBNumber:String
    lateinit var city:String
    lateinit var total_discount:String
    lateinit var coupon_name:String
    lateinit var total_price:String
    lateinit var delivery_date:String
    lateinit var supplierName:String
    lateinit var supplierMobileNumber:String
    lateinit var supplierCity:String
    lateinit var supplierEmail:String
    lateinit var productLength:String
    lateinit var productWidth:String
    lateinit var productHeight:String
    lateinit var productWeight:String
    lateinit var receiverLat:String
    lateinit var receiverLong:String
    lateinit var receiveremail:String
    lateinit var orderBookingDate:String
    lateinit var order_data:JSONArray
}