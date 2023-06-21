package com.adiluhung.storyapp.ui.views.maps

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.adiluhung.storyapp.R
import com.adiluhung.storyapp.databinding.ActivityMapsBinding
import com.adiluhung.storyapp.ui.views.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "logged_in_user")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory(
            this, dataStore
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapStyle()

        with(mMap.uiSettings) {
            isZoomControlsEnabled = true
            isCompassEnabled = true
        }

        mapsViewModel.stories.observe(this@MapsActivity) { stories ->

            stories.forEach { story ->
                mMap.addMarker(
                    MarkerOptions().position(LatLng(story.lat, story.lon))
                        .title(story.name)
                        .snippet(story.description)
                )
            }

            // Zoom into the latest story
            val latestStory = stories[0]
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latestStory.lat, latestStory.lon),
                    10f
                )
            )
            Toast.makeText(this@MapsActivity, "Zoomed in to the latest story", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }


    private fun setupViewModel() {
        mapsViewModel.isLoading.observe(this) { isLoading ->
            showProgress(isLoading)

            if (!isLoading) {
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }

        mapsViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != "") {
                Toast.makeText(this@MapsActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAction() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showProgress(isLoading: Boolean) {
        if (isLoading) {
            binding.progress.visibility = View.VISIBLE
        } else {
            binding.progress.visibility = View.INVISIBLE
        }
    }

    companion object {
        private val TAG = MapsActivity::class.simpleName
    }
}