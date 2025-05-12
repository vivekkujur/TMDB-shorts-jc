package com.example.tmdb_inshorts.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdb_inshorts.data.model.Movie
import com.example.tmdb_inshorts.di.DependencyProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DependencyProvider.getMovieRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults

    private val _bookmarkedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val bookmarkedMovies: StateFlow<List<Movie>> = _bookmarkedMovies

    private var isSearching = false

    init {
        loadMovies()
        loadBookmarkedMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                // First try to load from cache
                repository?.getTrendingMovies()
                    ?.catch { e ->
                        _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    ?.collect { movies ->
                        if (movies.isNotEmpty()) {
                            _uiState.value = UiState.Success(movies)
                        }else {
                            val response = repository?.refreshTrendingMovies()
                            if (response != null) {
                                _uiState.value = UiState.Success(response.results)
                            }
                        }
                    }

                // Then refresh from network
                val response = repository?.refreshTrendingMovies()
                if (response != null) {
                    _uiState.value = UiState.Success(response.results)
                }
            } catch (e: Exception) {
                if (_uiState.value !is UiState.Success) {
                    _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    private fun loadBookmarkedMovies() {
        viewModelScope.launch {
            repository?.getBookmarkedMovies()
                ?.collect { movies ->
                    _bookmarkedMovies.value = movies
                }
        }
    }

    fun refreshMovies() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val response = repository?.refreshTrendingMovies()
                if (response != null) {
                    _uiState.value = UiState.Success(response.results)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            isSearching = false
            _searchResults.value = emptyList()
            return
        }

        isSearching = true
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val response = repository?.searchMovies(query)
                if (response != null) {
                    _searchResults.value = response.results
                    _uiState.value = UiState.Success(response.results)

                }

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun clearSearch() {
        isSearching = false
        _searchResults.value = emptyList()
        loadMovies()
    }

    fun toggleBookmark(movieId: Int, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository?.toggleBookmark(movieId, isBookmarked)
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val movies: List<Movie>) : UiState()
        data class Error(val message: String) : UiState()
    }
} 