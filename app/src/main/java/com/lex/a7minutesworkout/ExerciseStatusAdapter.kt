package com.lex.a7minutesworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lex.a7minutesworkout.databinding.ItemExerciseStatusRvBinding

class ExerciseStatusAdapter(val itemsList: ArrayList<ExerciseModel>): RecyclerView.Adapter<ExerciseStatusAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemExerciseStatusRvBinding): RecyclerView.ViewHolder(binding.root){
        val tvItem = binding.tvItemStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExerciseStatusRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: ExerciseModel = itemsList[position]
        holder.tvItem.text = model.id.toString()

        when{
            model.isSelected ->{
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context,
                R.drawable.item_exercise_status_selected_color_accent)

                holder.tvItem.setTextColor(Color.parseColor("#000000"))
            }

            model.isCompleted ->{
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.item_circular_color_accent_bg)

                holder.tvItem.setTextColor(Color.parseColor("#FFFFFF"))
            }
            else ->{
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.item_circular_exercise_status_bg)

                holder.tvItem.setTextColor(Color.parseColor("#212121"))
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

}