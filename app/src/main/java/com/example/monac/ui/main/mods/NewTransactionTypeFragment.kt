package com.example.monac.ui.main.mods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentNewTransactionTypeBinding

class NewTransactionTypeFragment : Fragment(R.layout.fragment_new_transaction_type) {
    private var fragmentNewTransactionTypeBinding: FragmentNewTransactionTypeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewTransactionTypeBinding.bind(view)
        fragmentNewTransactionTypeBinding = binding
    }

    override fun onDestroy() {
        fragmentNewTransactionTypeBinding = null
        super.onDestroy()
    }
}