package com.adiluhung.storyapp.data.remote.retrofit

import com.adiluhung.storyapp.data.remote.responses.DetailStoryResponse
import com.adiluhung.storyapp.data.remote.responses.FileUploadResponse
import com.adiluhung.storyapp.data.remote.responses.LoginResponse
import com.adiluhung.storyapp.data.remote.responses.RegisterResponse
import com.adiluhung.storyapp.data.remote.responses.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Authentication
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // Story
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page : Int,
        @Query("size") size : Int
    ): StoryResponse

    @GET("stories")
    fun getStoriesWithLatLng(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): Call<StoryResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ): Call<FileUploadResponse>
}