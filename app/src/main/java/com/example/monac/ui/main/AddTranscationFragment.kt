package com.example.monac.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentAddTransactionBinding

class AddTranscationFragment : Fragment(R.layout.fragment_add_transaction) {
    private var fragmentAddTransactionBinding: FragmentAddTransactionBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddTransactionBinding.bind(view)
        fragmentAddTransactionBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        fragmentAddTransactionBinding = null
        super.onDestroy()
    }
}