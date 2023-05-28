package com.example.monac.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.monac.R
import com.example.monac.adapters.CardAdapter
import com.example.monac.data.Card
import com.example.monac.databinding.FragmentHomeBinding
import kotlin.math.abs

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var fragmentHomeBinding: FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)
        fragmentHomeBinding = binding

        initCardPager(binding)
    }

    private fun initCardPager(binding: FragmentHomeBinding) {
        var cardAdapter =
            CardAdapter(
                arrayListOf(
                    Card(name = "Заработная плата", value = 2848.0),
                    Card(name = "Стипендия", value = 0.001)
                ), binding.vpCards
            )
        binding.vpCards.adapter = cardAdapter

        setUpTransformer(binding)
    }

    private fun setUpTransformer(binding: FragmentHomeBinding) {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.12f
        }
        binding.vpCards.setPageTransformer(transformer)
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }
}