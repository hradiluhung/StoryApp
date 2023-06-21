package com.adiluhung.storyapp.ui.views.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adiluhung.storyapp.data.local.dataStore.UserModel
import com.adiluhung.storyapp.data.local.dataStore.UserPreferences
import com.adiluhung.storyapp.data.remote.responses.LoginResponse
import com.adiluhung.storyapp.data.remote.responses.RegisterResponse
import com.adiluhung.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isError = MutableLiveData<Boolean?>()
    val isError: LiveData<Boolean?> = _isError

    fun login(email: String, password: String) {
        _isLoading.value = true
        _isError.value = null
        _message.value = null
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody?.error == false) {
                        val result = responseBody.loginResult
                        _isLoading.value = false
                        _isError.value = responseBody.error
                        _message.value = null

                        viewModelScope.launch {
                            try {
                                pref.saveLoggedInUser(
                                    UserModel(
                                        result.userId,
                                        result.name,
                                        result.token
                                    )
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Halo Gais")
                            }
                        }
                    }
                } else {
                    val jsonError = response.errorBody()?.string()?.let { JSONObject(it) }
                    _isLoading.value = false
                    _isError.value = jsonError?.getBoolean("error")
                    _message.value = jsonError?.getString("message")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        _isError.value = null
        _message.value = null
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody?.error == false) {
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

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}