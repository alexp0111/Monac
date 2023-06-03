package com.example.monac.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentSettingsBinding
import com.example.monac.ui.account.PasswordFragment
import com.example.monac.util.*


class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var fragmentSettingsBinding: FragmentSettingsBinding? = null

    private var user = User()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        user = getCurrentUser(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSettingsBinding.bind(view)
        fragmentSettingsBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // SSet up info
        binding.apply {
            tvNameValue.text = user.name
            tvPasswordChangeValue.text = user.password.replace(".".toRegex(), "*")
            tvTypeValue.text = if (user.type == UserType.STANDART) "Стандарт" else "Детский"
            tvPasswordValue.text = if (getLogInType(requireActivity())) "Да" else "Нет"

            if (user.type == UserType.STANDART) {
                val params = clChildSection.layoutParams
                params.height = 1
                clChildSection.layoutParams = params
                clChildSection.visibility = View.INVISIBLE
            }
        }

        binding.cvPasswordChange.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.container, PasswordFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.cvPassword.setOnClickListener {
            if (getLogInType(requireActivity())) {
                setUpLogInType(requireActivity(), false)
                binding.tvPasswordValue.text = "Нет"
            } else {
                setUpLogInType(requireActivity(), true)
                binding.tvPasswordValue.text = "Да"
            }
        }

        binding.btnLogOut.setOnClickListener {
            clearSP(requireActivity())
            repeat(parentFragmentManager.backStackEntryCount) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroy() {
        fragmentSettingsBinding = null
        super.onDestroy()
    }
}