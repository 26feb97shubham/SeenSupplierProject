package com.dev.ecomercesupplier.rest

import android.text.TextUtils
import android.util.Log
import okhttp3.*
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient


class ApiClient {
    companion object {
        private var retrofit: Retrofit? = null
        private var userretrofit: Retrofit? = null
        private var emiratesPostRetrofit: Retrofit? = null
        private var emiratesPostTrackRetrofit: Retrofit? = null
        val baseUrl: String = "https://seen-uae.com/SupplierApi/"
        val emiratesPostBaseUrl: String = "https://osbtest.epg.gov.ae/ebs/genericapi/"
        val emiratesPostTrackingBaseUrl: String =
            "https://osbtest.epg.gov.ae/ebs/epg.pos.trackandtrace.rest/"

        fun getClient(): Retrofit? {
            if (retrofit == null) {
                val okHttpClient = OkHttpClient().newBuilder().connectTimeout(80, TimeUnit.SECONDS)
                    .readTimeout(80, TimeUnit.SECONDS).writeTimeout(80, TimeUnit.SECONDS)
                    .addInterceptor(LoginInterceptor()).build()

                retrofit =
                    Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient).addConverterFactory(
                        GsonConverterFactory.create()
                    ).build()
            }
            return retrofit
        }

        private val userbaseUrl: String = "https://seen-uae.com/BuyerApi/"

        fun UsergetClient(): Retrofit? {
            if (userretrofit == null) {
                val okHttpClient = OkHttpClient().newBuilder().connectTimeout(80, TimeUnit.SECONDS)
                    .readTimeout(80, TimeUnit.SECONDS).writeTimeout(80, TimeUnit.SECONDS)
                    .addInterceptor(LoginInterceptor()).build()

                userretrofit = Retrofit.Builder().baseUrl(userbaseUrl).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return userretrofit
        }


        fun emiratesPostGetClient(): Retrofit? {
//            if (emiratesPostRetrofit == null) {
//                val httpClient = OkHttpClient().newBuilder()
////                val okHttpClient = OkHttpClient().newBuilder().connectTimeout(80, TimeUnit.SECONDS)
////                    .readTimeout(80, TimeUnit.SECONDS).writeTimeout(80, TimeUnit.SECONDS)
//                    httpClient.connectTimeout(80, TimeUnit.SECONDS)
//                    .readTimeout(80, TimeUnit.SECONDS).writeTimeout(80, TimeUnit.SECONDS)
//
//
//                emiratesPostRetrofit = Retrofit.Builder().baseUrl(emiratesPostBaseUrl).client(httpClient.build()).addConverterFactory(GsonConverterFactory.create()).build()
//            }
//            return emiratesPostRetrofit
            if (emiratesPostRetrofit == null) {
                val okHttpClient1 = OkHttpClient().newBuilder().connectTimeout(8, TimeUnit.MINUTES)
                    .readTimeout(8, TimeUnit.MINUTES).writeTimeout(8, TimeUnit.MINUTES)
                    .build()
                Log.e("okhttp:=> ", emiratesPostBaseUrl)
                emiratesPostRetrofit =
                    Retrofit.Builder().baseUrl(emiratesPostBaseUrl).client(okHttpClient1)
                        .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return emiratesPostRetrofit
        }

        fun emiratesPostTrackGetClient(): Retrofit? {
            if (emiratesPostTrackRetrofit == null) {
                val okHttpClient = OkHttpClient().newBuilder().connectTimeout(8, TimeUnit.MINUTES)
                    .readTimeout(8, TimeUnit.MINUTES).writeTimeout(8, TimeUnit.MINUTES)
                    .addInterceptor(BasicAuthInterceptor("osb.user", "EPG@12345")).build()

                emiratesPostTrackRetrofit =
                    Retrofit.Builder().baseUrl(emiratesPostTrackingBaseUrl).client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return emiratesPostTrackRetrofit
        }

        fun createBuilder(paramsName: Array<String>, paramsValue: Array<String>): FormBody.Builder {
            val builder = FormBody.Builder()
            for (i in paramsName.indices) {
                Log.e("create_builder:", paramsName[i] + ":" + paramsValue[i])
                if (!TextUtils.isEmpty(paramsValue[i])) {
                    builder.add(paramsName[i], paramsValue[i])
                } else {
                    builder.add(paramsName[i], "")
                }
            }
            return builder
        }

        fun createMultipartBodyBuilder(
            paramsName: Array<String>,
            paramsValue: Array<String>
        ): MultipartBody.Builder? {
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            for (i in paramsName.indices) {
                Log.e("multipart_builder:", paramsName[i] + ":" + paramsValue[i])
                if (!TextUtils.isEmpty(paramsValue[i])) {
                    builder.addFormDataPart(paramsName[i], paramsValue[i])
                } else {
                    builder.addFormDataPart(paramsName[i], "")
                }
            }
            return builder
        }

        class LoginInterceptor : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val t1 = System.nanoTime()
                Log.e(
                    "OkHttp",
                    String.format(
                        "--> Sending request %s on %s%n%s",
                        request.url(),
                        chain.connection(),
                        request.headers()
                    )
                )
                try {
                    val requestBuffer = Buffer()
                    Log.e("OkHttp", requestBuffer.readUtf8().replace("=", ":").replace("&", "\n"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val response = chain.proceed(request)
                val t2 = System.nanoTime()
                Log.e(
                    "OkHttp",
                    String.format(
                        "<-- Received response for %s in %.1fms%n%s",
                        response.request().url(),
                        (t2 - t1) / 1e6,
                        response.headers()
                    )
                )
                val contentType = response.body()!!.contentType()
                val content = response.body()!!.string()
                Log.e("OkHttp", content)
                val wrappedBody = ResponseBody.create(contentType, content)
                return response.newBuilder().body(wrappedBody).build()
            }
        }

        class BasicAuthInterceptor(username: String, password: String) : Interceptor {
            private var credentials: String = Credentials.basic(username, password)

            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                var request = chain.request()
                request = request.newBuilder().header("Authorization", credentials).build()

                return chain.proceed(request)
            }
        }
    }
}