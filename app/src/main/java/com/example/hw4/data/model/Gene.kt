package com.example.hw4.data.model

data class Gene(
    val _embedded: GeneEmbedded,
)

data class GeneEmbedded(
    val genes: List<GeneX>
)

data class GeneX(
    val _links: Links,
    val description: String="",
    val name: String="",

)