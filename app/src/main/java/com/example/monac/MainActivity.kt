package com.example.monac

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monac.ui.main.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        * TODO:
        *  [1] Sync category & addTransaction type (+\-)
        *  [2] Transactions with users
        *  [3] Deletion of transactions
        *  [4] All mechanic for info fragment
        *  [5] Role model (limit logic)
        *  [6] Sync card total with transactions
        *  (--7--) Add type field to transactions in user
        *  [8] Maybe info fragment for each transaction (delete & comments)
        * */
    }
}