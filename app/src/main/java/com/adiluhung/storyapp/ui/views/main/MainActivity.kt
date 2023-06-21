package com.adiluhung.storyapp.ui.views.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.adiluhung.storyapp.R
import com.adiluhung.storyapp.databinding.ActivityMainBinding
import com.adiluhung.storyapp.ui.views.ViewModelFactory
import com.adiluhung.storyapp.ui.views.addStory.AddStoryActivity
import com.adiluhung.storyapp.ui.views.auth.LoginActivity
import com.adiluhung.storyapp.ui.views.maps.MapsActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "logged_in_user")

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(
            this, dataStore
        )
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        checkLoggedInUser()
        setContentView(binding.root)

        setupAction()
        setupViewModel()
    }

    private fun checkLoggedInUser() {
        mainViewModel.getLoggedInUser().observe(this) { user ->
            if (user.token.isEmpty()) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }

            binding.tvWelcome.text = getString(R.string.welcome, user.name)
        }

    }

    private fun setupViewModel() {
        val adapter = StoryListAdapter()
        mainViewModel.stories.observe(this) { stories ->
            adapter.submitData(lifecycle, stories)
        }
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            LoadingStateAdapter { adapter.retry() }
        )
        binding.rvStory.layoutManager = LinearLayoutManager(this)
    }

    private fun setupAction() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    mainViewModel.logout()
                    true
                }

                R.id.maps -> {
                    startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun showProgress(isLoading: Boolean) {
        if (isLoading) {
            binding.progress.visibility = View.VISIBLE
        } else {
            binding.progress.visibility = View.INVISIBLE
        }
    }

}