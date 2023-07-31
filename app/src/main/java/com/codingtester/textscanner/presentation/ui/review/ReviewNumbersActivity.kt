package com.codingtester.textscanner.presentation.ui.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingtester.textscanner.databinding.ActivityReviewNumbersBinding

class ReviewNumbersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewNumbersBinding
    private var numberList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewNumbersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringArrayListExtra("numbers")?.let {
            numberList = it
        }

        binding.txtDetectedNumbers.text = numberList.size.toString()
        binding.txtNumbers.text = numberList.toString()
    }
}