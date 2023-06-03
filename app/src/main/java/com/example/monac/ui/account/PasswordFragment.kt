package com.example.monac.ui.account

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.monac.R
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentPasswordBinding
import com.example.monac.util.getCurrentUser
import com.example.monac.util.setUpUser
import com.example.monac.view_model.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordFragment : Fragment(R.layout.fragment_password) {

    private val userViewModel: UserViewModel by viewModels()

    private var fragmentPasswordBinding: FragmentPasswordBinding? = null

    private var user = User()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        user = getCurrentUser(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPasswordBinding.bind(view)
        fragmentPasswordBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnNewPassword.setOnClickListener {
            if (validation(binding)) {
                user.password = binding.etPasswordProve.text.toString()
                userViewModel.updateUser(user) { isSuccess ->
                    if (isSuccess) {
                        Snackbar.make(
                            requireView(),
                            "Пароль изменён",
                            Snackbar.LENGTH_LONG
                        ).show()

                        setUpUser(requireActivity(), user)

                        parentFragmentManager.popBackStack()
                    } else Snackbar.make(
                        requireView(),
                        "Изменить пароль не удалось",
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

    private fun validation(binding: FragmentPasswordBinding): Boolean {
        binding.apply {
            return (etOldPassword.text.toString() == user.password
                    && !etPassword.text.isNullOrEmpty()
                    && etPassword.text.toString() == etPasswordProve.text.toString())
        }
    }

    override fun onDestroy() {
        fragmentPasswordBinding = null
        super.onDestroy()
    }
}