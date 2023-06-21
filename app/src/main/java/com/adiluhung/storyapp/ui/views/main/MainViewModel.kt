package com.adiluhung.storyapp.ui.views.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adiluhung.storyapp.data.StoryRepository
import com.adiluhung.storyapp.data.local.dataStore.UserModel
import com.adiluhung.storyapp.data.local.dataStore.UserPreferences
import com.adiluhung.storyapp.data.remote.responses.StoryItem
import kotlinx.coroutines.launch

class MainViewModel(
    storyRepository: StoryRepository,
    private val pref: UserPreferences
) :
    ViewModel() {
    val stories: LiveData<PagingData<StoryItem>> =
        storyRepository.getStories().cachedIn(viewModelScope)

    fun getLoggedInUser(): LiveData<UserModel> = pref.getLoggedInUser().asLiveData()

    fun logout() {
        viewModelScope.launch {
            pref.deleteLoggedInUser()
        }
    }
}