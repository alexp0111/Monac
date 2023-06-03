package com.example.monac.ui.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.monac.R
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentNewAccBinding
import com.example.monac.ui.main.HomeFragment
import com.example.monac.util.UserType
import com.example.monac.util.clearSP
import com.example.monac.util.setUpLogInType
import com.example.monac.util.setUpUser
import com.example.monac.view_model.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewAccFragment : Fragment(R.layout.fragment_new_acc) {

    private val userViewModel: UserViewModel by viewModels()

    private var fragmentNewAccBinding: FragmentNewAccBinding? = null

    private var selectedUri = Uri.EMPTY
    private var type = UserType.STANDART

    private val selectImageIntent =
        registerForActivityResult(ActivityResultContracts.OpenDocument())
        { uri ->
            if (uri != null) {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                selectedUri = uri
                Toast.makeText(requireContext(), uri.toString(), Toast.LENGTH_SHORT).show()
                fragmentNewAccBinding?.ivAvatar?.let {
                    Glide.with(requireContext())
                        .load(uri)
                        .into(it)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewAccBinding.bind(view)
        fragmentNewAccBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnLogIn.setOnClickListener {
            binding.apply {
                val user = User(
                    name = etName.text.toString(),
                    password = etPassword.text.toString(),
                    imageUri = selectedUri.toString(),
                    type = type
                )
                if (validation(this)) userViewModel.updateUser(user) { isSuccess ->
                    if (isSuccess) {
                        Snackbar.make(
                            requireView(),
                            "Регистрация пройдена успешно",
                            Snackbar.LENGTH_LONG
                        ).show()

                        clearSP(requireActivity())
                        setUpUser(requireActivity(), user)
                        setUpLogInType(requireActivity(), true)

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, HomeFragment()).commit()
                    } else Snackbar.make(
                        requireView(),
                        "Зарегистрировать пользователя не удалось",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else Snackbar.make(
                    requireView(),
                    "Заполните все данные, пожалуйста",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.ivAvatar.setOnClickListener {
            selectImageIntent.launch(arrayOf("image/*"))
        }

        binding.apply {
            tvStandart.setOnClickListener {
                type = UserType.STANDART
                tvStandart.setTextColor(
                    resources.getColor(
                        R.color.picker_text_on,
                        resources.newTheme()
                    )
                )
                tvChild.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
            }
            tvChild.setOnClickListener {
                type = UserType.CHILD
                tvStandart.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
                tvChild.setTextColor(
                    resources.getColor(
                        R.color.picker_text_on,
                        resources.newTheme()
                    )
                )
            }
        }
    }

    private fun validation(b: FragmentNewAccBinding): Boolean {
        b.apply {
            return (!tvName.text.isNullOrEmpty()
                    && !tvPassword.text.isNullOrEmpty()
                    && selectedUri != Uri.EMPTY)
        }
    }

    override fun onDestroy() {
        fragmentNewAccBinding = null
        super.onDestroy()
    }
}