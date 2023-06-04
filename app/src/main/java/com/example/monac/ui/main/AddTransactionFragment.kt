package com.example.monac.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
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
import com.example.monac.ui.main.mods.NewCardTypeFragment
import com.example.monac.util.TransactionType
import com.example.monac.util.UiState
import com.example.monac.util.getCurrentUser
import com.example.monac.view_model.CardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private var fragmentAddTransactionBinding: FragmentAddTransactionBinding? = null

    private var currentUser = User()
    private var currentCardIndex = 0
    private var currentTime = "00:00"
    private var currentDate = "00.00.0000"
    private var type = TransactionType.EXPENSES

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

        // Set up tume & date
        binding.tvTime.text = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
        binding.tvDate.text = SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().time)

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Card Adapter
        initCardPager(binding)

        // picker
        binding.apply {
            tvExpenses.setOnClickListener {
                type = TransactionType.EXPENSES
                tvExpenses.setTextColor(
                    resources.getColor(
                        R.color.picker_text_on,
                        resources.newTheme()
                    )
                )
                tvEarnings.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
            }
            tvEarnings.setOnClickListener {
                type = TransactionType.EARNINGS
                tvExpenses.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
                tvEarnings.setTextColor(
                    resources.getColor(
                        R.color.picker_text_on,
                        resources.newTheme()
                    )
                )
            }
        }

        // time picker
        binding.tvTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.tvDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        Log.d("TYTYTY", p1.toString())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, p1)
        calendar.set(Calendar.MINUTE, p2)
        val time = calendar.time

        val sdf = SimpleDateFormat("HH:mm")
        val timeInFormat = sdf.format(time)
        currentTime = timeInFormat
        fragmentAddTransactionBinding?.tvTime?.text = timeInFormat
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, p3)
        calendar.set(Calendar.MONTH, p2)
        calendar.set(Calendar.YEAR, p1)
        val time = calendar.time

        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val dateInFormat = sdf.format(time)
        currentDate = dateInFormat
        fragmentAddTransactionBinding?.tvDate?.text = dateInFormat
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