package com.adiluhung.storyapp.ui.views

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adiluhung.storyapp.di.Injection
import com.adiluhung.storyapp.ui.views.addStory.AddStoryViewModel
import com.adiluhung.storyapp.ui.views.auth.AuthViewModel
import com.adiluhung.storyapp.ui.views.detailStory.DetailViewModel
import com.adiluhung.storyapp.ui.views.main.MainViewModel
import com.adiluhung.storyapp.ui.views.maps.MapsViewModel

class ViewModelFactory(private val context: Context, private val dataStore: DataStore<Preferences>) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideRepository(context, Injection.provideUserPreference(dataStore)), Injection.provideUserPreference(dataStore)) as T
            }

            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(Injection.provideUserPreference(dataStore)) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(Injection.provideUserPreference(dataStore)) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(Injection.provideUserPreference(dataStore)) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(Injection.provideUserPreference(dataStore)) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}