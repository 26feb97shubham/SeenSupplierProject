package com.dev.ecomercesupplier.model

import com.google.gson.annotations.SerializedName

data class MyJsonDataModel(
    var BookingRequest: MyBookingRequest? = MyBookingRequest()
) {
    data class MyBookingRequest(

        @SerializedName("SenderContactName") var SenderContactName: String? = null,
        @SerializedName("SenderAddress") var SenderAddress: String? = null,
        @SerializedName("SenderCity") var SenderCity: Int? = null,
        @SerializedName("SenderContactMobile") var SenderContactMobile: String? = null,
        @SerializedName("SenderContactPhone") var SenderContactPhone: String? = null,
        @SerializedName("SenderEmail") var SenderEmail: String? = null,
        @SerializedName("SenderState") var SenderState: String? = null,
        @SerializedName("SenderCountry") var SenderCountry: Int? = null,
        @SerializedName("ReceiverContactName") var ReceiverContactName: String? = null,
        @SerializedName("ReceiverAddress") var ReceiverAddress: String? = null,
        @SerializedName("ReceiverCity") var ReceiverCity: Int? = null,
        @SerializedName("ReceiverContactMobile") var ReceiverContactMobile: String? = null,
        @SerializedName("ReceiverContactPhone") var ReceiverContactPhone: String? = null,
        @SerializedName("ReceiverEmail") var ReceiverEmail: String? = null,
        @SerializedName("ReceiverState") var ReceiverState: String? = null,
        @SerializedName("ReceiverCountry") var ReceiverCountry: Int? = null,
        @SerializedName("ContentTypeCode") var ContentTypeCode: String? = null,
        @SerializedName("NatureType") var NatureType: Int? = null,
        @SerializedName("Service") var Service: String? = null,
        @SerializedName("ShipmentType") var ShipmentType: String? = null,
        @SerializedName("DeleiveryType") var DeleiveryType: String? = null,
        @SerializedName("Registered") var Registered: String? = null,
        @SerializedName("PaymentType") var PaymentType: String? = null,
        @SerializedName("Pieces") var Pieces: Int? = null,
        @SerializedName("Weight") var Weight: Int? = null,
        @SerializedName("WeightUnit") var WeightUnit: String? = null,
        @SerializedName("Length") var Length: Int? = null,
        @SerializedName("Width") var Width: Int? = null,
        @SerializedName("Height") var Height: Int? = null,
        @SerializedName("DimensionUnit") var DimensionUnit: String? = null,
        @SerializedName("SendMailToSender") var SendMailToSender: String? = null,
        @SerializedName("SendMailToReceiver") var SendMailToReceiver: String? = null,
        @SerializedName("PreferredPickupDate") var PreferredPickupDate: String? = null,
        @SerializedName("PreferredPickupTimeFrom") var PreferredPickupTimeFrom: String? = null,
        @SerializedName("PreferredPickupTimeTo") var PreferredPickupTimeTo: String? = null,
        @SerializedName("Is_Return_Service") var IsReturnService: String? = null,
        @SerializedName("PrintType") var PrintType: String? = null,
        @SerializedName("Latitude") var Latitude: String? = null,
        @SerializedName("Longitude") var Longitude: String? = null,
        @SerializedName("AWBType") var AWBType: String? = null,
        @SerializedName("RequestType") var RequestType: String? = null

    )
}