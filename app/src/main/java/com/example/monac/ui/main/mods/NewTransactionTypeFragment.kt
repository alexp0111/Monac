package com.example.monac.ui.main.mods

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.example.monac.R
import com.example.monac.databinding.FragmentNewTransactionTypeBinding

class NewTransactionTypeFragment : Fragment(R.layout.fragment_new_transaction_type) {
    private var fragmentNewTransactionTypeBinding: FragmentNewTransactionTypeBinding? = null

    private var currentColor: Int = Color.WHITE

    //TODO:::::

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewTransactionTypeBinding.bind(view)
        fragmentNewTransactionTypeBinding = binding

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
                        currentColor = Color.rgb(r, g, b)
                        binding.tvHeader.setTextColor(currentColor)
                    } catch (e: Exception) {
                        Log.d("FNCA", "out of pic")
                    }
                }
                true
            }
        }
    }

    override fun onDestroy() {
        fragmentNewTransactionTypeBinding = null
        super.onDestroy()
    }
}