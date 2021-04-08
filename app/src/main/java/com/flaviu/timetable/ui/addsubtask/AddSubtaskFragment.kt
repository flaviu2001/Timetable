package com.flaviu.timetable.ui.addsubtask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.flaviu.timetable.*
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.AddSubtaskFragmentBinding
import java.util.*

class AddSubtaskFragment : Fragment() {
    private lateinit var binding: AddSubtaskFragmentBinding
    private lateinit var viewModel: AddSubtaskViewModel
    private var deadline: Calendar? = null
    private var reminder: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddSubtaskFragmentBinding.inflate(inflater)
        val database = CardDatabase.getInstance(requireContext()).cardDatabaseDao
        val application = requireNotNull(this.activity).application
        val cardId = AddSubtaskFragmentArgs.fromBundle(requireArguments()).cardId
        viewModel = ViewModelProvider(this, AddSubtaskViewModelFactory(database, application, cardId)).get(AddSubtaskViewModel::class.java)
        viewModel.card.observe(viewLifecycleOwner){
            binding.card = it
        }
        binding.cardLayout.weekdaysTextView.visibility = TextView.VISIBLE
        binding.deadlineEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val c = Calendar.getInstance()
                    val timePickerDialog = TimePickerDialog(requireContext(), { _, hour, minute ->
                        deadline = Calendar.getInstance()
                        deadline!!.set(year, month, day, hour, minute, 0)
                        binding.deadlineEditText.setText(prettyTimeString(deadline))
                        binding.resetDeadlineButton.visibility = Button.VISIBLE
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),true)
                    timePickerDialog.updateTime(0, 0)
                    timePickerDialog.show()
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
        binding.reminderEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val c = Calendar.getInstance()
                    val timePickerDialog = TimePickerDialog(requireContext(), { _, hour, minute ->
                        reminder = Calendar.getInstance()
                        reminder!!.set(year, month, day, hour, minute, 0)
                        binding.reminderEditText.setText(prettyTimeString(reminder))
                        binding.resetReminderButton.visibility = Button.VISIBLE
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),true)
                    timePickerDialog.updateTime(0, 0)
                    timePickerDialog.show()
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
        binding.resetDeadlineButton.setOnClickListener {
            deadline = null
            binding.deadlineEditText.setText("")
            binding.resetDeadlineButton.visibility = Button.GONE
        }
        binding.resetReminderButton.setOnClickListener {
            reminder = null
            binding.reminderEditText.setText("")
            binding.resetReminderButton.visibility = Button.GONE
        }
        setButtonColor(binding.addSubtaskButton, requireActivity())
        setButtonColor(binding.resetDeadlineButton, requireActivity())
        setButtonColor(binding.resetReminderButton, requireActivity())
        binding.addSubtaskButton.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            if (description.isEmpty()) {
                Toast.makeText(requireContext(), "The description cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (reminder != null && reminder!! < Calendar.getInstance()) {
                Toast.makeText(requireContext(), "You must set the reminder in the future", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addSubtask(cardId, description, deadline, reminder)
            this.findNavController().navigateUp()
            hideKeyboard(requireActivity())
        }
        return binding.root
    }

}