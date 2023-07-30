package com.codingtester.textscanner.presentation.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.codingtester.textscanner.R
import com.codingtester.textscanner.databinding.SaveFileLayoutBinding
import com.codingtester.textscanner.presentation.utils.ScannerHelper.getDateFromMille
import dagger.hilt.android.AndroidEntryPoint
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

@AndroidEntryPoint
class SaveFileDialog : DialogFragment() {

    private lateinit var binding: SaveFileLayoutBinding

    private var filePath: String = ""

    // this variable to know with file type user selected
    private var fileExe = TXT

    private var noteDate: Long? = null
    private var noteTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get data of note that user selected to save
        noteDate = arguments?.getLong(MainActivity.NOTE_DATE)
        noteTitle = arguments?.getString(MainActivity.NOTE_TITLE)

        // this path that file will save on it
        filePath = Environment.getExternalStoragePublicDirectory(
            "$DIRECTORY_DOCUMENTS/${requireContext().getString(R.string.app_name)}"
        ).path
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SaveFileLayoutBinding.inflate(inflater, container, false)

        // change variable when user selected file type
        binding.radioGroupFile.setOnCheckedChangeListener { _, radioID ->
            when (radioID) {
                R.id.radioTxt -> {
                    fileExe = TXT
                }

                R.id.radioWord -> {
                    fileExe = WORD
                }
            }
        }

        binding.btnSave.setOnClickListener {
            handleSaveFileProcess()
        }
        return binding.root
    }

    private fun handleSaveFileProcess() {
        val fileName = binding.edtFileName.text.toString()

        if (fileName.isNotEmpty()) {
            // if Android api >= Q we don't need to permission but else we need to permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFileInDevice(fileName)
            } else {
                //check permission before save file
                if (isStoragePermissionGranted()) {
                    saveFileInDevice(fileName)
                } else {
                    requestPermission()
                }
            }
        } else {
            binding.edtFileName.error = requireActivity().getString(R.string.required)
        }
    }

    private fun saveFileInDevice(fileName: String) {
        binding.btnSave.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
        // check which file type user selected
        when (fileExe) {
            TXT -> {
                saveToTxtFile(fileName)
            }
            WORD -> {
                saveToWordFile(fileName)
            }
        }
    }

    private fun saveToTxtFile(fileName: String) {
        try {
            makeFileDir()

            val print = PrintWriter("$filePath/${fileName}.TXT")
            print.write("Created in : ${getDateFromMille(noteDate)} \nNote Title :  $noteTitle")
            print.close()
            Toast.makeText(
                requireContext(),
                requireActivity().getString(R.string.file_saved_successfully),
                Toast.LENGTH_SHORT
            ).show()
            dialog!!.dismiss()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveToWordFile(fileName: String) {
        addParagraph(fileName, createWordDoc())
    }
    private fun createWordDoc(): XWPFDocument {
        return XWPFDocument()
    }
    private fun makeFileDir(): File {
        // make direction of our folder to save files in it
        val file = File(filePath)
        try {
            file.mkdirs()
        } catch (e: Exception) {
            Log.e("TAG", "File Exception : ${e.message}")
        }
        return file
    }

    private fun addParagraph(fileName: String, targetDoc: XWPFDocument) {
        //creating a paragraph in our document and setting its alignment
        val paragraph = targetDoc.createParagraph()
        paragraph.alignment = ParagraphAlignment.LEFT

        //creating a run for adding text
        val sentenceRun = paragraph.createRun()
        val sentenceRun2 = paragraph.createRun()

        //format the text
        sentenceRun.isBold = false
        sentenceRun.fontSize = FONT_SIZE
        sentenceRun.fontFamily = FONT_NAME
        sentenceRun.setText("Created in : ${getDateFromMille(noteDate)}")

        sentenceRun2.isBold = false
        sentenceRun2.fontSize = FONT_SIZE
        sentenceRun2.fontFamily = FONT_NAME
        sentenceRun2.setText("Note Title : $noteTitle")

        //add a sentence break
        sentenceRun.addBreak()
        sentenceRun2.addBreak()

        saveToWordDoc(fileName, targetDoc)
    }

    private fun saveToWordDoc(fileName: String, targetDoc: XWPFDocument) {
        // set name of document that found on path and save file by output stream
        val wordFile = File(makeFileDir(), "${fileName}.docx")
        try {
            val fileOut = FileOutputStream(wordFile)
            targetDoc.write(fileOut)
            fileOut.close()
            Toast.makeText(
                requireContext(),
                requireActivity().getString(R.string.file_saved_successfully),
                Toast.LENGTH_SHORT
            )
                .show()
            dialog!!.dismiss()
        } catch (e: Exception) {
            binding.btnSave.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 200
        )
    }
    private fun isStoragePermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }
    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    companion object {
        const val FONT_NAME = "Comic Sans MS"
        const val FONT_SIZE = 14
        const val TXT = "txt"
        const val WORD = "word"
    }
}