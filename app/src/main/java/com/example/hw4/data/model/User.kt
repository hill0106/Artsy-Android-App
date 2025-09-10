package com.example.hw4.data.model

data class User(
    val `data`: DataX,
    val message: String
)

data class DataX(
    val authenticatedUser: AuthenticatedUser
)

data class AuthenticatedUser(
    val __v: Int,
    val _id: String,
    val email: String,
    val favorite: List<Favorite>,
    val name: String,
    val password: String,
    val profileImageUrl: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)