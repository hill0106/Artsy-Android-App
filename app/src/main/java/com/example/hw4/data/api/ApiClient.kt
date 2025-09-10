package com.example.hw4.data.api


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import java.util.concurrent.TimeUnit

object ApiClient {
    private lateinit var retrofit: Retrofit
    fun init(context: Context) {
         val cookieJar = PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(context)
        )
        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout   (30, TimeUnit.SECONDS)
            .writeTimeout  (30, TimeUnit.SECONDS)
            .build()


        retrofit =
            Retrofit.Builder()
                .baseUrl("https://csci571-web-tech-hw3.uw.r.appspot.com/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val apiService: ApiService
        get() = retrofit.create(ApiService::class.java)
}


