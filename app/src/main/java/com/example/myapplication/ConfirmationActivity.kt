package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        val fullName = intent.getStringExtra(EventRegistrationActivity.EXTRA_FULL_NAME).orEmpty()
        val phone = intent.getStringExtra(EventRegistrationActivity.EXTRA_PHONE).orEmpty()
        val email = intent.getStringExtra(EventRegistrationActivity.EXTRA_EMAIL).orEmpty()
        val eventType = intent.getStringExtra(EventRegistrationActivity.EXTRA_EVENT_TYPE).orEmpty()
        val eventDate = intent.getStringExtra(EventRegistrationActivity.EXTRA_EVENT_DATE).orEmpty()
        val gender = intent.getStringExtra(EventRegistrationActivity.EXTRA_GENDER).orEmpty()
        val imageUri = intent.getStringExtra(EventRegistrationActivity.EXTRA_IMAGE_URI)

        findViewById<TextView>(R.id.tvFullNameValue).text = fullName
        findViewById<TextView>(R.id.tvPhoneValue).text = phone
        findViewById<TextView>(R.id.tvEmailValue).text = email
        findViewById<TextView>(R.id.tvEventTypeValue).text = eventType
        findViewById<TextView>(R.id.tvEventDateValue).text = eventDate
        findViewById<TextView>(R.id.tvGenderValue).text = gender

        val imageView = findViewById<ImageView>(R.id.ivSelectedImage)
        if (!imageUri.isNullOrBlank()) {
            imageView.setImageURI(Uri.parse(imageUri))
        }

        findViewById<Button>(R.id.btnRegisterAnother).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}

