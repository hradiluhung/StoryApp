package com.adiluhung.storyapp.ui.views.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adiluhung.storyapp.data.local.dataStore.UserPreferences
import com.adiluhung.storyapp.data.remote.responses.StoryItem
import com.adiluhung.storyapp.data.remote.responses.StoryResponse
import com.adiluhung.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreferences) : ViewModel() {
    private val userToken = runBlocking {
        pref.getLoggedInUser()
            .map { userModel -> userModel.token }
            .first()
    }

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _stories = MutableLiveData<List<StoryItem>>()
    val stories: LiveData<List<StoryItem>> = _stories

    init {
        getStoriesWithLatLng()
    }

    private fun getStoriesWithLatLng() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStoriesWithLatLng("Bearer $userToken")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody?.error == false) {
                        Log.d("MapsViewModel", "list story : ${responseBody.listStory}")
                        _isLoading.value = false
                        _stories.value = responseBody.listStory
                    }
                } else {
                    val jsonError = response.errorBody()?.string()?.let { JSONObject(it) }
                    _isLoading.value = false
                    _errorMessage.value = jsonError?.getString("message")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private val TAG = MapsViewModel::class.java.simpleName
    }
}