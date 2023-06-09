package com.example.monac.ui.main.mods

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
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

    private var currentCard: Card? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewCardTypeBinding.bind(view)
        fragmentNewCardTypeBinding = binding

        // Payment instrument picker
        setUpPaymentInstrumentPicker(binding)
        setUpMarkerPicker(binding)

        currentCard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelable("card", Card::class.java)
        else arguments?.getParcelable("card")

        binding.apply {
            if (currentCard != null) {
                tvHeader.text = currentCard!!.name
                currentCard!!.color.let {
                    tvHeader.setTextColor(it)
                    currentColor = it
                }
                etName.setText(currentCard!!.name)
                etCardNumber.setText(currentCard!!.number)

                if (currentCard!!.paymentInstrument == PaymentInstruments.MIR) tvMir.performClick()
                else tvMastercard.performClick()

                when (currentCard!!.marker) {
                    "$" -> tvUsd.performClick()
                    "₽" -> tvRub.performClick()
                    else -> tvEur.performClick()
                }

                etComments.setText(currentCard!!.comments)

            }
        }

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.ivColorPicker.apply {
            setOnTouchListener { _, event ->
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

        binding.fab.setOnClickListener {
            if (validation(binding)) {
                val card = Card(
                    id = if (currentCard != null) currentCard!!.id else null,
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
                            getString(R.string.card_added),
                            Snackbar.LENGTH_LONG
                        ).show()

                        parentFragmentManager.popBackStack()
                    } else Snackbar.make(
                        requireView(),
                        getString(R.string.card_add_error),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.fill_all_fields),
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