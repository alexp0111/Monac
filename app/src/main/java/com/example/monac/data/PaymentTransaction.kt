package com.example.monac.data

import com.example.monac.util.PaymentType
import java.time.LocalDateTime

data class PaymentTransaction(
    val transactionID: String = "",
    val value: Double = 0.0,
    val cardID: String = "",
    val type: PaymentType = PaymentType.CATEGORY,
    val typeID: String = "", // category ID
    val date: LocalDateTime = LocalDateTime.now(),
    val comments: String = ""
)