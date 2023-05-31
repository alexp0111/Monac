package com.example.monac.ui.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentInfoBinding
import com.example.monac.ui.animator.PickerAnimator

class InfoFragment : Fragment(R.layout.fragment_info) {
    private var fragmentInfoBinding: FragmentInfoBinding? = null

    private var datePicker = mutableMapOf<ConstraintLayout, TextView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentInfoBinding.bind(view)
        fragmentInfoBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Animating picker
        binding.apply {
            datePicker[picker1] = pickerText1
            datePicker[picker2] = pickerText2
            datePicker[picker3] = pickerText3
            datePicker[picker4] = pickerText4
            datePicker[picker5] = pickerText5
            datePicker[picker6] = pickerText6

            PickerAnimator {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }.animate(resources, context, datePicker, pickerCircle)
        }
    }

    override fun onDestroy() {
        fragmentInfoBinding = null
        super.onDestroy()
    }
}