package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class EventRegistrationActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var spinnerEventType: Spinner
    private lateinit var etEventDate: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var imagePreview: ImageView
    private lateinit var checkboxTerms: CheckBox

    private var selectedDate: String? = null
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                imagePreview.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_registration)

        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        spinnerEventType = findViewById(R.id.spinnerEventType)
        etEventDate = findViewById(R.id.etEventDate)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        imagePreview = findViewById(R.id.imagePreview)
        checkboxTerms = findViewById(R.id.checkboxTerms)

        setupEventTypeSpinner()

        etEventDate.setOnClickListener { showDatePicker() }
        etEventDate.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) showDatePicker() }

        findViewById<Button>(R.id.btnChooseImage).setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            submitForm()
        }
    }

    private fun setupEventTypeSpinner() {
        val options = resources.getStringArray(R.array.event_type_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEventType.adapter = adapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                etEventDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1_000
        datePickerDialog.show()
    }

    private fun submitForm() {
        etFullName.error = null
        etPhone.error = null
        etEmail.error = null
        etEventDate.error = null

        val fullName = etFullName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val eventType = spinnerEventType.selectedItem.toString()
        val genderId = radioGroupGender.checkedRadioButtonId

        if (fullName.length < 3) {
            etFullName.error = getString(R.string.error_full_name)
            etFullName.requestFocus()
            return
        }

        if (!phone.matches(Regex("^[0-9]{10,15}$"))) {
            etPhone.error = getString(R.string.error_phone)
            etPhone.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.error_email)
            etEmail.requestFocus()
            return
        }

        if (spinnerEventType.selectedItemPosition == 0) {
            Toast.makeText(this, getString(R.string.error_event_type), Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate.isNullOrBlank()) {
            etEventDate.error = getString(R.string.error_date)
            etEventDate.requestFocus()
            return
        }

        if (genderId == -1) {
            Toast.makeText(this, getString(R.string.error_gender), Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, getString(R.string.error_image), Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkboxTerms.isChecked) {
            Toast.makeText(this, getString(R.string.error_terms), Toast.LENGTH_SHORT).show()
            return
        }

        val gender = findViewById<RadioButton>(genderId).text.toString()

        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_title)
            .setMessage(R.string.confirm_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.confirm) { _, _ ->
                val intent = Intent(this, ConfirmationActivity::class.java).apply {
                    putExtra(EXTRA_FULL_NAME, fullName)
                    putExtra(EXTRA_PHONE, phone)
                    putExtra(EXTRA_EMAIL, email)
                    putExtra(EXTRA_EVENT_TYPE, eventType)
                    putExtra(EXTRA_EVENT_DATE, selectedDate)
                    putExtra(EXTRA_GENDER, gender)
                    putExtra(EXTRA_IMAGE_URI, selectedImageUri.toString())
                }
                startActivity(intent)
            }
            .show()
    }

    companion object {
        const val EXTRA_FULL_NAME = "extra_full_name"
        const val EXTRA_PHONE = "extra_phone"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_EVENT_TYPE = "extra_event_type"
        const val EXTRA_EVENT_DATE = "extra_event_date"
        const val EXTRA_GENDER = "extra_gender"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}

