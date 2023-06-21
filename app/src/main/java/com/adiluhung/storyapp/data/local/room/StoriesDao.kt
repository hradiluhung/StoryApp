package com.adiluhung.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adiluhung.storyapp.data.remote.responses.StoryItem

@Dao
interface StoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryItem>)

    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int, StoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAllStories()
}