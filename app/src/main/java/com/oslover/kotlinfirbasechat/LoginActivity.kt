package com.oslover.kotlinfirbasechat

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            performLogin()
        }

        back_to_registration_login.setOnClickListener {
            finish()
        }
    }

    private fun performLogin() {
        val email = email_text_login.text.toString()
        val password = password_text_login.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this, "Successfully logged in user with uid: ${it.result.user.uid}", Toast.LENGTH_SHORT).show()
                    showLatestMessage()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to login user: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun showLatestMessage() {
        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}