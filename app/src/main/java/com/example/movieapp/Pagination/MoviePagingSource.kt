package com.example.movieapp.Pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.Model.Movie
import com.example.movieapp.RetrofitInstance.RetrofitInstance

class MoviePagingSource(
    private val apiKey: String,
    private val category: String,
    private val query: String? = null,
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = when (category) {

                "now_playing" -> RetrofitInstance.api.getNowPlayingMovies(apiKey, page)
                "upcoming" -> RetrofitInstance.api.getUpcomingMovies(apiKey, page)
                "top_rated" -> RetrofitInstance.api.getTopRatedMovies(apiKey, page)
                "search" -> RetrofitInstance.api.searchMovies(apiKey, query ?: "", page)
                else -> throw IllegalArgumentException("Unknown type")
            }

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page == response.total_pages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}


//                "popular" -> RetrofitInstance.api.getPopularMovies(apiKey, page)
//                "top_rated" -> RetrofitInstance.api.getTopRatedMovies(apiKey, page)
//                else -> RetrofitInstance.api.getTrendingMovies(apiKey, page)