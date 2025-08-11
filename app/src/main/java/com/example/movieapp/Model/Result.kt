package com.example.movieapp.Model

import androidx.compose.runtime.Stable


data class Movie(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val overview: String,
    val vote_average: Double,
    val original_title: String,
    val duration : Int = 120
)
