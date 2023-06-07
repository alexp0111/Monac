package com.example.monac.ui.account

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.monac.R
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentLimitBinding
import com.example.monac.ui.main.HomeFragment
import com.example.monac.util.*
import com.example.monac.view_model.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LimitFragment : Fragment(R.layout.fragment_limit) {
    private var fragmentLimitBinding: FragmentLimitBinding? = null

    private val userViewModel: UserViewModel by viewModels()

    private var user = User()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        user = getCurrentUser(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentLimitBinding.bind(view)
        fragmentLimitBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            if (validation(binding)) {
                val newUser = user
                newUser.limitRUB = binding.etLimitRub.text.toString().toDouble()
                newUser.limitUSD = binding.etLimitUsd.text.toString().toDouble()
                newUser.limitEUR = binding.etLimitEur.text.toString().toDouble()
                userViewModel.updateUser(newUser) { id ->
                    if (id != -1L) {
                        Snackbar.make(
                            requireView(),
                            "Лимит установлен",
                            Snackbar.LENGTH_LONG
                        ).show()

                        val inputType = getLogInType(requireActivity())
                        clearSP(requireActivity())
                        setUpUser(requireActivity(), newUser)
                        setUpLogInType(requireActivity(), inputType)

                        parentFragmentManager.popBackStack()
                    } else Snackbar.make(
                        requireView(),
                        "Установить лимит не удалось",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else Snackbar.make(
                requireView(),
                "Заполните все данные, пожалуйста",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun validation(binding: FragmentLimitBinding): Boolean {
        binding.apply {
            return (etPassword.text.toString() == user.password)
                    && !etLimitRub.text.isNullOrEmpty() && etLimitRub.text.toString().isDigitsOnly()
                    && !etLimitEur.text.isNullOrEmpty() && etLimitEur.text.toString().isDigitsOnly()
                    && !etLimitUsd.text.isNullOrEmpty() && etLimitUsd.text.toString().isDigitsOnly()

        }
    }

    override fun onDestroy() {
        fragmentLimitBinding = null
        super.onDestroy()
    }
}