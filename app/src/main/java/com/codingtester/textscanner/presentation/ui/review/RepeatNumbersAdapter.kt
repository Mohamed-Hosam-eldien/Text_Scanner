package com.codingtester.textscanner.presentation.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codingtester.textscanner.R

class RepeatNumbersAdapter(
    private val numberList: Map<String, Int>
) : RecyclerView.Adapter<RepeatNumbersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.repeat_layout, parent, false)
        )
    }
    override fun getItemCount(): Int = numberList.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = numberList.keys.elementAt(position)
        holder.txtNumber.text = key
        holder.txtRepeat.text = numberList[key].toString()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNumber: TextView = itemView.findViewById(R.id.txtNumberLayout)
        val txtRepeat: TextView = itemView.findViewById(R.id.txtRepeatLayout)
    }
}