package com.codingtester.textscanner.presentation.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codingtester.textscanner.R
import com.codingtester.textscanner.databinding.FragmentHomeBinding
import com.codingtester.textscanner.presentation.utils.Constants
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val numberList: ArrayList<String> = ArrayList()
    private lateinit var selectedType: ScannerType

    // use ActivityResultContract because onActivityResult is Deprecated
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage
                .activity()
                .getIntent(requireContext())
                .setType("image/*")
                .setAction(MediaStore.ACTION_IMAGE_CAPTURE)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            cropActivityResultLauncher.launch(null)
        } else {
            requestCameraPermission()
        }
    }

    private fun getTextFromUri(imageUri: Uri) {
        val inputImage = InputImage.fromFilePath(requireActivity(), imageUri)
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
            val bundle = Bundle()
            bundle.putStringArrayList(Constants.NUMBERS_LIST, numberList)
            findNavController().navigate(R.id.action_homeFragment_to_reviewNumbersFragment, bundle)
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
            val bundle = Bundle()
            bundle.putString(Constants.TEXT_AFTER_REC, text)
            findNavController().navigate(R.id.action_homeFragment_to_reviewTextFragment, bundle)
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.CAMERA
            ), 100
        )
    }

    private fun isCameraPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            requireActivity(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    enum class ScannerType {
        TEXT, NUMBERS
    }

}