package com.example.monac.ui.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.monac.R
import com.example.monac.adapters.CardAdapter
import com.example.monac.data.card.Card
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentAddTransactionBinding
import com.example.monac.databinding.FragmentHomeBinding
import com.example.monac.ui.main.mods.NewCardTypeFragment
import com.example.monac.util.UiState
import com.example.monac.util.getCurrentUser
import com.example.monac.view_model.CardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class AddTranscationFragment : Fragment(R.layout.fragment_add_transaction) {
    private var fragmentAddTransactionBinding: FragmentAddTransactionBinding? = null

    private var currentUser = User()
    private var currentCardIndex = 0

    private val cardViewModel: CardViewModel by viewModels()

    private var cardList = arrayListOf<Card>()

    private val cardAdapter by lazy {
        CardAdapter(requireContext(),
            onItemClicked = { _, _ -> },
            onItemAddClicked = {
                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, NewCardTypeFragment()).commit()
            }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentUser = getCurrentUser(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Working with categories: search
        // TODO: Picking date & time
        // TODO: Set marker to value based on card marker

        val binding = FragmentAddTransactionBinding.bind(view)
        fragmentAddTransactionBinding = binding

        observers(binding)

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Card Adapter
        initCardPager(binding)
    }

    override fun onStart() {
        super.onStart()
        cardViewModel.getAllCardsForUser(currentUser.id ?: -1)
    }

    private fun observers(binding: FragmentAddTransactionBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardViewModel.allCardsForUser.collect {
                    if (it is UiState.Success && it.data != null) {
                        cardList = ArrayList(it.data)
                        cardAdapter.updateList(cardList)
                    }
                    if (it is UiState.Failure) {
                        Toast.makeText(
                            requireContext(),
                            "sww",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun initCardPager(binding: FragmentAddTransactionBinding) {
        binding.vpCards.adapter = cardAdapter

        binding.vpCards.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentCardIndex = position
            }
        })

        setUpTransformer(binding)
    }

    private fun setUpTransformer(binding: FragmentAddTransactionBinding) {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.vpCards.setPageTransformer(transformer)
    }

    override fun onDestroy() {
        fragmentAddTransactionBinding = null
        super.onDestroy()
    }
}