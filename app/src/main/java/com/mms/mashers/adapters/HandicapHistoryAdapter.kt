package com.mms.mashers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mms.mashers.databinding.HandicapHistoryItemBinding

class HandicapHistoryAdapter(private val handicapHistoryItems: List<HandicapHistoryItem>) :
    RecyclerView.Adapter<HandicapHistoryAdapter.HandicapHistoryViewHolder>() {

    class HandicapHistoryViewHolder(private val binding: HandicapHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(handicapHistoryItem: HandicapHistoryItem) {
            binding.datePlayed.text = handicapHistoryItem.datePlayed
            binding.teeColour.text = handicapHistoryItem.teeColour
            binding.currentHandicap.text = handicapHistoryItem.currentHandicap.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HandicapHistoryViewHolder {
        val binding =
            HandicapHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HandicapHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HandicapHistoryViewHolder, position: Int) {
        holder.bind(handicapHistoryItems[position])
    }

    override fun getItemCount(): Int {
        return handicapHistoryItems.size
    }
}
