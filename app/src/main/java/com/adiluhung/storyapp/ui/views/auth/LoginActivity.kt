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
import com.adiluhung.storyapp.databinding.ActivityLoginBinding
import com.adiluhung.storyapp.ui.views.ViewModelFactory
import com.adiluhung.storyapp.ui.views.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "logged_in_user")

class LoginActivity : AppCompatActivity() {
    private var binding: ActivityLoginBinding? = null
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(
            this, dataStore
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
                    if (!isError) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        message?.let {
                            Toast.makeText(
                                this@LoginActivity, message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding?.btnLogin?.setOnClickListener {
            val email = binding?.etEmail?.text.toString()
            val password = binding?.etPassword?.text.toString()

            when {
                email.isEmpty() -> {
                    binding?.etEmail?.error = "Masukkan email"
                }

                password.isEmpty() -> {
                    binding?.etPassword?.error = "Masukkan password"
                }

                else -> {
                    if (binding?.etEmail?.error == null && binding?.etPassword?.error == null) {
                        authViewModel.login(email, password)
                    }
                }
            }
        }

        binding?.btnRegister?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding?.btnRegister?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
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
        val tvLoginAlpha = ObjectAnimator.ofFloat(binding?.tvLogin, View.ALPHA, 1f).apply {
            duration = 1000
        }
        val tvLoginTranslateY =
            ObjectAnimator.ofFloat(binding?.tvLogin, View.TRANSLATION_Y, 40f, 0f).apply {
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
        val btnLoginAlpha = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).apply {
            duration = 1000
        }
        val btnLoginTranslateY =
            ObjectAnimator.ofFloat(binding?.btnLogin, View.TRANSLATION_Y, 40f, 0f).apply {
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
                tvLoginAlpha,
                tvLoginTranslateY,
                etEmailAlpha,
                etEmailTranslateY,
                etPasswordAlpha,
                etPasswordTranslateY,
                btnLoginAlpha,
                btnLoginTranslateY,
                layoutAdditionalAlpha,
                layoutAdditionalTranslateY
            )
        }.start()
    }
}

