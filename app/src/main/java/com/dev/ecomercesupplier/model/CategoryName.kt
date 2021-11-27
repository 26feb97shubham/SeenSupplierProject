package com.dev.ecomercesupplier.model

import org.json.JSONArray

class CategoryName {
    var id:Int=0
    var name:String =""
    var catName:String =""
    var address:String =""
    var rating:Double =0.0
    var isLike:Int=0
    var image:String=""
    var checkCategoryStatus:Boolean=false
    lateinit var attributes:JSONArray
}