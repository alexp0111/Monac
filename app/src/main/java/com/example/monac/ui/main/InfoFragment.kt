package com.example.monac.ui.main

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monac.R
import com.example.monac.adapters.TransactionAdapter
import com.example.monac.data.card.Card
import com.example.monac.data.category.TransactionCategory
import com.example.monac.data.transaction.PaymentTransaction
import com.example.monac.databinding.FragmentInfoBinding
import com.example.monac.ui.animator.PickerAnimator
import com.example.monac.util.TransactionType
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InfoFragment : Fragment(R.layout.fragment_info) {
    private var fragmentInfoBinding: FragmentInfoBinding? = null

    private var datePicker = mutableMapOf<ConstraintLayout, TextView>()

    private var transactionList = arrayListOf<PaymentTransaction>()
    private var categoryList = arrayListOf<TransactionCategory>()
    private var card = Card()


    private val transactionAdapter by lazy {
        TransactionAdapter(requireContext(),
            onLongItemClicked = { _, item ->
                if (item.comments.isNotEmpty())
                    Snackbar.make(requireView(), "${getString(R.string.comments)}: ${item.comments}", Snackbar.LENGTH_LONG)
                    .show()
                true
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentInfoBinding.bind(view)
        fragmentInfoBinding = binding

        // RecentTransactionsAdapter
        initTransactionsRecycler(binding)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            transactionList = arguments?.getParcelableArrayList(
                "transactions",
                PaymentTransaction::class.java
            ) as ArrayList<PaymentTransaction>

            categoryList = arguments?.getParcelableArrayList(
                "categories",
                TransactionCategory::class.java
            ) as ArrayList<TransactionCategory>

            card = arguments?.getParcelable("card", Card::class.java) ?: Card()
        }

        setUpChartForPeriod("24h", binding)

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // chart
        binding.apply {
            lineChart.gradientFillColors =
                intArrayOf(
                    ContextCompat.getColor(requireContext(), R.color.main_text_color),
                    Color.TRANSPARENT
                )
            lineChart.animation.duration = animationDuration
        }

        // Animating picker
        binding.apply {
            datePicker[picker1] = pickerText1
            datePicker[picker2] = pickerText2
            datePicker[picker3] = pickerText3
            datePicker[picker4] = pickerText4
            datePicker[picker5] = pickerText5
            datePicker[picker6] = pickerText6

            PickerAnimator {
                setUpChartForPeriod(it, binding)
            }.animate(resources, context, datePicker, pickerCircle)
        }
    }

    private fun setUpChartForPeriod(it: String, binding: FragmentInfoBinding) {
        val shownList = getListForPeriod(it)
        binding.lineChart.animate(shownList.asReversed())
    }

    private fun getListForPeriod(period: String): ArrayList<Pair<String, Float>> {
        val currentDate = LocalDateTime.now()
        val list = arrayListOf<PaymentTransaction>()
        transactionList.forEach {
            val date = LocalDateTime.parse(
                it.date + " " + it.time,
                DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
            )

            when (period) {
                "24h" -> if (currentDate.minusDays(1) <= date) list.add(it)
                "7d" -> if (currentDate.minusDays(7) <= date) list.add(it)
                "1m" -> if (currentDate.minusMonths(1) <= date) list.add(it)
                "3m" -> if (currentDate.minusMonths(3) <= date) list.add(it)
                "1y" -> if (currentDate.minusYears(1) <= date) list.add(it)
                else -> list.add(it)
            }
        }
        transactionAdapter.updateList(list, categoryList, card)
        return list.asPairedList()
    }

    private fun initTransactionsRecycler(
        binding: FragmentInfoBinding
    ) {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.rvOperations.layoutManager = manager
        binding.rvOperations.adapter = transactionAdapter
    }

    override fun onDestroy() {
        fragmentInfoBinding = null
        super.onDestroy()
    }

    companion object {
        private const val animationDuration = 1000L
    }
}

private fun ArrayList<PaymentTransaction>.asPairedList(): ArrayList<Pair<String, Float>> {
    val list = arrayListOf<Pair<String, Float>>()
    this.forEach {
        if (it.type == TransactionType.EARNINGS)
            list.add(Pair(it.date.plus(it.time), it.value.toFloat()))
        else list.add(Pair(it.date.plus(it.time), it.value.toFloat() * (-1)))
    }
    return list
}
