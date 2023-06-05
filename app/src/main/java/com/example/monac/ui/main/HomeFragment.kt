package com.example.monac.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.monac.R
import com.example.monac.adapters.CardAdapter
import com.example.monac.adapters.TransactionAdapter
import com.example.monac.adapters.TransactionUserAdapter
import com.example.monac.data.category.TransactionCategory
import com.example.monac.data.card.Card
import com.example.monac.data.getActualContacts
import com.example.monac.data.transaction.PaymentTransaction
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentHomeBinding
import com.example.monac.ui.SettingsFragment
import com.example.monac.ui.main.mods.NewCardTypeFragment
import com.example.monac.ui.main.mods.NewTransactionTypeFragment
import com.example.monac.util.UiState
import com.example.monac.util.getCurrentUser
import com.example.monac.view_model.CardViewModel
import com.example.monac.view_model.TransactionViewModel
import com.example.monac.view_model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val TAG = "HOME_FRAGMENT"
    private var fragmentHomeBinding: FragmentHomeBinding? = null

    private var currentUser = User()
    private var currentCardIndex = 0

    private val userViewModel: UserViewModel by viewModels()
    private val cardViewModel: CardViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()

    private var cardList = arrayListOf<Card>()
    private var transactionListForCard = arrayListOf<PaymentTransaction>()

    private val cardAdapter by lazy {
        CardAdapter(requireContext(),
            onItemClicked = { pos, item ->
                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, InfoFragment()).commit()
            },
            onItemAddClicked = {
                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, NewCardTypeFragment()).commit()
            }
        )
    }

    private val transactionUserAdapter by lazy {
        TransactionUserAdapter(requireContext(),
            onItemClicked = { pos, item ->
                // todo: new transaction to user
            },
            onItemAddClicked = {
                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, NewTransactionTypeFragment()).commit()
            }
        )
    }

    private val transactionrAdapter by lazy {
        TransactionAdapter(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkPermission(Manifest.permission.READ_CONTACTS, 1)
        currentUser = getCurrentUser(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)
        fragmentHomeBinding = binding

        observers(binding)

        Glide.with(requireContext())
            .load(currentUser.imageUri.toUri())
            .into(binding.ivAvatar)

        binding.tvHelloName.text = buildString {
            append("Здравствуйте, ")
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

        //Transaction Adapter
        if (checkPermission(Manifest.permission.READ_CONTACTS, 1)) {
            val conts = getActualContacts(requireContext())
            conts.let {
                it?.forEach {
                    Log.d(TAG, it.toString())
                }
                it?.let { list -> initUserRecycler(binding, list) }
            }
        }

        // TODO:
        //  1. Goto viewmodel
        //  2. Check for new contacts
        //  3. If there are new contacts - add info to DB
        //  4. Get list of users from DB & display it

        // RecentTransactionsAdapter
        initTransactionsRecycler(binding)
    }

    override fun onStart() {
        super.onStart()
        // cardViewModel.deleteAllCardsForUser(getCurrentUser(requireActivity()).id ?: -1)
        cardViewModel.getAllCardsForUser(getCurrentUser(requireActivity()).id ?: -1)
    }

    private fun observers(binding: FragmentHomeBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardViewModel.allCardsForUser.collect {
                    if (it is UiState.Success && it.data != null) {
                        cardList = ArrayList(it.data)
                        cardAdapter.updateList(cardList)

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
                        transactionrAdapter.updateList(transactionListForCard)
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

    private fun initTransactionsRecycler(
        binding: FragmentHomeBinding
    ) {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.rvRecent.layoutManager = manager
        binding.rvRecent.adapter = transactionrAdapter
    }

    private fun initUserRecycler(
        binding: FragmentHomeBinding,
        list: List<TransactionCategory>
    ) {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvTransactions.layoutManager = manager
        binding.rvTransactions.adapter = transactionUserAdapter

        transactionUserAdapter.updateList(ArrayList(list.sortedWith(compareBy { it.name })))
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

    private fun checkPermission(permission: String, requestCode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
            false
        } else {
            true
        }
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }
}