package com.example.githubuser

import com.google.gson.annotations.SerializedName

data class ResponseUserSearch(

    @field:SerializedName("items")
    val items: List<ItemsItem>
)

data class ItemsItem(

    @field:SerializedName("login")
    val login: String,

    @field:SerializedName("avatar_url")
    val avatarUrl: String,

    )

