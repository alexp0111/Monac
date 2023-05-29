package com.example.monac.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.monac.R
import com.example.monac.adapters.CardAdapter
import com.example.monac.data.Card
import com.example.monac.data.getActualContacts
import com.example.monac.databinding.FragmentHomeBinding
import kotlin.math.abs

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val TAG = "HOME_FRAGMENT"
    private var fragmentHomeBinding: FragmentHomeBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkPermission(Manifest.permission.READ_CONTACTS, 1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)
        fragmentHomeBinding = binding

        // Card Adapter
        initCardPager(binding)

        if (checkPermission(Manifest.permission.READ_CONTACTS, 1)) {
            val conts = getActualContacts(requireContext())
            conts?.forEach {
                Log.d(TAG, it.toString())
            }
        }

        //Transaction Adapter
        // TODO:
        //  1. Goto viewmodel
        //  2. Check for new contacts
        //  3. If there are new contacts - add info to DB
        //  4. Get list of users from DB & display it
    }

    private fun initCardPager(binding: FragmentHomeBinding) {
        var cardAdapter =
            CardAdapter(
                arrayListOf(
                    Card(name = "Заработная плата", value = 2848.0),
                    Card(name = "Стипендия", value = 0.001),
                    Card(name = "bob", value = 0.001)
                )
            )
        binding.vpCards.adapter = cardAdapter

        setUpTransformer(binding)
    }

    private fun setUpTransformer(binding: FragmentHomeBinding) {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.vpCards.setPageTransformer(transformer)
    }

    private fun checkPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
            return false
        } else {
            return true
        }
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }
}