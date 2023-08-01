package com.codingtester.textscanner.presentation.ui.notes

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingtester.textscanner.R
import com.codingtester.textscanner.databinding.FragmentNotesBinding
import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.presentation.utils.Constants
import com.codingtester.textscanner.presentation.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesFragment : Fragment(), OnClickNote {

    private lateinit var binding: FragmentNotesBinding
    private val noteAdapter by lazy { NoteAdapter(this) }
    private val viewModel by viewModels<NoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerNotes.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }

        viewModel.noteState.observe(viewLifecycleOwner) { notes ->
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
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.delete_note))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_note))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                lifecycleScope.launch {
                    viewModel.deleteNote(note)
                    dialog.dismiss()
                    Toast.makeText(
                        requireActivity(),
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
        args.putLong(Constants.NOTE_DATE, note.dateInMilliSecond)
        args.putString(Constants.NOTE_TITLE, note.content)

        dialog.arguments = args
        dialog.show(requireActivity().supportFragmentManager, getString(R.string.savefile))
    }
    override fun onClickToCard(note: Note) {
        val bundle = Bundle()
        bundle.putString(Constants.TEXT_AFTER_REC, note.content)
        findNavController().navigate(R.id.action_notesFragment_to_reviewTextFragment, bundle)
    }
}