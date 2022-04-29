package com.dev.ecomercesupplier.model

import org.json.JSONArray

class CategoryName {
    var id:Int=0
    var name:String =""
    var name_ar:String =""
    var catName:String =""
    var catNameAr:String =""
    var address:String =""
    var rating:Double =0.0
    var isLike:Int=0
    var image:String=""
    var checkCategoryStatus:Boolean=false
    var isCategorySelected:Boolean=false
    lateinit var attributes:JSONArray
}