package com.flaviu.timetable.ui.editsubtask

import android.app.AlertDialog
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
        cardId = EditSubtaskFragmentArgs.fromBundle(requireArguments()).cardId
        val subtaskId = EditSubtaskFragmentArgs.fromBundle(requireArguments()).subtaskId
        val database = CardDatabase.getInstance(requireContext()).cardDatabaseDao
        viewModel = ViewModelProvider(this, EditSubtaskViewModelFactory(cardId, subtaskId, database)).get(EditSubtaskViewModel::class.java)
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
        setButtonColor(binding.deleteButton, requireActivity())
        setButtonColor(binding.editSubtaskButton, requireActivity())
        setButtonColor(binding.resetDeadlineButton, requireActivity())
        setButtonColor(binding.resetReminderButton, requireActivity())
        viewModel.subtask.observe(viewLifecycleOwner) {
            deadline = it.dueDate
            reminder = it.reminderDate
            if (reminder != null && reminder!! < Calendar.getInstance())
                reminder = null
            binding.descriptionEditText.setText(it.description)
            if (deadline != null) {
                binding.deadlineEditText.setText(prettyTimeString(deadline))
                binding.resetDeadlineButton.visibility = Button.VISIBLE
            } else binding.resetDeadlineButton.visibility = Button.GONE
            if (reminder != null && reminder!! > Calendar.getInstance()) {
                binding.reminderEditText.setText(prettyTimeString(reminder))
                binding.resetReminderButton.visibility = Button.VISIBLE
            } else binding.resetReminderButton.visibility = Button.GONE
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
            scheduleNotification(
                requireActivity(),
                description,
                viewModel.subtask.value!!.reminderId!!,
                reminder
            )
            val reminderId = viewModel.subtask.value!!.reminderId ?: NotificationIdManipulator.generateId(requireActivity())
            viewModel.updateSubtask(description, deadline, reminder, reminderId)
            this.findNavController().navigateUp()
            hideKeyboard(requireActivity())
        }
        binding.deleteButton.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Are you sure you wish to delete the subtask?")
                .setPositiveButton("Yes") { _, _ ->
                    this.findNavController().navigateUp()
                    hideKeyboard(requireActivity())
                    viewModel.deleteSubtask()
                }.setNegativeButton("No", null)
                .create().show()
        }
        return binding.root
    }

}