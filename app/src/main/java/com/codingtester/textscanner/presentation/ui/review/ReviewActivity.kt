package com.codingtester.textscanner.presentation.ui.review

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.codingtester.textscanner.R
import com.codingtester.textscanner.databinding.ActivityReviewBinding
import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.presentation.ui.main.MainActivity
import com.codingtester.textscanner.presentation.utils.ScannerHelper.copyToClipboard
import com.codingtester.textscanner.presentation.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewActivity : AppCompatActivity() {

    private val viewModel by viewModels<NoteViewModel>()
    private lateinit var binding: ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(MainActivity.TEXT_AFTER_REC)?.let { content ->
            binding.edtText.setText(content)
        }

        binding.clickSave.setOnClickListener { saveNoteToFirebase() }

        binding.clickCopy.setOnClickListener { copyToClipboard(binding.edtText.text.toString(), this) }
    }

    private fun saveNoteToFirebase() {
        binding.clickSave.visibility = View.GONE
        binding.reviewProgressBar.visibility = View.VISIBLE
        val note = Note(content = binding.edtText.text.toString(),dateInMilliSecond =  System.currentTimeMillis())
        viewModel.addNote(note)

        // after save we don't need this screen so we will close it
        Toast.makeText(this, getString(R.string.note_has_been_added_successfully), Toast.LENGTH_LONG).show()
        finish()
    }
}