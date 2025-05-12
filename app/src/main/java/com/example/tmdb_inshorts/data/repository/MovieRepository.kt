package com.example.tmdb_inshorts.data.repository

import com.example.tmdb_inshorts.data.local.AppDatabase
import com.example.tmdb_inshorts.data.local.MovieDao
import com.example.tmdb_inshorts.data.local.MovieEntity
import com.example.tmdb_inshorts.data.model.Movie
import com.example.tmdb_inshorts.data.model.MovieResponse
import com.example.tmdb_inshorts.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepository private constructor(
    private val movieDao: MovieDao
) {
    private val apiService = RetrofitClient.apiService

    fun getTrendingMovies(): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { entities ->
            entities.map { it.toMovie() }
        }
    }

    fun getBookmarkedMovies(): Flow<List<Movie>> {
        return movieDao.getBookmarkedMovies().map { entities ->
            entities.map { it.toMovie() }
        }
    }

    suspend fun refreshTrendingMovies(page: Int = 1): MovieResponse {
        val response = apiService.getTrendingMovies(API_KEY, page)
        // Cache the movies
        movieDao.deleteAllMovies() // Clear old data
        movieDao.insertMovies(response.results.map { MovieEntity.fromMovie(it) })
        return response
    }

    suspend fun searchMovies(query: String, page: Int = 1): MovieResponse {
        return apiService.searchMovies(API_KEY, query, page)
    }

    fun getMovieDetails(movieId: Int): Flow<Movie?> {
        return movieDao.getMovieById(movieId).map { it?.toMovie() }
    }

    suspend fun refreshMovieDetails(movieId: Int): Movie {
        val movie = apiService.getMovieDetails(movieId, API_KEY)
        movieDao.insertMovie(MovieEntity.fromMovie(movie))
        return movie
    }

    suspend fun toggleBookmark(movieId: Int, isBookmarked: Boolean) {
        movieDao.updateBookmarkStatus(movieId, isBookmarked)
    }

    companion object {
        private const val API_KEY = "42c6e41563c3159135682b58ae749904"

        @Volatile
        private var INSTANCE: MovieRepository? = null

        fun getInstance(movieDao: MovieDao): MovieRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MovieRepository(movieDao)
                INSTANCE = instance
                instance
            }
        }
    }
} 