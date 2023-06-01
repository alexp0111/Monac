package com.example.monac.ui.main.mods

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

fun getBitmapFromView(view: View): Bitmap {
    val bitmap = Bitmap.createBitmap(
        view.width, view.height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}