package com.example.monac.data

data class TransactionUser(
    val id: String = "-1",
    val name: String = "",
    val phone: String?,
    val uri: String?,
    val comments: String = ""
) {
    override fun toString(): String {
        return "TransactionUser(id='$id', name='$name', phone=$phone, uri=$uri, comments='$comments')"
    }
}