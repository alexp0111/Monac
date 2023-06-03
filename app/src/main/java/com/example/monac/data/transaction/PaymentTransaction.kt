package com.example.monac.data.transaction

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.monac.util.PaymentType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "transactions")
data class PaymentTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val userID: Long? = null,
    val value: Double = 0.0,
    val cardID: Long? = null,
    val type: PaymentType = PaymentType.CATEGORY,
    val typeID: Long? = null, // category ID
    val date: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
    val comments: String = ""
)