package com.example.monac.data.transaction

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.monac.util.PaymentType
import com.example.monac.util.TransactionType
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
@Entity(tableName = "transactions")
data class PaymentTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val userID: Long? = null,
    val value: Double = 0.0,
    val cardID: Long? = null,
    val typeID: Long? = null, // category ID
    val date: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val time: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
    val type: TransactionType = TransactionType.EXPENSES,
    val comments: String = ""
) : Parcelable