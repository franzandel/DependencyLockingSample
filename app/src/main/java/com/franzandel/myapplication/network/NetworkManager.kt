package com.franzandel.myapplication.network

import com.franzandel.myapplication.api.ApiService
import com.franzandel.myapplication.api.Post
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkManager {
    
    /**
     * VERSION CONFLICT DEMONSTRATION
     * 
     * This setup demonstrates how Retrofit's transitive dependency resolution
     * can cause version conflicts with explicitly declared dependencies:
     * 
     * - OkHttp 3.3.1: Ancient version declared explicitly (2016 era)
     * - Retrofit 2.8.1: Internally uses OkHttp 3.14.7 (much newer)
     * - Version Resolution: 3.3.1 → 3.14.7 (11+ versions jump!)
     * - Logging Interceptor: 3.3.1 (compiled against old APIs)
     * 
     * This demonstrates real-world dependency management challenges:
     * - Build succeeds due to Gradle's version resolution
     * - Potential runtime issues when old/new APIs interact
     * - Importance of dependency locking and version alignment
     */
    
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message ->
            println("🔥 OkHttp Log: $message")
        }
        
        // This should work fine - basic logging setup
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(createLoggingInterceptor())
        
        // VERSION CONFLICT DEMONSTRATION:
        // Add interceptor to showcase potential API compatibility issues
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            // Add headers to identify this as a version conflict demo
            val requestWithHeaders = originalRequest.newBuilder()
                .addHeader("User-Agent", "Version-Conflict-Demo/1.0")
                .addHeader("X-OkHttp-Declared", "3.3.1")
                .addHeader("X-Retrofit-Version", "2.8.1")
                .addHeader("X-OkHttp-Resolved", "3.14.7")
                .build()

            println("🚀 Making API call with version conflict scenario...")
            println("📋 OkHttp: 3.3.1 (declared) → 3.14.7 (resolved by Retrofit 2.8.1)")
            
            // Response handling with version-aware logging
            try {
                val response = chain.proceed(requestWithHeaders)
                
                // Log response details to demonstrate successful API interaction
                println("✅ Response Code: ${response.code()}")
                println("✅ Response Headers: ${response.headers()}")
                
                // Response body handling - demonstrating API compatibility
                val responseBody = response.body()
                if (responseBody != null) {
                    // These methods work because Gradle resolved to OkHttp 3.14.7
                    val contentLength = responseBody.contentLength()
                    val contentType = responseBody.contentType()
                    println("✅ Response body - Length: $contentLength, Type: $contentType")
                    
                    // Note: This works with resolved version 3.14.7, not declared 3.3.1
                    println("✅ API call successful with resolved OkHttp version")
                }
                
                response
            } catch (e: Exception) {
                println("❌ API call failed: ${e.javaClass.simpleName} - ${e.message}")
                throw e
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun getRandomPost(): Result<Post> {
        return try {
            println("🚀 VERSION CONFLICT DEMO: Making API call...")
            println("📋 Declared: OkHttp 3.3.1 + Retrofit 2.8.1")
            println("📋 Resolved: OkHttp 3.14.7 (via Retrofit's transitive dependency)")
            println("⚙️  Gradle resolved version conflicts automatically")
            
            val post = apiService.getPost((1..100).random())
            
            println("✅ API call successful!")
            println("🎯 Demonstration: Gradle's dependency resolution prevents runtime errors")
            println("📝 Note: Without dependency locking, versions can change unexpectedly")
            Result.success(post)
        } catch (e: Exception) {
            val errorMsg = "❌ API call failed: ${e.javaClass.simpleName} - ${e.message}"
            println(errorMsg)
            Result.failure(e)
        }
    }
} 