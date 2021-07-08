package com.example.appointmentbook.UI.Signup.User

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appointmentbook.Network.ApiAdapter
import com.example.appointmentbook.R
import com.example.appointmentbook.UI.Login.User.UserLoginActivity
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserSignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar?.hide()
        val btnUserSignUp = findViewById<Button>(R.id.btnUserSignup)
        btnUserSignUp.setOnClickListener {
            validateInput()
        }
    }

    private fun validateInput() {
        if (userName.text.toString().isEmpty()) {
            userName.error = "Please enter your name"
            userName.requestFocus()
            return
        }
        if (userBranch.text.toString().isEmpty()) {
            userBranch.error = "Please enter your Branch name"
            userBranch.requestFocus()
            return
        }
        if (userSemester.text.toString().isEmpty()) {
            userSemester.error = "Please enter your Semester"
            userSemester.requestFocus()
            return
        }
        if (userSignupEmail.text.toString().isEmpty()) {
            userSignupEmail.error = "Please enter your email ID"
            userSignupEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userSignupEmail.text.toString()).matches()) {
            userSignupEmail.error = "Please enter a valid email ID"
            userSignupEmail.requestFocus()
            return
        }
        if (userSignupPass.text.toString().isEmpty()) {
            userSignupPass.error = "Please enter your password"
            userSignupPass.requestFocus()
            return
        }
        if (userSignupPass.text.toString() != userConfirmPass.text.toString()) {
            userConfirmPass.error = "Password did not match"
            userConfirmPass.requestFocus()
            return
        }

        btnUserSignup.visibility = View.INVISIBLE
        signupProgressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val userBranch = userBranch.text.toString()
                val userSemester = userSemester.text.toString()
                val userName = userName.text.toString()

                val response = ApiAdapter.apiClient.signUpStudent(
                    userName,
                    userSignupEmail.text.toString(),
                    userSignupPass.text.toString(),
                    userBranch,
                    userSemester
                )
                if (response.isSuccessful && response.body() != null) {
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("tokenSharedPref", MODE_PRIVATE)
                    val edit: SharedPreferences.Editor = sharedPreferences.edit()
                    edit.putString("role", "student")
                    edit.putString("userBranch", userBranch)
                    edit.putString("userSemester", userSemester)
                    edit.putString("userName", userName)
                    edit.apply()
                    startActivity(Intent(this@UserSignUpActivity, UserLoginActivity::class.java))
                } else {
                    btnUserSignup.visibility = View.VISIBLE
                    signupProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@UserSignUpActivity,
                        response.message().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                btnUserSignup.visibility = View.VISIBLE
                signupProgressBar.visibility = View.INVISIBLE
                Toast.makeText(this@UserSignUpActivity, e.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

}


