package com.flaviu.timetable.ui.editlabel

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.database.Label
import com.flaviu.timetable.databinding.LabelCardBinding
import com.flaviu.timetable.getAccentColor
import com.flaviu.timetable.setButtonColor
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class LabelAdapter(private val fragment: Fragment, private val database: CardDatabaseDao) : RecyclerView.Adapter<LabelHolder>() {
    var data = listOf<Label>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelHolder {
        return LabelHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LabelHolder, position: Int) {
        holder.bind(data[position], database, fragment)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class LabelHolder private constructor(private val binding: LabelCardBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(label: Label, database: CardDatabaseDao, fragment: Fragment) {
        binding.label = label
        val sharedPref = fragment.requireActivity().getPreferences(Context.MODE_PRIVATE)
        val canEdit = sharedPref.getBoolean(fragment.getString(R.string.saved_edit_state), true)
        if (canEdit) {
            binding.delete.visibility = ImageView.VISIBLE
            binding.visibleIcon.visibility = ImageView.VISIBLE
        } else {
            binding.delete.visibility = ImageView.GONE
            binding.visibleIcon.visibility = ImageView.GONE
        }
        binding.delete.setOnClickListener{
            AlertDialog.Builder(fragment.requireContext()).setMessage("Are you sure you want to delete the label? It will also delete all cards which do not have other labels than this one")
                .setPositiveButton("Yes") { _, _ ->
                    val job = Job()
                    val uiScope = CoroutineScope(Dispatchers.Main + job)
                    uiScope.launch {
                        database.deleteLabel(label.labelId)
                        database.deleteCardsWithoutLabels()
                    }
                }.setNegativeButton("No", null).show()
        }
        binding.visibleIcon.setOnClickListener{
            val job = Job()
            val uiScope = CoroutineScope(Dispatchers.Main + job)
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    database.switchLabelVisibility(label.labelId)
                }
            }
        }
        binding.card.setCardBackgroundColor(getAccentColor(fragment.requireActivity()))
        binding.card.setOnClickListener{
            if (canEdit) {
                val layout = fragment.layoutInflater.inflate(R.layout.edit_label_layout, null)
                val alert = AlertDialog.Builder(fragment.requireContext()).setView(layout).show()
                setButtonColor(layout.findViewById(R.id.button), fragment.requireActivity())
                setButtonColor(layout.findViewById(R.id.cancel_button), fragment.requireActivity())
                layout.findViewById<EditText>(R.id.labelName).setText(label.name)
                layout.findViewById<Button>(R.id.button).setOnClickListener{
                    val text = layout.findViewById<EditText>(R.id.labelName).text.toString()
                    if (text.isEmpty()) {
                        Snackbar.make(fragment.requireView(), "The label name cannot be empty", Snackbar.LENGTH_SHORT).show()
                    }else {
                        val job = Job()
                        val uiScope = CoroutineScope(Dispatchers.Main + job)
                        uiScope.launch {
                            withContext(Dispatchers.IO) {
                                val newLabel = Label(labelId = label.labelId, name = text)
                                database.updateLabel(newLabel)
                            }
                        }
                        alert.dismiss()
                    }
                }
                layout.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                    alert.dismiss()
                }
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): LabelHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = LabelCardBinding.inflate(layoutInflater, parent, false)
            return LabelHolder(binding)
        }
    }
}