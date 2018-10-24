package com.oslover.kotlinfirbasechat.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.storage.FirebaseStorage
import com.oslover.kotlinfirbasechat.messages.LatestMessagesActivity
import com.oslover.kotlinfirbasechat.R
import com.oslover.kotlinfirbasechat.models.User
import java.util.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()
        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text.setOnClickListener {
            Log.d("RegisterActivity", "Try to show login Activity")

            //launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        button_select_photo_register.setOnClickListener {
            selectPhoto()
        }
    }

    private fun performRegister() {
        var email = email_text_register.text.toString()
        var password = password_text_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return
        }

        registerFirebase(email, password)
    }

/*
* Select User Photo
* */
    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    private var selectedPhotoUri: Uri? = null

    //get selected image.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check what the selected image was...

            val uri = data.data
            selectedPhotoUri = uri

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            profile_image_register.setImageBitmap(bitmap)

            button_select_photo_register.alpha = 0f
        }
    }

/*
*   Firebase Storage communication
* */

    private fun registerFirebase(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this, "Successfully created user with uid: ${it.result.user.uid}", Toast.LENGTH_SHORT).show()
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create User: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri == null) {
            Toast.makeText(this, "Please select a photo.", Toast.LENGTH_SHORT).show()
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("RegisterActivity","File location: $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity","Failed to save image")
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_text_register.text.toString(), profileImageUrl)
        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity","User saved to FB DB")
                    showLatestMessage()
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity","Failed to save user")
                }
    }

    private fun showLatestMessage() {
        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
