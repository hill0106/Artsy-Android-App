package com.example.hw4.data.model

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("token")
    val `data`: String,

    val id: String,
    val message: String
)

data class LoginRequest (
    val email: String,
    val password: String
)