package com.example.monac.ui.main.mods

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.monac.R
import com.example.monac.data.category.TransactionCategory
import com.example.monac.data.user.User
import com.example.monac.databinding.FragmentNewCategoryTypeBinding
import com.example.monac.util.PaymentType
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
    private var type = PaymentType.CATEGORY

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
        type = if ((arguments?.getString("name")
                ?: "") == "transaction"
        ) PaymentType.TRANSACTION else PaymentType.CATEGORY

        binding.apply {
            if (type == PaymentType.CATEGORY) {
                tvHeader.text = "Новая категория"
                tvPhone.visibility = View.GONE
                clPhone.visibility = View.GONE
            } else {
                tvHeader.text = "Новый перевод"
                tvPhone.visibility = View.VISIBLE
                clPhone.visibility = View.VISIBLE
            }
        }

        binding.etName.setText(name)

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.fab.setOnClickListener {
            if (validation(binding)) {
                val newCategory = TransactionCategory(
                    userID = currentUser.id,
                    name = binding.etName.text.toString(),
                    phone = if (type == PaymentType.TRANSACTION) binding.etPhone.text.toString() else null,
                    color = currentColor,
                    type = type,
                    comments = binding.etComments.text.toString()
                )
                categoryViewModel.updateCategory(newCategory) { isSuccess ->
                    if (isSuccess) {
                        Snackbar.make(
                            requireView(),
                            "Успешно добавлено",
                            Snackbar.LENGTH_LONG
                        ).show()

                        parentFragmentManager.popBackStack()
                    } else Snackbar.make(
                        requireView(),
                        "Не удалось добавить",
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
            return !etName.text.isNullOrEmpty() && (
                    (type == PaymentType.TRANSACTION
                            && !binding.etPhone.text.isNullOrEmpty())
                            || (type == PaymentType.CATEGORY)
                    )
        }
    }

    override fun onDestroy() {
        fragmentNewCategoryTypeBinding = null
        super.onDestroy()
    }
}