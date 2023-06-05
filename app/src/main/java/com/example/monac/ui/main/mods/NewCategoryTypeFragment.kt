package com.example.monac.ui.main.mods

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.monac.R
import com.example.monac.data.category.TransactionCategory
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentNewCategoryTypeBinding
import com.example.monac.util.PaymentType
import com.example.monac.util.TransactionType
import com.example.monac.util.getCurrentUser
import com.example.monac.view_model.CategoryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCategoryTypeFragment : Fragment(R.layout.fragment_new_category_type) {
    private var fragmentNewCategoryTypeBinding: FragmentNewCategoryTypeBinding? = null

    private val categoryViewModel: CategoryViewModel by viewModels()

    private var currentUser = User()
    private var currentColor: Int = Color.WHITE
    private var type = TransactionType.EXPENSES

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentUser = getCurrentUser(requireActivity())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewCategoryTypeBinding.bind(view)
        fragmentNewCategoryTypeBinding = binding

        // Args
        val name = arguments?.getString("name") ?: ""
        binding.etName.setText(name)

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

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

            binding.fab.setOnClickListener {
                if (validation(binding)) {
                    val newCategory = TransactionCategory(
                        userID = currentUser.id,
                        name = binding.etName.text.toString(),
                        phone = null,
                        uri = null,
                        color = currentColor,
                        type = PaymentType.CATEGORY,
                        comments = binding.etComments.text.toString()
                    )
                    categoryViewModel.updateCategory(newCategory) {isSuccess ->
                        if (isSuccess) {
                            Snackbar.make(
                                requireView(),
                                "категория добавлена",
                                Snackbar.LENGTH_LONG
                            ).show()

                            parentFragmentManager.popBackStack()
                        } else Snackbar.make(
                            requireView(),
                            "Не удалось добавить категорию",
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

        binding.ivColorPicker.apply {
            setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    try {
                        val pixels: Int =
                            getBitmapFromView(this).getPixel(event.x.toInt(), event.y.toInt())
                        val r = Color.red(pixels)
                        val g = Color.green(pixels)
                        val b = Color.blue(pixels)
                        currentColor = Color.rgb(r, g, b)
                        binding.tvHeader.setTextColor(currentColor)
                    } catch (e: Exception) {
                        Log.d("FNCA", "out of pic")
                    }
                }
                true
            }
        }
    }

    private fun validation(binding: FragmentNewCategoryTypeBinding): Boolean {
        binding.apply {
            return !etName.text.isNullOrEmpty()
        }
    }

    override fun onDestroy() {
        fragmentNewCategoryTypeBinding = null
        super.onDestroy()
    }
}