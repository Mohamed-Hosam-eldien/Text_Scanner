package com.codingtester.textscanner.presentation.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codingtester.textscanner.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ScannerHelper {
    fun getDateFromMille(noteDate: Long?): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy - hh:mm", Locale.getDefault())
        return noteDate?.let { formatter.format(Date(noteDate)) }.toString()
    }
    fun copyToClipboard(text: String, context: Context) {
        val clipboard: ClipboardManager = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        val textToCopy = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(textToCopy)
        Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }
}