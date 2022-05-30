package com.dev.ecomercesupplier.model

import com.google.gson.annotations.SerializedName

data class BookingResultResponse (

    @SerializedName("BookingResponse" ) var BookingResponse : BookingResponse? = BookingResponse()

)

data class BookingResponse (

    @SerializedName("AWBNumber"           ) var AWBNumber           : String?        = null,
    @SerializedName("Status"              ) var Status              : String?        = null,
    @SerializedName("Description"         ) var Description         : String?        = null,
    @SerializedName("AWBLabelURL"         ) var AWBLabelURL         : String?        = null,
    @SerializedName("AWBLabel"            ) var AWBLabel            : String?        = null,
    @SerializedName("ShipperContactName"  ) var ShipperContactName  : String?        = null,
    @SerializedName("ShipperCompanyName"  ) var ShipperCompanyName  : String?        = null,
    @SerializedName("ShipperCity"         ) var ShipperCity         : String?        = null,
    @SerializedName("ShipperCountry"      ) var ShipperCountry      : String?        = null,
    @SerializedName("ReceiverContactName" ) var ReceiverContactName : String?        = null,
    @SerializedName("ReceiverCompanyName" ) var ReceiverCompanyName : String?        = null,
    @SerializedName("ReceiverCity"        ) var ReceiverCity        : String?        = null,
    @SerializedName("ReceiverCountry"     ) var ReceiverCountry     : String?        = null,
    @SerializedName("ProductGroup"        ) var ProductGroup        : String?        = null,
    @SerializedName("PaymentType"         ) var PaymentType         : String?        = null,
    @SerializedName("CODAmount"           ) var CODAmount           : String?        = null,
    @SerializedName("CODCurrency"         ) var CODCurrency         : String?        = null,
    @SerializedName("ReferenceNo"         ) var ReferenceNo         : String?        = null,
    @SerializedName("serviceResult"       ) var serviceResult       : ServiceResult? = ServiceResult()

)

data class ServiceResult (

    @SerializedName("success"  ) var success  : Boolean? = null,
    @SerializedName("ErrorMsg" ) var ErrorMsg : String?  = null

)
