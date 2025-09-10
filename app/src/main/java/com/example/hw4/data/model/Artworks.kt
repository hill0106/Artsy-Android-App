package com.example.hw4.data.model

data class Artworks(
    val _embedded: EmbeddedArtwork,
)

data class EmbeddedArtwork(
    val artworks: List<Artwork>
)

data class Artwork(
    val _links: Links,
    val title: String,
    val id: String
)

