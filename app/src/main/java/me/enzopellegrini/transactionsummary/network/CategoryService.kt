package me.enzopellegrini.transactionsummary.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

const val BASE_URL = ""
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface CategoryService {
    @GET("/categories/get")
    suspend fun fetchCategories(): CategoryResponse
}

object CategoryApi {
    val retrofitService: CategoryService by lazy {
        retrofit.create(CategoryService::class.java)
    }
}