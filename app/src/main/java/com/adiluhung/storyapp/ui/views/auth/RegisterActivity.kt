package com.adiluhung.storyapp.ui.views.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.adiluhung.storyapp.databinding.ActivityRegisterBinding
import com.adiluhung.storyapp.ui.views.ViewModelFactory


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "logged_in_user")

class RegisterActivity : AppCompatActivity() {
    private var binding: ActivityRegisterBinding? = null
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(
            this, dataStore
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupViewModel()
        setupAction()
        playAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupViewModel() {
        authViewModel.isLoading.observe(this) { isLoading ->
            showProgress(isLoading)
        }

        authViewModel.isError.observe(this) { isError ->
            authViewModel.message.observe(this) { message ->
                isError?.let {
                    message?.let {
                        if (!isError) {
                            Toast.makeText(
                                this@RegisterActivity, message,
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@RegisterActivity, message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding?.btnRegister?.setOnClickListener {
            val name = binding?.etName?.text.toString()
            val email = binding?.etEmail?.text.toString()
            val password = binding?.etPassword?.text.toString()

            when {
                name.isEmpty() -> {
                    binding?.etEmail?.error = "Masukkan nama"
                }

                email.isEmpty() -> {
                    binding?.etEmail?.error = "Masukkan email"
                }

                password.isEmpty() -> {
                    binding?.etPassword?.error = "Masukkan password"
                }

                else -> {
                    if (binding?.etEmail?.error == null && binding?.etPassword?.error == null) {
                        authViewModel.register(name, email, password)
                    }
                }
            }
        }

        binding?.btnLogin?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding?.btnLogin?.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun showProgress(isLoading: Boolean) {
        if (isLoading) {
            binding?.progress?.visibility = View.VISIBLE
        } else {
            binding?.progress?.visibility = View.INVISIBLE
        }
    }

    private fun playAnimation() {
        val tvRegisterAlpha = ObjectAnimator.ofFloat(binding?.tvRegister, View.ALPHA, 1f).apply {
            duration = 1000
        }
        val tvRegisterTranslateY =
            ObjectAnimator.ofFloat(binding?.tvRegister, View.TRANSLATION_Y, 40f, 0f).apply {
                duration = 1000
            }
        val etNameAlpha = ObjectAnimator.ofFloat(binding?.contEtName, View.ALPHA, 1f).apply {
            duration = 1000
        }
        val etNameTranslateY =
            ObjectAnimator.ofFloat(binding?.contEtName, View.TRANSLATION_Y, 40f, 0f).apply {
                duration = 1000
            }
        val etEmailAlpha = ObjectAnimator.ofFloat(binding?.contEtEmail, View.ALPHA, 1f).apply {
            duration = 1000
        }
        val etEmailTranslateY =
            ObjectAnimator.ofFloat(binding?.contEtEmail, View.TRANSLATION_Y, 40f, 0f).apply {
                duration = 1000
            }
        val etPasswordAlpha =
            ObjectAnimator.ofFloat(binding?.contEtPassword, View.ALPHA, 1f).apply {
                duration = 1000
            }
        val etPasswordTranslateY =
            ObjectAnimator.ofFloat(binding?.contEtPassword, View.TRANSLATION_Y, 40f, 0f).apply {
                duration = 1000
            }
        val btnRegisterAlpha = ObjectAnimator.ofFloat(binding?.btnRegister, View.ALPHA, 1f).apply {
            duration = 1000
        }
        val btnRegisterTranslateY =
            ObjectAnimator.ofFloat(binding?.btnRegister, View.TRANSLATION_Y, 40f, 0f).apply {
                duration = 1000
            }
        val layoutAdditionalAlpha =
            ObjectAnimator.ofFloat(binding?.layoutAdditionalAction, View.ALPHA, 1f).apply {
                duration = 1000
            }
        val layoutAdditionalTranslateY =
            ObjectAnimator.ofFloat(binding?.layoutAdditionalAction, View.TRANSLATION_Y, 40f, 0f)
                .apply {
                    duration = 1000
                }

        AnimatorSet().apply {
            playTogether(
                tvRegisterAlpha,
                tvRegisterTranslateY,
                etNameAlpha,
                etNameTranslateY,
                etEmailAlpha,
                etEmailTranslateY,
                etPasswordAlpha,
                etPasswordTranslateY,
                btnRegisterAlpha,
                btnRegisterTranslateY,
                layoutAdditionalAlpha,
                layoutAdditionalTranslateY
            )
        }.start()
    }
}