package com.example.monac.data.category

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.monac.util.PaymentType
import com.example.monac.util.TransactionType
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "categories")
data class TransactionCategory(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val userID: Long? = null,
    val name: String = "",
    val phone: String? = null,
    val color: Int? = null,
    val type: PaymentType = PaymentType.TRANSACTION,
    val comments: String = ""
): Parcelable {
    override fun toString(): String {
        return "TransactionCategory(id=$id, userID=$userID, name='$name', phone=$phone, color=$color, type=$type, comments='$comments')"
    }
}