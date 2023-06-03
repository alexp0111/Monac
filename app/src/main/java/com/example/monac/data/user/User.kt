package com.example.monac.data.user

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.monac.util.UserType
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String = "",
    var password: String = "",
    val imageUri: String = "",
    val type: UserType = UserType.STANDART,
) : Parcelable {
}