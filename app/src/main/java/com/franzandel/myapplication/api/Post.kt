package com.franzandel.myapplication.api

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
) 