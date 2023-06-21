package com.adiluhung.storyapp.utils

import com.adiluhung.storyapp.data.remote.responses.StoryItem

object DummyStory {
    fun generateDummyStory(): List<StoryItem> {
        val storyList = ArrayList<StoryItem>()
        for (i in 0..10) {
            val news = StoryItem(
                "story-$i",
                "https://story-api.dicoding.dev/images/stories/photos-1683667982709_sm-NHiVW.jpg",
                "2023-05-09T21:33:02.711Z",
                "John Doe",
                "Ini deskripsi",
                -6.2991235,
                106.9039209
            )
            storyList.add(news)
        }
        return storyList
    }
}