package com.mangaproject.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:3000/"

    // ---------- Client de base (sans auth) ----------

    private fun baseClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

    private fun buildRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    // Retrofit sans token (public / auth)

    private val retrofit: Retrofit by lazy {
        buildRetrofit(baseClient())
    }

    // Auth (login / register …)
    val api: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    // API générale SANS auth (Jikan, listes publiques, etc.)
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // ---------- Client avec token (pour admin / routes protégées) ----------

    private fun clientWithAuth(token: String?): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            Log.d("AUTH", "TOKEN SENT = $token")
            val original: Request = chain.request()
            val builder = original.newBuilder()

            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            Log.d("AUTH", "FINAL HEADERS = ${builder.build().headers}")
            chain.proceed(builder.build())
        }

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .build()
    }

    /**
     * À utiliser pour les appels qui nécessitent d'être connecté
     * (admin, favoris de l'utilisateur, etc.)
     */
    fun authedApiService(token: String?): ApiService {
        val client = clientWithAuth(token)
        val retrofitAuth = buildRetrofit(client)
        return retrofitAuth.create(ApiService::class.java)
    }
}
