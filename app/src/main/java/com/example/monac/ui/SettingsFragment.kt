package com.example.monac.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var fragmentSettingsBinding: FragmentSettingsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSettingsBinding.bind(view)
        fragmentSettingsBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        fragmentSettingsBinding = null
        super.onDestroy()
    }
}