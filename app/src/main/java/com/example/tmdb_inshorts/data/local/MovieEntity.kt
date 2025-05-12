package com.example.tmdb_inshorts.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tmdb_inshorts.data.model.Movie

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val overview: String
) {
    fun toMovie(): Movie {
        return Movie(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            overview = overview
        )
    }

    companion object {
        fun fromMovie(movie: Movie): MovieEntity {
            return MovieEntity(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage,
                overview = movie.overview
            )
        }
    }
} 