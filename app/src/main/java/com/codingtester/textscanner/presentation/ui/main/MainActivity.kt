package com.codingtester.textscanner.presentation.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingtester.textscanner.R
import com.codingtester.textscanner.databinding.ActivityMainBinding
import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.presentation.ui.review.ReviewActivity
import com.codingtester.textscanner.presentation.viewmodel.NoteViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnClickNote {

    private val viewModel by viewModels<NoteViewModel>()
    private lateinit var binding: ActivityMainBinding
    private val noteAdapter by lazy { NoteAdapter(this) }
    private val numbers: MutableList<String> = emptyList<String>().toMutableList()

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

        binding.recyclerNotes.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        viewModel.noteState.observe(this) { notes ->
            if (notes.isEmpty()) {
                setEmptyData()
            } else {
                setNotesData(notes)
            }
        }

        // when user finish crop image , image will convert to uri
        // then take this uri and pass it to function to get text from it
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                getTextFromUri(uri)
            }
        }

        // before take photo we will check permission then open camera
//        binding.btnTakePhoto.setOnClickListener {
//            if (isCameraPermissionGranted()) {
//                cropActivityResultLauncher.launch(null)
//            } else {
//                requestCameraPermission()
//            }
//        }
    }

    private fun setNotesData(notes: List<Note>) {
        binding.recyclerNotes.visibility = View.VISIBLE
        binding.imgEmpty.visibility = View.GONE
        noteAdapter.updatePopularList(notes)
    }

    private fun setEmptyData() {
        binding.recyclerNotes.visibility = View.GONE
        binding.imgEmpty.visibility = View.VISIBLE
    }

    override fun onClickToDelete(note: Note) {
        AlertDialog.Builder(this@MainActivity)
            .setTitle(getString(R.string.delete_note))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_note))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                lifecycleScope.launch {
                    viewModel.deleteNote(note)
                    dialog.dismiss()
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.note_removed_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onClickToSaveFile(note: Note) {
        val dialog = SaveFileDialog()

        val args = Bundle()
        args.putLong(NOTE_DATE, note.dateInMilliSecond)
        args.putString(NOTE_TITLE, note.content)

        dialog.arguments = args
        dialog.show(this.supportFragmentManager, getString(R.string.savefile))
    }

    private fun getTextFromUri(imageUri: Uri) {

        val inputImage = InputImage.fromFilePath(this@MainActivity, imageUri)

        //creating TextRecognizer instance
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        //process the image
        recognizer.process(inputImage)
            .addOnSuccessListener { textAfterRecognize ->
                numbers.clear()
                textAfterRecognize.textBlocks.forEach { block: Text.TextBlock ->
                    Log.e("TAG", "block --> :  ${block.text}")
                    getNumbersFromTextBlock(block.text)
                }
                Log.e("TAG", "final list --> :  $numbers")
                navigateToReviewFragment(textAfterRecognize.text)
            }.addOnFailureListener {
                Toast.makeText(
                    this@MainActivity,
                    "recognition module not be downloaded," +
                            " please update your Google Play Service on your device",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun getNumbersFromTextBlock(text: String) {
        Log.e("TAG", "block to list -- > : ${stringToWords(text)}")
        numbers += stringToWords(text)
    }

    private fun stringToWords(text : String): List<String> {
        return text.trim()
            .splitToSequence(' ')
            .filter { it.isNotEmpty() }
            .toList()
    }

    // after get text from image we will move to review screen
    // to show text and save it on firebase
    private fun navigateToReviewFragment(text: String) {
        if (text.isBlank() || text.isEmpty()) {
            // if no text found on image
            Toast.makeText(
                this@MainActivity,
                getString(R.string.failed_to_recognize_text_try_again),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // if found text on image
            Intent(this, ReviewActivity::class.java).apply {
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

    companion object {
        const val NOTE_DATE = "NOTE_DATE"
        const val NOTE_TITLE = "NOTE_TITLE"
        const val TEXT_AFTER_REC = "TEXT_AFTER_REC"
    }
}