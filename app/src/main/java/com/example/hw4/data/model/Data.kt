package com.example.hw4.data.model

data class Data(
    val _id: String="",
    val email: String="",
    val favorite: List<Favorite>,
    val name: String="",
    val profileImageUrl: String=""
)

data class UserResponse(
    val data: Data
)