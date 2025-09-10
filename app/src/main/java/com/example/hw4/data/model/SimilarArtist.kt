package com.example.hw4.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimilarArtistItem(
    @SerialName("artistId"  ) val artistId  : String,
    @SerialName("title"     ) val title     : String,
    @SerialName("isFavorite") val isFavorite: Boolean,
    @SerialName("image"     ) val image     : String
)