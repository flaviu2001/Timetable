package com.flaviu.timetable.ui.editlabel

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.database.Label
import com.flaviu.timetable.databinding.EditLabelFragmentBinding
import com.flaviu.timetable.setButtonColor
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class EditLabelFragment : Fragment() {
    private lateinit var binding: EditLabelFragmentBinding
    private lateinit var viewModel: EditLabelViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditLabelFragmentBinding.inflate(inflater)
        val database = CardDatabase.getInstance(requireContext()).cardDatabaseDao
        viewModel = ViewModelProvider(this, EditLabelViewModelFactory(database)).get(EditLabelViewModel::class.java)
        val adapter = LabelAdapter(this, CardDatabase.getInstance(requireContext()).cardDatabaseDao)
        binding.labelList.adapter = adapter
        viewModel.labels.observe(viewLifecycleOwner){
            adapter.data = it
        }
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val canEdit = sharedPref.getBoolean(getString(R.string.saved_edit_state), true)
        if (canEdit)
            binding.addLabel.visibility = Button.VISIBLE
        else binding.addLabel.visibility = Button.GONE
        binding.addLabel.setOnClickListener{
            val layout = layoutInflater.inflate(R.layout.add_label_layout, null)
            val alert = AlertDialog.Builder(requireContext()).setView(layout).show()
            layout.findViewById<Button>(R.id.button).setOnClickListener{
                val text = layout.findViewById<EditText>(R.id.labelName).text.toString()
                if (text.isEmpty()) {
                    Snackbar.make(requireView(), "The label name cannot be empty", Snackbar.LENGTH_SHORT).show()
                }else {
                    val job = Job()
                    val uiScope = CoroutineScope(Dispatchers.Main + job)
                    uiScope.launch {
                        withContext(Dispatchers.IO) {
                            database.insertLabel(Label(name = text))
                        }
                    }
                    alert.dismiss()
                }
            }
            layout.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                alert.dismiss()
            }
            setButtonColor(layout.findViewById(R.id.button), requireActivity())
            setButtonColor(layout.findViewById(R.id.cancel_button), requireActivity())
        }
        setButtonColor(binding.addLabel, requireActivity())
        return binding.root
    }
}