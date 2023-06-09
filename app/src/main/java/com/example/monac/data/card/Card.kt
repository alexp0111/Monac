package com.example.monac.data.card

import android.graphics.Color
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.monac.util.PaymentInstruments
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val userID: Long? = null,
    val name: String = "",
    var value: Double = 0.0,
    val marker: String = "$",
    val number: String = "0000",
    val comments: String = "",
    val paymentInstrument: PaymentInstruments = PaymentInstruments.MASTERCARD,
    val color: Int = Color.parseColor("#33691E")
): Parcelable