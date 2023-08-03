package com.codingtester.textscanner.presentation.ui.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codingtester.textscanner.R
import com.codingtester.textscanner.databinding.FragmentReviewTextBinding
import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.presentation.utils.Constants
import com.codingtester.textscanner.presentation.utils.ScannerHelper
import com.codingtester.textscanner.presentation.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewTextFragment : Fragment() {

    private val viewModel by viewModels<NoteViewModel>()
    private lateinit var binding: FragmentReviewTextBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewTextBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        arguments?.getString(Constants.TEXT_AFTER_REC)?.let { content ->
            binding.edtText.setText(content)
        }

        binding.clickSave.setOnClickListener { saveNoteToFirebase() }

        binding.clickCopy.setOnClickListener {
            ScannerHelper.copyToClipboard(
                binding.edtText.text.toString(),
                requireActivity()
            )
        }
    }

    private fun saveNoteToFirebase() {
        binding.clickSave.visibility = View.INVISIBLE
        binding.reviewProgressBar.visibility = View.VISIBLE
        val note = Note(content = binding.edtText.text.toString(),dateInMilliSecond =  System.currentTimeMillis())
        viewModel.addNote(note)

        // after save we don't need this screen so we will close it
        findNavController().popBackStack(R.id.reviewTextFragment, true)
        Toast.makeText(requireActivity(), getString(R.string.note_has_been_added_successfully), Toast.LENGTH_LONG).show()
    }
}