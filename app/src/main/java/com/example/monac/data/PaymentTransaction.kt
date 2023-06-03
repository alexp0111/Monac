package com.example.monac.data

import com.example.monac.util.PaymentType
import java.time.LocalDateTime

data class PaymentTransaction(
    val id: Long? = null,
    val userID:  Long? = null,
    val value: Double = 0.0,
    val cardID:  Long? = null,
    val type: PaymentType = PaymentType.CATEGORY,
    val typeID:  Long? = null, // category ID
    val date: LocalDateTime = LocalDateTime.now(),
    val comments: String = ""
)