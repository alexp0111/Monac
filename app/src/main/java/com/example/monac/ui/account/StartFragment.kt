package com.example.monac.ui.account

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monac.R
import com.example.monac.adapters.UserAdapter
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentStartBinding
import com.example.monac.ui.main.HomeFragment
import com.example.monac.util.UiState
import com.example.monac.util.getCurrentUser
import com.example.monac.util.getLogInType
import com.example.monac.util.isSPClear
import com.example.monac.view_model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.fragment_start) {

    private val userViewModel: UserViewModel by viewModels()

    private var fragmentStartBinding: FragmentStartBinding? = null

    private var userList = arrayListOf<User>()

    private val userAdapter by lazy {
        UserAdapter(requireContext(),
            onItemClicked = { _, item ->
                val fragment = LogInFragment()
                val bundle = Bundle()
                bundle.putParcelable("user", item)
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, fragment).commit()
            },
            onItemAddClicked = {
                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, NewAccFragment()).commit()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isSPClear(requireActivity())) {
            if (!getLogInType(requireActivity())) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment()).commit()
            } else {
                val fragment = LogInFragment()
                val bundle = Bundle()
                bundle.putParcelable("user", getCurrentUser(requireActivity()))
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, fragment).commit()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        userViewModel.getAllUsers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentStartBinding.bind(view)
        fragmentStartBinding = binding

        initUserAdapter(binding)
        observers()
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.allUsers.collect {
                    if (it is UiState.Success && it.data != null) {
                        userList = ArrayList(it.data)
                        userAdapter.updateList(userList)
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


    private fun initUserAdapter(binding: FragmentStartBinding) {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvUsers.layoutManager = manager
        binding.rvUsers.adapter = userAdapter
    }

    override fun onDestroy() {
        fragmentStartBinding = null
        super.onDestroy()
    }
}