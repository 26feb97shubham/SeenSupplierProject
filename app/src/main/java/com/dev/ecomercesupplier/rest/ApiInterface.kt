package com.dev.ecomercesupplier.rest

import com.dev.ecomercesupplier.model.AttributeNameResponse
import com.dev.ecomercesupplier.model.BookingResultResponse
import com.dev.ecomercesupplier.model.MyJsonDataModel
import com.dev.ecomercesupplier.model.TrackFinalResultResponse
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @POST(ApiUtils.LOGIN)
    fun login(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.Register)
    fun signUp(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ForgotPassword)
    fun forgotPassword(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ChangePassword)
    fun changePassword(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.VerifyAccount)
    fun verifyAccount(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ResendOtp)
    fun resendOtp(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ResetPassword)
    fun resetPassword(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetProfile)
    fun getProfile(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.PackagePlanPayment)
    fun packagePlanPayment(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetCountries)
    fun getCountries(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetPackages)
    fun getPackages(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetCategories)
    fun getCategories(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetAttributesName)
    fun getAttributeName(@Body body: RequestBody): Call<AttributeNameResponse>

    @POST(ApiUtils.GetHomes)
    fun getHomes(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ChooseCategories)
    fun chooseCategories(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.EditProfile)
    fun editProfile(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ContactUs)
    fun contactUs(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.AddItem)
    fun addItem(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetItem)
    fun getItem(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.CouponAdd)
    fun couponAdd(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetMyCoupons)
    fun getMyCoupons(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.CouponDelete)
    fun couponDelete(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetNotifications)
    fun getNotifications(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.NotificationDelete)
    fun notificationDelete(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.DeleteItem)
    fun deleteItem(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ReceiveOrders)
    fun receiveOrders(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.AcceptReject)
    fun acceptReject(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.EditBankDetail)
    fun editBankDetail(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetTermsConditions)
    fun getTermsConditions(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetPrivacyPolicy)
    fun getPrivacyPolicy(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetAboutUs)
    fun getAboutUs(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetFaq)
    fun getFaq(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.UpdateCategory)
    fun updateCategory(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.revenues)
    fun revenues(@Body body: RequestBody?) : Call<ResponseBody?>?


    @POST(ApiUtils.createBooking)
    fun createBooking(@HeaderMap headerMap: Map<String, String>, @Body body: MyJsonDataModel?) : Call<BookingResultResponse?>?

    @POST(ApiUtils.createbooking)
    fun createbooking(@Body body: RequestBody?) : Call<ResponseBody?>?

    @GET(ApiUtils.getTrackDetails)
    fun getTrackDetails(@Query("track_id") track_id : String) : Call<TrackFinalResultResponse?>?
}