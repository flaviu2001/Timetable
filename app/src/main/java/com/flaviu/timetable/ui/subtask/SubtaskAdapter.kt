package com.flaviu.timetable.ui.subtask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.database.Subtask
import com.flaviu.timetable.databinding.SubtaskCardBinding

class SubtaskAdapter(private val lifecycleOwner: LifecycleOwner, private val database: CardDatabaseDao, private val subtaskListener: SubtaskListener) : RecyclerView.Adapter<SubtaskHolder>() {
    var data = listOf<Subtask>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskHolder {
        return SubtaskHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SubtaskHolder, position: Int) {
        holder.bind(data[position], lifecycleOwner, database, subtaskListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class SubtaskHolder private constructor(private val binding: SubtaskCardBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(subtask: Subtask, lifecycleOwner: LifecycleOwner, database: CardDatabaseDao, subtaskListener: SubtaskListener) {
        binding.subtask = subtask
        binding.subtaskListener = subtaskListener
        database.getCard(subtask.cardId).observe(lifecycleOwner) {
            if (it != null)
                binding.card = it
        }
    }

    companion object {
        fun from(parent: ViewGroup): SubtaskHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SubtaskCardBinding.inflate(layoutInflater, parent, false)
            return SubtaskHolder(binding)
        }
    }
}

class SubtaskListener(val clickListener: (subtaskId: Long) -> Unit) {
    fun onClick(subtask: Subtask) = clickListener(subtask.subtaskId)
}
