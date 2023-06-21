package com.adiluhung.storyapp.ui.customViews

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class EmailEditText : TextInputEditText {
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                error = if (!isEmailValid(email)) {
                    "Invalid email address"
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }

        })
    }

    fun isEmailValid(email: String): Boolean {
        return email.matches(emailPattern.toRegex())
    }
}