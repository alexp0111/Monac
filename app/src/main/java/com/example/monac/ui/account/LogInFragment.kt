package com.example.monac.ui.account

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.monac.R
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentLogInBinding
import com.example.monac.ui.main.HomeFragment
import com.example.monac.util.isSPClear
import com.example.monac.util.setUpLogInType
import com.example.monac.util.setUpUser
import com.google.android.material.snackbar.Snackbar

class LogInFragment : Fragment(R.layout.fragment_log_in) {
    private var fragmentLogInBinding: FragmentLogInBinding? = null

    var user = User()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentLogInBinding.bind(view)
        fragmentLogInBinding = binding

        // Args
        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(
            "user",
            User::class.java
        ) ?: User()
        else arguments?.getParcelable("user") ?: User()

        binding.apply {
            // Image
            Glide.with(requireContext())
                .load(user.imageUri.toUri())
                .into(ivAvatar)

            // Name
            tvAccount.text = user.name
        }

        binding.btnLogIn.setOnClickListener {
            if (!binding.etPassword.text.isNullOrEmpty()) {
                if (binding.etPassword.text.toString() == user.password) {

                    if (isSPClear(requireActivity())) {
                        setUpUser(requireActivity(), user)
                        setUpLogInType(requireActivity(), true)
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment()).commit()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Похоже, что пароль не подходит",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(
                    requireView(),
                    "Пожалуйста, введите пароль",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onDestroy() {
        fragmentLogInBinding = null
        super.onDestroy()
    }
}