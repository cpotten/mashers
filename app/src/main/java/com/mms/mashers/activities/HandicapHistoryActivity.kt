package com.mms.mashers.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mms.mashers.databinding.ActivityHandicapHistoryBinding
import com.mms.mashers.viewmodels.HandicapHistoryViewModel

class HandicapHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHandicapHistoryBinding
    private lateinit var viewModel: HandicapHistoryViewModel
    private lateinit var handicapHistoryAdapter: HandicapHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHandicapHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HandicapHistoryViewModel::class.java)

        initRecyclerView()
        loadHandicapHistory()
    }

    private fun initRecyclerView() {
        handicapHistoryAdapter = HandicapHistoryAdapter(emptyList())
        binding.handicapHistoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HandicapHistoryActivity)
            adapter = handicapHistoryAdapter
        }
    }

    private fun loadHandicapHistory() {
        // You need to implement the getHandicapHistoryItems() method in your ViewModel
        // to retrieve the list of HandicapHistoryItem objects from the database.
        viewModel.getHandicapHistoryItems().observe(this, { items ->
            handicapHistoryAdapter = HandicapHistoryAdapter(items)
            binding.handicapHistoryRecyclerView.adapter = handicapHistoryAdapter
        })
    }
}
