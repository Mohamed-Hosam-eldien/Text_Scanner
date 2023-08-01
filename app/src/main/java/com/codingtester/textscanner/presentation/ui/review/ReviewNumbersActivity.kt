package com.codingtester.textscanner.presentation.ui.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingtester.textscanner.databinding.ActivityReviewNumbersBinding
import com.codingtester.textscanner.presentation.utils.Constants.NUMBERS_LIST

class ReviewNumbersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewNumbersBinding
    private var numberList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewNumbersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringArrayListExtra(NUMBERS_LIST)?.let {
            numberList = it
        }

        // set repeated numbers to recyclerView
        val repeatedItems: Map<String, Int> = numberList.groupingBy { it }.eachCount()
        binding.recyclerRepetition.apply {
            adapter = RepeatNumbersAdapter(repeatedItems)
            layoutManager = LinearLayoutManager(this@ReviewNumbersActivity)
        }

        // set list of numbers and number detected count
        binding.txtDetectedNumbers.text = numberList.size.toString()
        binding.txtNumbers.text = numberList.toString()

        //set greatest number
        binding.txtGreatestNumber.text = numberList.maxOfOrNull { it.toInt() }.toString()

        //set smallest number
        binding.txtSmallestNumber.text = numberList.minOfOrNull { it.toInt() }.toString()

        // set total of numbers
        binding.txtTotalSum.text = numberList.sumOf { it.toInt() }.toString()
    }
}