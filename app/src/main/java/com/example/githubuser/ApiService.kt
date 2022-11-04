package com.example.githubuser

import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @GET("users")
    fun getName(
        @Query("q") q: String?
    ): Call<ResponseUserSearch>

    @GET("/users/{name}")
    fun getDetailUser(
        @Path("name") name: String?
    ): Call<ResponseUserDetail>

    @Headers(
        "Authorization: ${BuildConfig.KEY}",
        "User-Agent: request"
    )
    @GET("/users/{name}/followers")
    fun getFollower(
        @Path("name") name: String?
    ): Call<List<ResponseUserFoll>>

    @Headers(
        "Authorization: ${BuildConfig.KEY}",
        "User-Agent: request"
    )
    @GET("/users/{name}/following")
    fun getFollowing(
        @Path("name") name: String?
    ): Call<List<ResponseUserFoll>>
}