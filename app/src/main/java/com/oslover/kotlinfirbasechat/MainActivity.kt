package com.oslover.kotlinfirbasechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            var email = email_text_register.text.toString()
            var password = password_text_register.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,  password).addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("MainActivity", "Successfully created user with uid: ${it.result.user.uid}")
            }
        }

        already_have_account_text.isClickable = true

        already_have_account_text.setOnClickListener {
            Log.d("MainActivity", "Try to show login Activity")

            //launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
