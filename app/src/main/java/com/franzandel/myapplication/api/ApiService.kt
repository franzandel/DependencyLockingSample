package com.franzandel.myapplication.api

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Post
    
    @GET("posts")
    suspend fun getAllPosts(): List<Post>
} 