package com.example.monac.ui.main.mods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentNewCardTypeBinding

class NewCardTypeFragment : Fragment(R.layout.fragment_new_card_type) {
    private var fragmentNewCardTypeBinding: FragmentNewCardTypeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewCardTypeBinding.bind(view)
        fragmentNewCardTypeBinding = binding
    }

    override fun onDestroy() {
        fragmentNewCardTypeBinding = null
        super.onDestroy()
    }
}