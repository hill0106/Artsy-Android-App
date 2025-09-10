package com.example.hw4.data.api

import com.example.hw4.data.model.Artist
import com.example.hw4.data.model.ArtistSearch
import com.example.hw4.data.model.Artworks
import com.example.hw4.data.model.Data
import com.example.hw4.data.model.Gene
import com.example.hw4.data.model.LoginData
import com.example.hw4.data.model.SignupRequest
import com.example.hw4.data.model.LoginRequest
import com.example.hw4.data.model.SimilarArtistItem
import com.example.hw4.data.model.User
import com.example.hw4.data.model.UserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.DELETE


interface ApiService {

    @GET("user/me")
    suspend fun getMyData(): Response<UserResponse>

    @GET("search")
    suspend fun getSearchData(
        @Query("q") query: String
    ): ArtistSearch

    @GET("artist/{id}")
    suspend fun getArtist(
        @Path("id") artistId: String
    ): Artist

    @GET("artwork/{id}")
    suspend fun getArtwork(
        @Path("id") artistId: String
    ): Artworks

    @GET("gene/{id}")
    suspend fun getGene(
        @Path("id") artistId: String
    ): Gene

    @GET("artist/smlr/{id}")
    suspend fun getSimilarArtists(
        @Path("id") artistId: String
    ): List<SimilarArtistItem>

    @POST("user/signup")
    suspend fun register(
        @Body signup: SignupRequest
    ): Response<User>

    @POST("auth/login")
    suspend fun login(
        @Body login: LoginRequest
    ): Response<LoginData>

    @POST("auth/logout")
    suspend fun logout(): Response<User>

    @GET("user/liked/{id}")
    suspend fun likeArtist(
        @Path("id") artistId: String
    ): Response<Void>

    @DELETE("user/rmliked/{id}")
    suspend fun removeArtist(
        @Path("id") artistId: String
    ): Response<Void>

    @DELETE("user")
    suspend fun deleteUser(): Response<User>
}