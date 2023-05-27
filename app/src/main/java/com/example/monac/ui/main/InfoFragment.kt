package com.example.monac.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentInfoBinding

class InfoFragment : Fragment(R.layout.fragment_info) {
    private var fragmentInfoBinding: FragmentInfoBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentInfoBinding.bind(view)
        fragmentInfoBinding = binding
    }

    override fun onDestroy() {
        fragmentInfoBinding = null
        super.onDestroy()
    }
}