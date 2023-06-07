package com.example.monac.util

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.monac.data.user.User
import com.google.gson.Gson

fun setUpUser(requireActivity: FragmentActivity, user: User) {
    val prefs =
        requireActivity.getSharedPreferences(
            "current_user",
            Context.MODE_PRIVATE
        )
    prefs.edit().putString("user", Gson().toJson(user)).apply()
}

fun getCurrentUser(requireActivity: FragmentActivity): User {
    val prefs =
        requireActivity.getSharedPreferences(
            "current_user",
            Context.MODE_PRIVATE
        )
    if (prefs.getString("user", "")?.isEmpty() == true) return User()
    return Gson().fromJson(prefs.getString("user", ""), User::class.java)
}

fun setUpLogInType(requireActivity: FragmentActivity, needsPassword: Boolean) {
    val prefs =
        requireActivity.getSharedPreferences(
            "current_user",
            Context.MODE_PRIVATE
        )
    prefs.edit().putBoolean("log_in", needsPassword).apply()
}

fun getLogInType(requireActivity: FragmentActivity): Boolean {
    val prefs =
        requireActivity.getSharedPreferences(
            "current_user",
            Context.MODE_PRIVATE
        )
    return prefs.getBoolean("log_in", true)
}

fun clearSP(requireActivity: FragmentActivity) {
    val prefs =
        requireActivity.getSharedPreferences(
            "current_user",
            Context.MODE_PRIVATE
        )

    prefs.edit().clear().apply()
}

fun isSPClear(requireActivity: FragmentActivity): Boolean {
    val prefs =
        requireActivity.getSharedPreferences(
            "current_user",
            Context.MODE_PRIVATE
        )

    return prefs.all.isEmpty()
}