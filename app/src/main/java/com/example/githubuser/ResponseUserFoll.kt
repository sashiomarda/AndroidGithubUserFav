package com.example.githubuser

import com.google.gson.annotations.SerializedName

data class ResponseUserFoll(

    @field:SerializedName("login")
    val login: String,

    @field:SerializedName("avatar_url")
    val avatarUrl: String,

    )
