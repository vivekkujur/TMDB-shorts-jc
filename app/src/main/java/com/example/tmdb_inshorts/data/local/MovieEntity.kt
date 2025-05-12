package com.example.tmdb_inshorts.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tmdb_inshorts.data.model.Movie

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val isBookmarked: Boolean = false,
    val backdropPath: String,


) {
    fun toMovie(): Movie {
        return Movie(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            backdropPath = backdropPath
        )
    }

    companion object {
        fun fromMovie(movie: Movie, isBookmarked: Boolean = false): MovieEntity {
            return MovieEntity(
                id = movie.id,
                title = movie.title,
                overview = movie.overview,
                posterPath = movie.posterPath?:"",
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage,
                isBookmarked = isBookmarked,
                backdropPath= movie.backdropPath?:""
            )
        }
    }
} 