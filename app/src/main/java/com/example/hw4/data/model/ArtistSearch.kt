package com.example.hw4.data.model

data class ArtistSearch(
    val _embedded: Embedded,
    val offset: Int,
    val q: String,
    val total_count: Int
)

data class Embedded(
    val results: List<ArtistResult>
)

data class ArtistResult(
    val _links: Links,
    val description: Any,
    val og_type: String,
    val title: String,
    val type: String
)

data class Links(
    var self: Self,
    val thumbnail: Thumbnail
)

data class Thumbnail(
    val href: String=""
)

data class Self(
    val href: String=""
)

