package com.example.monac.ui.main.mods

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.monac.R
import com.example.monac.data.card.Card
import com.example.monac.databinding.FragmentNewCardTypeBinding
import com.example.monac.util.PaymentInstruments
import com.example.monac.util.getCurrentUser
import com.example.monac.view_model.CardViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCardTypeFragment : Fragment(R.layout.fragment_new_card_type) {
    private var fragmentNewCardTypeBinding: FragmentNewCardTypeBinding? = null

    private val cardViewModel: CardViewModel by viewModels()

    private var currentColor: Int = Color.WHITE
    private var currentMarker = "$"
    private var currentPaymentInstrument = PaymentInstruments.MASTERCARD

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewCardTypeBinding.bind(view)
        fragmentNewCardTypeBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
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

        // Payment instrument picker
        setUpPaymentInstrumentPicker(binding)
        setUpMarkerPicker(binding)

        binding.fab.setOnClickListener {
            if (validation(binding)) {
                val card = Card(
                    userID = getCurrentUser(requireActivity()).id,
                    name = binding.etName.text.toString(),
                    marker = currentMarker,
                    number = binding.etCardNumber.text.toString(),
                    comments = binding.etComments.text.toString(),
                    paymentInstrument = currentPaymentInstrument,
                    color = currentColor
                )
                cardViewModel.updateCard(card) { isSuccess ->
                    if (isSuccess) {
                        Snackbar.make(
                            requireView(),
                            "карта добавлена",
                            Snackbar.LENGTH_LONG
                        ).show()

                        parentFragmentManager.popBackStack()
                    } else Snackbar.make(
                        requireView(),
                        "Не удалось добавить карту",
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

    private fun setUpMarkerPicker(binding: FragmentNewCardTypeBinding) {
        binding.apply {
            tvUsd.setOnClickListener {
                currentMarker = "$"
                tvUsd.setTextColor(resources.getColor(R.color.picker_text_on, resources.newTheme()))
                tvRub.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
                tvEur.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
            }
            tvRub.setOnClickListener {
                currentMarker = "₽"
                tvUsd.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
                tvRub.setTextColor(resources.getColor(R.color.picker_text_on, resources.newTheme()))
                tvEur.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
            }
            tvEur.setOnClickListener {
                currentMarker = "€"
                tvUsd.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
                tvRub.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
                tvEur.setTextColor(resources.getColor(R.color.picker_text_on, resources.newTheme()))
            }
        }
    }

    private fun setUpPaymentInstrumentPicker(binding: FragmentNewCardTypeBinding) {
        binding.apply {
            tvMastercard.setOnClickListener {
                currentPaymentInstrument = PaymentInstruments.MASTERCARD
                tvMastercard.setTextColor(
                    resources.getColor(
                        R.color.picker_text_on,
                        resources.newTheme()
                    )
                )
                tvMir.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
            }
            tvMir.setOnClickListener {
                currentPaymentInstrument = PaymentInstruments.MIR
                tvMastercard.setTextColor(
                    resources.getColor(
                        R.color.picker_text_off,
                        resources.newTheme()
                    )
                )
                tvMir.setTextColor(resources.getColor(R.color.picker_text_on, resources.newTheme()))
            }
        }
    }

    private fun validation(binding: FragmentNewCardTypeBinding): Boolean {
        binding.apply {
            return (!etName.text.isNullOrEmpty()
                    && !etCardNumber.text.isNullOrEmpty()
                    && etCardNumber.text.length == 4
                    && etCardNumber.text.isDigitsOnly())
        }
    }

    override fun onDestroy() {
        fragmentNewCardTypeBinding = null
        super.onDestroy()
    }
}