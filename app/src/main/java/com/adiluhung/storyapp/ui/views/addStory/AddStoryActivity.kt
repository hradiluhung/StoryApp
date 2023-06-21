package com.adiluhung.storyapp.ui.views.addStory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.adiluhung.storyapp.R
import com.adiluhung.storyapp.databinding.ActivityAddStoryBinding
import com.adiluhung.storyapp.ui.views.ViewModelFactory
import com.adiluhung.storyapp.ui.views.main.MainActivity
import com.adiluhung.storyapp.utils.createCustomTempFile
import com.adiluhung.storyapp.utils.reduceFile
import com.adiluhung.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "logged_in_user")

class AddStoryActivity : AppCompatActivity() {
    private var binding: ActivityAddStoryBinding? = null
    private lateinit var currentPhotoPath: String
    private var isImageProvided: Boolean = false
    private var storyImage: File? = null
    private var isWithLocation: Boolean = true

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactory(
            this, dataStore
        )
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                reduceFile(file)
                storyImage = file
                isImageProvided = true
                binding?.ivStory?.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage = result?.data?.data as Uri
            selectedImage.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                reduceFile(myFile)
                isImageProvided = true
                storyImage = myFile
                binding?.ivStory?.setImageURI(uri)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupAction()
        setupViewModel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                currentLocation = location
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupViewModel() {
        addStoryViewModel.isLoading.observe(this) { isLoading ->
            showProgress(isLoading)
        }

        addStoryViewModel.isError.observe(this) { isError ->
            addStoryViewModel.message.observe(this) { message ->
                if (!isError) {
                    startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding?.btnCamera?.setOnClickListener { startTakePhoto() }
        binding?.btnGallery?.setOnClickListener { startGallery() }
        binding?.btnReset?.setOnClickListener {
            // resetField()
            storyImage?.toString()?.let { it1 -> Log.d("AddStoryActivity", it1) }
        }
        binding?.btnUpload?.setOnClickListener { uploadStory() }
        binding?.topAppBar?.setNavigationOnClickListener { finish() }
        binding?.checkboxLocation?.setOnCheckedChangeListener { _, isChecked ->
            isWithLocation = isChecked
        }
    }

    private fun uploadStory() {
        val description = binding?.etDescription?.text.toString()
        if (!isImageProvided) {
            Toast.makeText(this, getString(R.string.image_is_required), Toast.LENGTH_SHORT).show()
        } else if (description == "") {
            Toast.makeText(this, getString(R.string.description_is_required), Toast.LENGTH_SHORT)
                .show()
        } else {
            if (isWithLocation) {
                addStoryViewModel.uploadImage(storyImage, description, currentLocation)
            } else {
                addStoryViewModel.uploadImage(storyImage, description)
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launchIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.adiluhung.storyapp",
                it
            )

            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launchIntentCamera.launch(intent)
        }
    }

    private fun resetField() {
        isImageProvided = false
        binding?.ivStory?.setImageDrawable(
            ContextCompat.getDrawable(
                this@AddStoryActivity,
                R.drawable.placeholder_image
            )
        )
        binding?.etDescription?.text?.clear()
    }

    private fun showProgress(isLoading: Boolean) {
        if (isLoading) {
            binding?.progress?.visibility = View.VISIBLE
        } else {
            binding?.progress?.visibility = View.INVISIBLE
        }
    }
}