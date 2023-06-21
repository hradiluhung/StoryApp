package com.adiluhung.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.adiluhung.storyapp.data.local.dataStore.UserPreferences
import com.adiluhung.storyapp.data.local.room.StoryDatabase
import com.adiluhung.storyapp.data.remote.responses.StoryItem
import com.adiluhung.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val pref : UserPreferences
) {

    private val userToken = runBlocking {
        pref.getLoggedInUser()
            .map { userModel -> userModel.token }
            .first()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(userToken, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storiesDao().getAllStories()
            }
        ).liveData
    }
}