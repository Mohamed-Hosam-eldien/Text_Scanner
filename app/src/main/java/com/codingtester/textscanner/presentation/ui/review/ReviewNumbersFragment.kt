package com.codingtester.textscanner.presentation.ui.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingtester.textscanner.databinding.FragmentReviewNumbersBinding
import com.codingtester.textscanner.presentation.utils.Constants

class ReviewNumbersFragment : Fragment() {

    private lateinit var binding: FragmentReviewNumbersBinding
    private var numberList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewNumbersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        arguments?.getStringArrayList(Constants.NUMBERS_LIST)?.let {
            numberList = it
        }

        // set repeated numbers to recyclerView
        val repeatedItems: Map<String, Int> = numberList.groupingBy { it }.eachCount()
        binding.recyclerRepetition.apply {
            adapter = RepeatNumbersAdapter(repeatedItems)
            layoutManager = LinearLayoutManager(requireActivity())
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