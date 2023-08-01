package com.codingtester.textscanner.presentation.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.codingtester.textscanner.R
import com.codingtester.textscanner.databinding.ActivityMainBinding
import com.codingtester.textscanner.presentation.ui.notes.NotesActivity
import com.codingtester.textscanner.presentation.ui.review.ReviewNumbersActivity
import com.codingtester.textscanner.presentation.ui.review.ReviewTextActivity
import com.codingtester.textscanner.presentation.utils.Constants.NUMBERS_LIST
import com.codingtester.textscanner.presentation.utils.Constants.TEXT_AFTER_REC
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val numberList: ArrayList<String> = ArrayList()
    private lateinit var selectedType: ScannerType

    // use ActivityResultContract because onActivityResult is Deprecated
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage
                .activity()
                .getIntent(this@MainActivity)
                .setType("image/*")
                .setAction(MediaStore.ACTION_IMAGE_CAPTURE)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // when user finish crop image, image will convert to uri
        // then take this uri and pass it to function to get the text from it
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                getTextFromUri(uri)
            }
        }

        binding.cardText.setOnClickListener {
            selectedType = ScannerType.TEXT
            openCamera()
        }

        binding.cardNumber.setOnClickListener {
            selectedType = ScannerType.NUMBERS
            openCamera()
        }

        binding.imgNotes.setOnClickListener {
            Intent(this, NotesActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            cropActivityResultLauncher.launch(null)
        } else {
            requestCameraPermission()
        }
    }

    private fun getTextFromUri(imageUri: Uri) {
        val inputImage = InputImage.fromFilePath(this@MainActivity, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener { textAfterRecognize ->
                when(selectedType) {
                    ScannerType.TEXT -> {
                        navigateToTextReview(textAfterRecognize.text)
                    }
                    ScannerType.NUMBERS -> {
                        analysisScannerNumbers(textAfterRecognize)
                    }
                }
            }.addOnFailureListener {
                showMessage(getString(R.string.please_update_your_Google_Play_Service_on_your_device))
            }
    }

    private fun analysisScannerNumbers(textAfterRecognize: Text?) {
        numberList.clear()
        textAfterRecognize?.textBlocks?.forEach { block ->
            numberList += convertTextToList(block.text)
        }
        navigateToNumbersReview(numberList)
    }

    private fun navigateToNumbersReview(numberList: ArrayList<String>) {
        if (numberList.isEmpty()) {
            showMessage(getString(R.string.failed_to_recognize_text_try_again))
        } else {
            Intent(this, ReviewNumbersActivity::class.java).apply {
                this.putStringArrayListExtra(NUMBERS_LIST, numberList)
                startActivity(this)
            }
        }
    }

    // this function to separate each number in [block of text] and convert it to [list] of numbers
    // "22 33 44" --> [22,33,44]
    private fun convertTextToList(text : String): List<String> {
        return text.trim()
            .splitToSequence(' ')
            .filter { it.isNotEmpty() && it.isDigitsOnly() }
            .toList()
    }

    private fun navigateToTextReview(text: String) {
        if (text.isBlank() || text.isEmpty()) {
            showMessage(getString(R.string.failed_to_recognize_text_try_again))
        } else {
            Intent(this, ReviewTextActivity::class.java).apply {
                this.putExtra(TEXT_AFTER_REC, text)
                startActivity(this)
            }
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.CAMERA
            ), 100
        )
    }

    private fun isCameraPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this@MainActivity,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    enum class ScannerType {
        TEXT, NUMBERS
    }
}