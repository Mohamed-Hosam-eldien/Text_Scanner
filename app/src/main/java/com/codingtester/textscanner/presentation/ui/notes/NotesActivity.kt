package com.codingtester.textscanner.presentation.ui.notes

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingtester.textscanner.R
import com.codingtester.textscanner.databinding.ActivityNotesBinding
import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.presentation.ui.review.ReviewTextActivity
import com.codingtester.textscanner.presentation.utils.Constants
import com.codingtester.textscanner.presentation.utils.Constants.NOTE_DATE
import com.codingtester.textscanner.presentation.utils.Constants.NOTE_TITLE
import com.codingtester.textscanner.presentation.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class NotesActivity : AppCompatActivity(), OnClickNote {

    private lateinit var binding: ActivityNotesBinding
    private val noteAdapter by lazy { NoteAdapter(this) }
    private val viewModel by viewModels<NoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNotesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerNotes.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(this@NotesActivity)
        }

        viewModel.noteState.observe(this) { notes ->
            if (notes.isEmpty()) {
                setEmptyData()
            } else {
                setNotesData(notes)
            }
        }
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
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_note))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_note))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                lifecycleScope.launch {
                    viewModel.deleteNote(note)
                    dialog.dismiss()
                    Toast.makeText(
                        this@NotesActivity,
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
    override fun onClickToCard(note: Note) {
        Intent(this, ReviewTextActivity::class.java).apply {
            this.putExtra(Constants.TEXT_AFTER_REC, note.content)
            startActivity(this)
        }
    }
}