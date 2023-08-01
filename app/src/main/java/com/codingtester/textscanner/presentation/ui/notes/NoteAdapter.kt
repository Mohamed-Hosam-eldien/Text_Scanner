package com.codingtester.textscanner.presentation.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingtester.textscanner.R
import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.presentation.utils.ScannerHelper.copyToClipboard
import com.codingtester.textscanner.presentation.utils.ScannerHelper.getDateFromMille

class NoteAdapter(
    private val onClickNote: OnClickNote
) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private var noteList: List<Note> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.note_layout_linear, parent, false)
        )
    }

    override fun getItemCount(): Int = noteList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = noteList[position]
        holder.txtNoteTitle.text = note.content
        holder.txtNoteDate.text = getDateFromMille(note.dateInMilliSecond)

        holder.clickToSave.setOnClickListener {
            onClickNote.onClickToSaveFile(note)
        }
        holder.clickToDelete.setOnClickListener {
            onClickNote.onClickToDelete(note)
        }
        holder.imgCopy.setOnClickListener {
            copyToClipboard(note.content, holder.itemView.context)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNoteTitle: TextView = itemView.findViewById(R.id.txtNoteTitle)
        val txtNoteDate: TextView = itemView.findViewById(R.id.txtNoteDate)
        val imgCopy: ImageView = itemView.findViewById(R.id.imgCopy)
        val clickToSave: CardView = itemView.findViewById(R.id.clickSaveToWord)
        val clickToDelete: CardView = itemView.findViewById(R.id.clickDelete)
    }

    fun updatePopularList(newBoards: List<Note>) {
        val recipeDiffUtil = NoteDiffUtil(noteList, newBoards)
        val diffResult = DiffUtil.calculateDiff(recipeDiffUtil)
        noteList = newBoards
        diffResult.dispatchUpdatesTo(this)
    }
}