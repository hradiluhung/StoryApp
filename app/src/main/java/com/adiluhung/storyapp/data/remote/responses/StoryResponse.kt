package com.adiluhung.storyapp.data.remote.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class StoryResponse(
	val listStory: List<StoryItem>,
	val error: Boolean,
	val message: String
)

data class DetailStoryResponse(
	val error: Boolean,
	val message: String,
	val story: StoryItem
)

@Entity(tableName = "story")
data class StoryItem(
	@PrimaryKey
	val id: String,
	val photoUrl: String,
	val createdAt: String,
	val name: String,
	val description: String,
	val lon: Double,
	val lat: Double
)

data class FileUploadResponse(
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)


