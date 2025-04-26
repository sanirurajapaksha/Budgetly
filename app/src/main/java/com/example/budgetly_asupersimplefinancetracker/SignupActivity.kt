package com.example.budgetly_asupersimplefinancetracker

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.Toast

class SignupActivity : AppCompatActivity() {
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views
        nameInputLayout = findViewById(R.id.nameInputLayout)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        signupButton = findViewById(R.id.signupButton)

        signupButton.setOnClickListener {
            if (validateInputs()) {
                registerUser()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate name
        val name = nameInput.text.toString().trim()
        if (name.isEmpty()) {
            nameInputLayout.error = "Name is required"
            isValid = false
        } else if (name.length < 3) {
            nameInputLayout.error = "Name must be at least 3 characters"
            isValid = false
        } else {
            nameInputLayout.error = null
        }

        // Validate email
        val email = emailInput.text.toString().trim()
        if (email.isEmpty()) {
            emailInputLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Please enter a valid email address"
            isValid = false
        } else {
            emailInputLayout.error = null
        }

        // Validate password
        val password = passwordInput.text.toString()
        if (password.isEmpty()) {
            passwordInputLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordInputLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else if (!password.matches(".*[A-Z].*".toRegex())) {
            passwordInputLayout.error = "Password must contain at least one uppercase letter"
            isValid = false
        } else if (!password.matches(".*[a-z].*".toRegex())) {
            passwordInputLayout.error = "Password must contain at least one lowercase letter"
            isValid = false
        } else if (!password.matches(".*[0-9].*".toRegex())) {
            passwordInputLayout.error = "Password must contain at least one number"
            isValid = false
        } else {
            passwordInputLayout.error = null
        }

        return isValid
    }

    private fun registerUser() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()

        // Save user data and auth state in SharedPreferences
        getSharedPreferences("user_prefs", MODE_PRIVATE).edit().apply {
            putBoolean("is_logged_in", true)
            putString("user_name", name)
            putString("user_email", email)
            apply()
        }

        // Show success message
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

        // Navigate to MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity() // Clear all previous activities including onboarding
    }
} 