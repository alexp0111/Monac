package com.example.monac.ui.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentLogInBinding

class LogInFragment : Fragment(R.layout.fragment_log_in) {
    private var fragmentLogInBinding: FragmentLogInBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentLogInBinding.bind(view)
        fragmentLogInBinding = binding
    }

    override fun onDestroy() {
        fragmentLogInBinding = null
        super.onDestroy()
    }
}