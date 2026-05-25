package com.mansi.aiinsight.data.api


import android.content.Context
import com.google.gson.GsonBuilder
import com.mansi.aiinsight.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null

    fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val httpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

            // Add logging interceptor
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
            httpClientBuilder.addInterceptor(loggingInterceptor)

            // Add auth interceptor
            httpClientBuilder.addInterceptor { chain ->
                val originalRequest = chain.request()
                val token = getToken(context)

                val requestBuilder = originalRequest.newBuilder()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
                requestBuilder.header("Content-Type", "application/json")

                chain.proceed(requestBuilder.build())
            }

            val gson = GsonBuilder()
                .setLenient()
                .create()

            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }

    fun getApiService(context: Context): ApiService {
        if (apiService == null) {
            apiService = getRetrofit(context).create(ApiService::class.java)
        }
        return apiService!!
    }

    private fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }

    fun clearToken(context: Context) {
        val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        sharedPref.edit().remove("token").apply()
        apiService = null
        retrofit = null
    }
}