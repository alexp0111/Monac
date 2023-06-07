package com.example.monac.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.monac.R
import com.example.monac.adapters.CardAdapter
import com.example.monac.adapters.CategoryAdapter
import com.example.monac.data.card.Card
import com.example.monac.data.category.TransactionCategory
import com.example.monac.data.transaction.PaymentTransaction
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentAddTransactionBinding
import com.example.monac.ui.main.mods.NewCardTypeFragment
import com.example.monac.ui.main.mods.NewCategoryTypeFragment
import com.example.monac.util.TransactionType
import com.example.monac.util.UiState
import com.example.monac.util.getCurrentUser
import com.example.monac.view_model.CardViewModel
import com.example.monac.view_model.CategoryViewModel
import com.example.monac.view_model.TransactionViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private var fragmentAddTransactionBinding: FragmentAddTransactionBinding? = null

    // TODO: Set type (earn\expanse) based on category

    private var currentUser = User()
    private var currentCardIndex = 0
    private var currentTime = "00:00"
    private var currentDate = "0000.00.00"
    private var currentMarker = "$"
    private var type = TransactionType.EXPENSES
    private var currentCategory = TransactionCategory()

    private val cardViewModel: CardViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    private var cardList = arrayListOf<Card>()
    private var categoryList = arrayListOf<TransactionCategory>()

    private val cardAdapter by lazy {
        CardAdapter(requireContext(),
            onItemClicked = { _, _ -> },
            onLongItemClicked = { _, _ -> false },
            onItemAddClicked = {
                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, NewCardTypeFragment()).commit()
            }
        )
    }

    private val categoryAdapter by lazy {
        CategoryAdapter(
            requireContext(),
            onItemClicked = { _, category ->
                Log.d("QWERTYUI", "1212")
                fragmentAddTransactionBinding?.etCategory?.setText(category.name)
                currentCategory = category
            },
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentUser = getCurrentUser(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddTransactionBinding.bind(view)
        fragmentAddTransactionBinding = binding

        observers(binding)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            currentCategory = arguments?.getParcelable("category", TransactionCategory::class.java)
                ?: TransactionCategory()
        else currentCategory = arguments?.getParcelable("category") ?: TransactionCategory()

        // Set up tume & date
        currentTime = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
        currentDate = SimpleDateFormat("yyyy.MM.dd").format(Calendar.getInstance().time)
        binding.tvTime.text = currentTime
        binding.tvDate.text = currentDate

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Card Adapter
        initCardPager(binding)

        // Category guess adapter
        initCategoryAdapter(binding)

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

        // summ
        binding.tvSum.setText("Сумма ($)")

        // category search
        binding.etCategory.addTextChangedListener {
            currentUser.id?.let { userID ->
                categoryViewModel.guessCategoriesForUser(
                    userID,
                    it.toString()
                )
            }
        }

        // category adding
        binding.ivCategory.setOnClickListener {
            val fragment = NewCategoryTypeFragment()

            if (!binding.etCategory.text.isNullOrEmpty()) {
                val bundle = Bundle()
                bundle.putString("name", binding.etCategory.text.toString())
                bundle.putString("type", "category")
                fragment.arguments = bundle
            }

            parentFragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.container, fragment).commit()
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

        binding.fab.setOnClickListener {
            if (validation(binding)) {
                val transaction = PaymentTransaction(
                    userID = currentUser.id,
                    value = binding.etSum.text.toString().toDouble(),
                    cardID = cardList[currentCardIndex].id,
                    typeID = currentCategory.id,
                    date = currentDate,
                    time = currentTime,
                    type = type,
                    comments = binding.etComments.text.toString()
                )
                transactionViewModel.updateTransaction(transaction) { isSuccess ->
                    if (isSuccess) {
                        Snackbar.make(
                            requireView(),
                            "операция добавлена",
                            Snackbar.LENGTH_LONG
                        ).show()

                        parentFragmentManager.popBackStack()
                    } else Snackbar.make(
                        requireView(),
                        "Не удалось добавить операцию",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(
                    requireView(),
                    "Убедитесь, что поля заполнены корректно",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun validation(binding: FragmentAddTransactionBinding): Boolean {
        binding.apply {
            return !etSum.text.isNullOrEmpty() && currentCategory.id != null
        }
    }

    private fun initCategoryAdapter(binding: FragmentAddTransactionBinding) {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvCategories.layoutManager = manager
        binding.rvCategories.adapter = categoryAdapter
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

        val sdf = SimpleDateFormat("yyyy.MM.dd")
        val dateInFormat = sdf.format(time)
        currentDate = dateInFormat
        fragmentAddTransactionBinding?.tvDate?.text = dateInFormat
    }

    override fun onStart() {
        super.onStart()
        fragmentAddTransactionBinding?.etCategory?.setText("")
        if (currentCategory.id != null) fragmentAddTransactionBinding?.etCategory?.setText(
            currentCategory.name
        )
        cardViewModel.getAllCardsForUser(currentUser.id ?: -1)
    }

    private fun observers(binding: FragmentAddTransactionBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardViewModel.allCardsForUser.collect {
                    if (it is UiState.Success && it.data != null) {
                        cardList = ArrayList(it.data)
                        binding.tvSum.text = "Сумма (${cardList[0].marker})"
                        cardAdapter.updateList(cardList, false)
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoryViewModel.guessedCategoriesForUser.collect {
                    if (it is UiState.Success && it.data != null) {
                        categoryList = ArrayList(it.data)
                        categoryAdapter.updateList(categoryList)
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
                binding.tvSum.text = "Сумма (${cardList[position].marker})"
                currentMarker = cardList[position].marker
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