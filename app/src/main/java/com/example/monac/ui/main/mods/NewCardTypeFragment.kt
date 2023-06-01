package com.example.monac.ui.main.mods

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentNewCardTypeBinding

class NewCardTypeFragment : Fragment(R.layout.fragment_new_card_type) {
    private var fragmentNewCardTypeBinding: FragmentNewCardTypeBinding? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewCardTypeBinding.bind(view)
        fragmentNewCardTypeBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.ivColorPicker.apply {
            setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    try {
                        val pixels: Int =
                            getBitmapFromView(this).getPixel(event.x.toInt(), event.y.toInt())
                        val r = Color.red(pixels)
                        val g = Color.green(pixels)
                        val b = Color.blue(pixels)
                        val hex = "#" + Integer.toHexString(pixels)
                        binding.tvHeader.setTextColor(Color.rgb(r, g, b))
                    } catch (e: Exception) {
                        Log.d("FNCA", "out of pic")
                    }
                }
                true
            }
        }
    }

    override fun onDestroy() {
        fragmentNewCardTypeBinding = null
        super.onDestroy()
    }
}