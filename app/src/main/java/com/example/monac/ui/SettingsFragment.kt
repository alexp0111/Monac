package com.example.monac.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.monac.R
import com.example.monac.data.category.TransactionCategory
import com.example.monac.data.getActualContacts
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentSettingsBinding
import com.example.monac.ui.account.LimitFragment
import com.example.monac.ui.account.PasswordFragment
import com.example.monac.util.*
import com.example.monac.view_model.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var fragmentSettingsBinding: FragmentSettingsBinding? = null

    private val categoryViewModel: CategoryViewModel by viewModels()

    private var userList = arrayListOf<TransactionCategory>()
    private var user = User()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkPermission(Manifest.permission.READ_CONTACTS, 1)
        user = getCurrentUser(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSettingsBinding.bind(view)
        fragmentSettingsBinding = binding

        observes()

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_NO)
            binding.tvThemeValue.text = "Светлая"
        else binding.tvThemeValue.text = "Темная"

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

        binding.tvLimitValue.text = "${user.limitUSD}$ / ${user.limitRUB}₽ / ${user.limitEUR}€"

        binding.cvLimit.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.container, LimitFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.apply {
            cvTheme.setOnClickListener {
                if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES) {
                    tvThemeValue.text = "Светлая"
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                } else {
                    tvThemeValue.text = "Темная"
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                }
            }
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
            requireActivity().finishAndRemoveTask()
        }
    }

    override fun onStart() {
        super.onStart()
        // categoryViewModel.deleteAllCategoriesForUser(user.id ?: -1)
        categoryViewModel.getAllTransactionUsersForUser(user.id ?: -1)
        fragmentSettingsBinding?.tvLimitValue?.text = "${user.limitUSD}$ / ${user.limitRUB}₽ / ${user.limitEUR}€"
    }

    private fun observes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoryViewModel.allTransactionUsersForUser.collect {
                    if (it is UiState.Success && it.data != null) {
                        userList = ArrayList(it.data)
                        if (checkPermission(Manifest.permission.READ_CONTACTS, 1)) {
                            val deltaList = arrayListOf<TransactionCategory>()
                            getActualContacts(requireContext(), user.id)?.forEach { trFromContact ->
                                var isInDB = false
                                userList.forEach { trFromBD ->
                                    if (trFromBD.phone == trFromContact.phone) isInDB = true
                                }
                                if (!isInDB) deltaList.add(trFromContact)
                            }
                            if (deltaList.isNotEmpty()) categoryViewModel.updateUsers(deltaList) {
                                // check for success
                            }
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
        fragmentSettingsBinding = null
        super.onDestroy()
    }
}