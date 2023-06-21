package com.adiluhung.storyapp.ui.views.detailStory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adiluhung.storyapp.data.local.dataStore.UserPreferences
import com.adiluhung.storyapp.data.remote.responses.DetailStoryResponse
import com.adiluhung.storyapp.data.remote.responses.StoryItem
import com.adiluhung.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val pref: UserPreferences) : ViewModel() {
    private val userToken = runBlocking {
        pref.getLoggedInUser()
            .map { userModel -> userModel.token }
            .first()
    }

    private val _storyItem = MutableLiveData<StoryItem>()
    val storyItem: LiveData<StoryItem> = _storyItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetailStory(id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailStory("Bearer $userToken", id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody?.error == false) {
                        _isLoading.value = false
                        _storyItem.value = responseBody.story
                    }
                } else {
                    _isLoading.value = false
                    Log.d(TAG, response.message())
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private val TAG = DetailViewModel::class.java.simpleName
    }
}