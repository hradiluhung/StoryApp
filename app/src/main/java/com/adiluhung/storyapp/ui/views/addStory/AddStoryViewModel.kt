package com.adiluhung.storyapp.ui.views.addStory

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adiluhung.storyapp.data.local.dataStore.UserPreferences
import com.adiluhung.storyapp.data.remote.responses.FileUploadResponse
import com.adiluhung.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(private val pref: UserPreferences) : ViewModel() {
    private val userToken = runBlocking {
        pref.getLoggedInUser()
            .map { userModel -> userModel.token }
            .first()
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun uploadImage(storyImage: File?, description: String, currentLocation: Location? = null) {
        _isLoading.value = true
        if (storyImage != null) {
            val descriptionReqBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = storyImage.asRequestBody("image/jpeg".toMediaType())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                storyImage.name,
                requestImageFile
            )

            val client = ApiConfig.getApiService()
                .uploadStory("bearer $userToken", imageMultiPart, descriptionReqBody,
                    currentLocation?.latitude, currentLocation?.longitude)
            client.enqueue(object : Callback<FileUploadResponse> {
                override fun onResponse(
                    call: Call<FileUploadResponse>,
                    response: Response<FileUploadResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            _isLoading.value = false
                            _isError.value = responseBody.error
                            _message.value = responseBody.message
                        }
                    } else {
                        val jsonError = response.errorBody()?.string()?.let { JSONObject(it) }
                        _isLoading.value = false
                        _isError.value = jsonError?.getBoolean("error")
                        _message.value = jsonError?.getString("message")
                    }
                }

                override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                }

            })
        }
    }

    companion object {
        private val TAG = AddStoryViewModel::class.java.simpleName
    }
}