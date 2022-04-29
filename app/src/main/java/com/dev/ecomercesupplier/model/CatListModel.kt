package com.dev.ecomercesupplier.model

data class CatListModel(
    var catId : Int?= null,
    var catName : String?= null,
    var catNameAr : String?= null,
    var isCatSelected: Boolean = false
)
