package com.example.monac.data

import android.graphics.Color
import com.example.monac.util.PaymentInstruments

data class Card(
    val id: String = "-1",
    val userID: String = "",
    val name: String = "",
    val value: Double = 0.0,
    val marker: String = "$",
    val number: String = "**** **** **** 0000",
    val comments: String = "",
    val paymentInstrument: PaymentInstruments = PaymentInstruments.MASTERCARD,
    val color: Int = Color.parseColor("#33691E")
)