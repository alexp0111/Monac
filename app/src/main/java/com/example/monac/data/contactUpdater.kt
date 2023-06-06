package com.example.monac.data

import android.content.Context
import android.graphics.Color
import android.provider.ContactsContract
import com.example.monac.data.category.TransactionCategory
import com.example.monac.util.PaymentType
import kotlin.math.absoluteValue

fun getActualContacts(context: Context, userID: Long?): ArrayList<TransactionCategory>? {
    val list = hashSetOf<TransactionCategory>()
    val contacts = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
    )
        ?: return null

    while (contacts.moveToNext()) {
        val name =
            contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME).absoluteValue)
        val phone =
            contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER).absoluteValue)

        list.add(
            TransactionCategory(
                userID = userID,
                name = name,
                phone = phone,
                color = Color.parseColor("#212121"),
                type = PaymentType.TRANSACTION
            )
        )
    }
    contacts.close()

    return ArrayList(list)
}