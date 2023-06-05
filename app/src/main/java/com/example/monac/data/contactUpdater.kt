package com.example.monac.data

import android.content.Context
import android.provider.ContactsContract
import com.example.monac.data.category.TransactionCategory
import kotlin.math.absoluteValue

fun getActualContacts(context: Context): ArrayList<TransactionCategory>? {
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
        val uri =
            contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI).absoluteValue)

        list.add(TransactionCategory(name = name, phone = phone, uri = uri))
    }
    contacts.close()

    return ArrayList(list)
}