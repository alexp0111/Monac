package com.example.monac.ui.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentNewAccBinding

class NewAccFragment  : Fragment(R.layout.fragment_new_acc){
    private var fragmentNewAccBinding: FragmentNewAccBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewAccBinding.bind(view)
        fragmentNewAccBinding = binding
    }

    override fun onDestroy() {
        fragmentNewAccBinding = null
        super.onDestroy()
    }
}