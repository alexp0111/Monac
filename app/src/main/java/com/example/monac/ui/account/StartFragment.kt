package com.example.monac.ui.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentStartBinding

class StartFragment : Fragment(R.layout.fragment_start) {

    private var fragmentStartBinding: FragmentStartBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentStartBinding.bind(view)
        fragmentStartBinding = binding
    }

    override fun onDestroy() {
        fragmentStartBinding = null
        super.onDestroy()
    }
}