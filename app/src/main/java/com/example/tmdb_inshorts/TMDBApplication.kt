package com.example.tmdb_inshorts

import android.app.Application
import com.example.tmdb_inshorts.di.DependencyProvider

class TMDBApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyProvider.initialize(this)
    }
} 