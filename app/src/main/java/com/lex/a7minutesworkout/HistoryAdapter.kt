package com.lex.a7minutesworkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lex.a7minutesworkout.databinding.ItemHistoryRowBinding

class HistoryAdapter(private var historyList: ArrayList<HistoryEntity>):RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistoryRowBinding):RecyclerView.ViewHolder(binding.root){
        val clMain = binding.clMain
        val tvPosition = binding.tvPosition
        val tvItemDate = binding.tvItemDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = historyList[position]

        holder.tvPosition.text = item.position.toString()
        holder.tvItemDate.text = item.date.toString()

        if (position % 2 == 0){
            holder.clMain.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
        }else{
            holder.clMain.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}