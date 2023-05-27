package com.example.monac.ui.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentPasswordBinding

class PasswordFragment : Fragment(R.layout.fragment_password) {
    private var fragmentPasswordBinding: FragmentPasswordBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPasswordBinding.bind(view)
        fragmentPasswordBinding = binding
    }

    override fun onDestroy() {
        fragmentPasswordBinding = null
        super.onDestroy()
    }
}