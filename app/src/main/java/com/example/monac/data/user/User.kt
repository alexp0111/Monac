package com.example.monac.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.monac.util.UserType

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String = "",
    val password: String = "",
    val imageUri: String = "",
    val type: UserType = UserType.STANDART,
) {
}