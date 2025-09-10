package com.example.hw4.data.model

data class Favorite(
    val _id: String="",
    val artistId: String="",
    val biography: String="",
    val birthday: String="",
    val deathday: String="",
    val image: String="",
    val isFavorite: Boolean=false,
    val likedAt: String="",
    val name: String="",
    val nationality: String=""
)