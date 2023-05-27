package com.example.monac.ui.main.mods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentNewCategoryTypeBinding

class NewCategoryTypeFragment : Fragment(R.layout.fragment_new_category_type) {
    private var fragmentNewCategoryTypeBinding: FragmentNewCategoryTypeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewCategoryTypeBinding.bind(view)
        fragmentNewCategoryTypeBinding = binding
    }

    override fun onDestroy() {
        fragmentNewCategoryTypeBinding = null
        super.onDestroy()
    }
}