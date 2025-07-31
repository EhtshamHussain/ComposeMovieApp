package com.example.movieapp.Model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
)