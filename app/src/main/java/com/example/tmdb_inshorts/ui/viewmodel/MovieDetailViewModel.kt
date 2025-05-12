package com.example.tmdb_inshorts.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdb_inshorts.data.model.Movie
import com.example.tmdb_inshorts.di.DependencyProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DependencyProvider.getMovieRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                // First try to get from cache
                repository?.getMovieDetails(movieId)
                    ?.catch { e ->
                        _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    ?.collect { cachedMovie ->
                        if (cachedMovie != null) {
                            _uiState.value = UiState.Success(cachedMovie)
                        }
                    }

                // Then refresh from network
                val movie = repository?.refreshMovieDetails(movieId)!!
                _uiState.value = UiState.Success(movie)
            } catch (e: Exception) {
                // Only show error if we don't have cached data
                if (_uiState.value !is UiState.Success) {
                    _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    fun refreshMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val movie = repository?.refreshMovieDetails(movieId)
                _uiState.value = movie?.let { UiState.Success(it) }!!
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val movie: Movie) : UiState()
        data class Error(val message: String) : UiState()
    }
} 