package com.flaviu.timetable.ui.editsubtask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.flaviu.timetable.*
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.EditSubtaskFragmentBinding
import java.util.*

class EditSubtaskFragment : Fragment() {
    private lateinit var binding: EditSubtaskFragmentBinding
    private lateinit var viewModel: EditSubtaskViewModel
    private var deadline: Calendar? = null
    private var reminder: Calendar? = null
    private var cardId = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditSubtaskFragmentBinding.inflate(inflater)
        val subtaskId = EditSubtaskFragmentArgs.fromBundle(requireArguments()).subtaskId
        val database = CardDatabase.getInstance(requireContext()).cardDatabaseDao
        viewModel = ViewModelProvider(this, EditSubtaskViewModelFactory(subtaskId, database)).get(EditSubtaskViewModel::class.java)
        binding.deadlineEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val c = Calendar.getInstance()
                    TimePickerDialog(requireContext(), { _, hour, minute ->
                        deadline = Calendar.getInstance()
                        deadline!!.set(year, month, day, hour, minute, 0)
                        binding.deadlineEditText.setText(prettyTimeString(deadline))
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),true).show()
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.reminderEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val c = Calendar.getInstance()
                    TimePickerDialog(requireContext(), { _, hour, minute ->
                        reminder = Calendar.getInstance()
                        reminder!!.set(year, month, day, hour, minute, 0)
                        binding.reminderEditText.setText(prettyTimeString(reminder))
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),true).show()
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.resetDeadlineButton.setOnClickListener {
            deadline = null
            binding.deadlineEditText.setText("")
        }
        binding.resetReminderButton.setOnClickListener {
            reminder = null
            binding.reminderEditText.setText("")
        }
        setButtonColor(binding.deleteButton, requireActivity())
        setButtonColor(binding.editSubtaskButton, requireActivity())
        setButtonColor(binding.resetDeadlineButton, requireActivity())
        setButtonColor(binding.resetReminderButton, requireActivity())
        viewModel.subtask.observe(viewLifecycleOwner) {
            deadline = it.dueDate
            reminder = it.reminderDate
            cardId = it.cardId
            binding.descriptionEditText.setText(it.description)
            if (deadline != null)
                binding.deadlineEditText.setText(prettyTimeString(deadline))
            if (reminder != null)
                binding.reminderEditText.setText(prettyTimeString(reminder))
        }
        binding.editSubtaskButton.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            if (description.isEmpty()) {
                Toast.makeText(requireContext(), "The description cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (reminder != null && reminder!! < Calendar.getInstance()) {
                Toast.makeText(requireContext(), "You must set the reminder in the future", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            scheduleNotification(requireActivity(), cardId, description, viewModel.subtask.value!!.reminderId!!, reminder)
            viewModel.updateSubtask(description, deadline, reminder)
            this.findNavController().navigateUp()
            hideKeyboard(requireActivity())
        }
        binding.deleteButton.setOnClickListener {
            viewModel.deleteSubtask()
            this.findNavController().navigateUp()
            hideKeyboard(requireActivity())
        }
        return binding.root
    }

}