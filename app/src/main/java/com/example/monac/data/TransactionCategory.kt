package com.example.monac.data

import com.example.monac.util.PaymentType

data class TransactionCategory(
    val id: Long? = null,
    val name: String = "",
    val phone: String? = null,
    val uri: String? = null,
    val color: Int? = null,
    val type: PaymentType = PaymentType.TRANSACTION,
    val comments: String = ""
) {
    override fun toString(): String {
        return "TransactionUser(id='$id', name='$name', phone=$phone, uri=$uri, comments='$comments')"
    }
}