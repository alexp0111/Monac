package com.example.monac.data.card

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.monac.util.PaymentInstruments

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val userID: Long? = null,
    val name: String = "",
    val value: Double = 0.0,
    val marker: String = "$",
    val number: String = "0000",
    val comments: String = "",
    val paymentInstrument: PaymentInstruments = PaymentInstruments.MASTERCARD,
    val color: Int = Color.parseColor("#33691E")
)