package com.example.tmdb_inshorts.di

import android.content.Context
import com.example.tmdb_inshorts.data.local.AppDatabase
import com.example.tmdb_inshorts.data.repository.MovieRepository

object DependencyProvider {
    private var database: AppDatabase? = null
    private var movieRepository: MovieRepository? = null

    fun initialize(context: Context) {
        if (database == null) {
            database = AppDatabase.getDatabase(context)
        }
    }

    fun getMovieRepository(): MovieRepository? {
        if (movieRepository == null) {
            movieRepository = database?.movieDao()?.let { MovieRepository.getInstance(it) }
        }
        return movieRepository
    }
} 