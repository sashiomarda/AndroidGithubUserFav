package com.example.githubuser

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    enum class ApiOrigin {
        MAIN, DETAIL
    }

    companion object {
        fun getApiService(activity: ApiOrigin): ApiService {
            var urlUser = ""
            when (activity) {
                ApiOrigin.MAIN -> urlUser = "https://api.github.com/search/"
                ApiOrigin.DETAIL -> urlUser = "https://api.github.com/"
            }
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(urlUser)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}