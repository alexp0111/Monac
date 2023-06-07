package com.example.monac.ui.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.monac.R
import com.example.monac.adapters.CardAdapter
import com.example.monac.adapters.SwipeGesture
import com.example.monac.adapters.TransactionAdapter
import com.example.monac.adapters.TransactionUserAdapter
import com.example.monac.data.card.Card
import com.example.monac.data.category.TransactionCategory
import com.example.monac.data.transaction.PaymentTransaction
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentHomeBinding
import com.example.monac.ui.SettingsFragment
import com.example.monac.ui.main.mods.NewCardTypeFragment
import com.example.monac.ui.main.mods.NewCategoryTypeFragment
import com.example.monac.util.UiState
import com.example.monac.util.getCurrentUser
import com.example.monac.view_model.CardViewModel
import com.example.monac.view_model.CategoryViewModel
import com.example.monac.view_model.TransactionViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private var fragmentHomeBinding: FragmentHomeBinding? = null

    private var currentUser = User()
    private var currentCardIndex = 0

    private val cardViewModel: CardViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()

    private var cardList = arrayListOf<Card>()
    private var userList = arrayListOf<TransactionCategory>()
    private var categoryList = arrayListOf<TransactionCategory>()
    private var transactionListForCard = arrayListOf<PaymentTransaction>()

    private val cardAdapter by lazy {
        CardAdapter(requireContext(), requireActivity(),
            onItemClicked = { _, _ ->
                val fragment = InfoFragment()

                val bundle = Bundle()
                bundle.putParcelableArrayList("transactions", transactionListForCard)
                bundle.putParcelableArrayList("categories", categoryList)
                bundle.putParcelable("card", cardList[currentCardIndex])
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, fragment).commit()
            },
            onLongItemClicked = { _, item ->
                val fragment = NewCardTypeFragment()

                val bundle = Bundle()
                bundle.putParcelable("card", item)
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, fragment).commit()
                true
            },
            onItemAddClicked = {
                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, NewCardTypeFragment()).commit()
            }
        )
    }

    private val transactionUserAdapter by lazy {
        TransactionUserAdapter(requireContext(),
            onItemClicked = { _, item ->
                val fragment = AddTransactionFragment()

                val bundle = Bundle()
                bundle.putParcelable("category", item)
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, fragment).commit()
            },
            onLongItemClicked = { _, item ->
                val fragment = NewCategoryTypeFragment()

                val bundle = Bundle()
                bundle.putString("type", "transaction")
                bundle.putParcelable("category", item)
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, fragment).commit()
                true
            },
            onItemAddClicked = {
                val fragment = NewCategoryTypeFragment()

                val bundle = Bundle()
                bundle.putString("type", "transaction")
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, fragment).commit()
            }
        )
    }

    private val transactionAdapter by lazy {
        TransactionAdapter(requireContext(),
            onLongItemClicked = { _, item ->
                if (item.comments.isNotEmpty())
                    Snackbar.make(
                        requireView(),
                        "${getString(R.string.comments)}: ${item.comments}",
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                true
            }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentUser = getCurrentUser(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)
        fragmentHomeBinding = binding

        observers()

        Glide.with(requireContext())
            .load(currentUser.imageUri.toUri())
            .into(binding.ivAvatar)

        binding.tvHelloName.text = buildString {
            append(getString(R.string.hello))
            append(currentUser.name)
            append("!")
        }

        binding.ivSettings.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.fab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, AddTransactionFragment())
                .addToBackStack(null)
                .commit()
        }

        // Card Adapter
        initCardPager(binding)

        //user list
        initUserRecycler(binding)

        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {
                    val transaction =
                        transactionListForCard[viewHolder.absoluteAdapterPosition]
                    transactionViewModel.deleteTransaction(transaction)
                    transactionAdapter.deleteItem(viewHolder.absoluteAdapterPosition)

                    Snackbar.make(requireView(), getString(R.string.removed_from_list), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.restore)) {
                            transactionViewModel.updateTransaction(transaction) {
                                transactionViewModel.getAllTransactionForUserAtCard(
                                    currentUser.id ?: -1, cardList[currentCardIndex].id ?: -1
                                )
                            }
                        }.show()
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.rvRecent)

        // RecentTransactionsAdapter
        initTransactionsRecycler(binding)
    }

    override fun onStart() {
        super.onStart()
        fragmentHomeBinding?.vpCards?.currentItem = 0
        cardViewModel.getAllCardsForUser(getCurrentUser(requireActivity()).id ?: -1)
        categoryViewModel.getAllTransactionUsersForUser(getCurrentUser(requireActivity()).id ?: -1)
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardViewModel.allCardsForUser.collect {
                    if (it is UiState.Success && it.data != null) {
                        cardList = ArrayList(it.data)
                        cardAdapter.updateList(cardList, true)

                        // Get transactions for card
                        if (currentUser.id != null && cardList.isNotEmpty() && cardList[0].id != null) {
                            transactionViewModel.getAllTransactionForUserAtCard(
                                currentUser.id!!,
                                cardList[0].id!!
                            )
                        }
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
                transactionViewModel.allTransactionForUserAtCard.collect {
                    if (it is UiState.Success && it.data != null) {
                        transactionListForCard = ArrayList(it.data)
                        currentUser.id?.let { it1 -> categoryViewModel.getAllCategoriesForUser(it1) }
                        cardAdapter.setUpVaue(transactionListForCard, currentCardIndex)
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
                categoryViewModel.allCategoriesForUser.collect {
                    if (it is UiState.Success && it.data != null) {
                        categoryList = ArrayList(it.data)
                        transactionAdapter.updateList(
                            transactionListForCard,
                            categoryList,
                            cardList[currentCardIndex]
                        )
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
                categoryViewModel.allTransactionUsersForUser.collect {dataList ->
                    if (dataList is UiState.Success && dataList.data != null) {
                        userList = ArrayList(dataList.data)
                        transactionUserAdapter.updateList(ArrayList(userList.sortedWith(compareBy { it.name })))
                    }
                    if (dataList is UiState.Failure) {
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

    private fun initTransactionsRecycler(
        binding: FragmentHomeBinding
    ) {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.rvRecent.layoutManager = manager
        binding.rvRecent.adapter = transactionAdapter
    }

    private fun initUserRecycler(
        binding: FragmentHomeBinding
    ) {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvTransactions.layoutManager = manager
        binding.rvTransactions.adapter = transactionUserAdapter
    }

    private fun initCardPager(binding: FragmentHomeBinding) {
        binding.vpCards.adapter = cardAdapter

        binding.vpCards.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentCardIndex = position
                if (currentUser.id != null && cardList.isNotEmpty() && position != cardList.size - 1 && cardList[position].id != null)
                    transactionViewModel.getAllTransactionForUserAtCard(
                        currentUser.id!!,
                        cardList[position].id!!
                    )
            }
        })

        setUpTransformer(binding)
    }

    private fun setUpTransformer(binding: FragmentHomeBinding) {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.vpCards.setPageTransformer(transformer)
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }
}