package com.adiluhung.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.adiluhung.storyapp.data.StoryRepository
import com.adiluhung.storyapp.data.local.dataStore.UserPreferences
import com.adiluhung.storyapp.data.local.room.StoryDatabase
import com.adiluhung.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context, pref: UserPreferences): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService, pref)
    }

    fun provideUserPreference(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferences.getInstance(
            dataStore
        )
    }
}