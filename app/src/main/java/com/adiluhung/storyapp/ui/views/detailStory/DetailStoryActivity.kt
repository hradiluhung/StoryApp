package com.adiluhung.storyapp.ui.views.detailStory

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.adiluhung.storyapp.R
import com.adiluhung.storyapp.databinding.ActivityDetailStoryBinding
import com.adiluhung.storyapp.ui.views.ViewModelFactory
import com.bumptech.glide.Glide

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "logged_in_user")

class DetailStoryActivity : AppCompatActivity() {
    private var binding: ActivityDetailStoryBinding? = null
    private val detailViewModel: DetailViewModel by viewModels {
        ViewModelFactory(
            this, dataStore
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val postId = intent.getStringExtra(EXTRA_STORY_ID)

        setupViewModel()
        showDetail(postId)
        setupAction()
    }

    private fun setupAction() {
        binding?.topAppBar?.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showDetail(postId: String?) {
        if (postId != null) {
            detailViewModel.getDetailStory(postId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupViewModel() {
        detailViewModel.storyItem.observe(this) { story ->
            binding?.ivStory?.let { imageView ->
                Glide.with(this)
                    .load(story.photoUrl)
                    .into(imageView)
            }
            binding?.tvSenderName?.text = getString(R.string.sender_name, story.name)
            binding?.tvDescription?.text = story.description
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            showProgress(isLoading)
        }

    }

    fun showProgress(isLoading: Boolean) {
        when (isLoading) {
            true -> {
                binding?.progress?.visibility = View.VISIBLE
                binding?.ivStory?.visibility = View.INVISIBLE
                binding?.tvSenderName?.visibility = View.INVISIBLE
                binding?.tvDescription?.visibility = View.INVISIBLE
            }

            else -> {
                binding?.progress?.visibility = View.INVISIBLE
                binding?.ivStory?.visibility = View.VISIBLE
                binding?.tvSenderName?.visibility = View.VISIBLE
                binding?.tvDescription?.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}