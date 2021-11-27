package com.dev.ecomercesupplier.custom

import android.content.Context
import android.content.res.Configuration
import java.util.*

class Utility {
    companion object{
        fun changeLanguage(context:Context, language:String){
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}