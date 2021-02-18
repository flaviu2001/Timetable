package com.flaviu.timetable.ui.editcard

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.flaviu.timetable.*
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.database.Label
import com.flaviu.timetable.databinding.EditCardFragmentBinding
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import java.util.*

class EditCardFragment : Fragment() {

    private lateinit var viewModel: EditCardViewModel
    private lateinit var binding: EditCardFragmentBinding
    private var itemColor = 0
    private var textColor = 0
    private var labelList = mutableListOf<Label>()
    private var selectedTrueFalse = BooleanArray(0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_card_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val database = CardDatabase.getInstance(application).cardDatabaseDao
        val arguments = EditCardFragmentArgs.fromBundle(requireArguments())
        val cardId = arguments.cardKey
        val editCardViewModelFactory = EditCardViewModelFactory(
            cardId,
            database,
            application
        )
        viewModel = ViewModelProvider(this, editCardViewModelFactory).get(EditCardViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        editTextTimeDialogInject(context, binding.startHourEditText)
        editTextTimeDialogInject(context, binding.endHourEditText)
        editTextWeekdayDialogInject(context, binding.weekdayEditText)
        viewModel.allLabels.observe(viewLifecycleOwner) {labels ->
            binding.labelEditText.setOnClickListener{
                if (selectedTrueFalse.size != labels.size)
                    selectedTrueFalse = BooleanArray(labels.size) {false}
                AlertDialog.Builder(requireContext()).setTitle("Choose your labels")
                    .setMultiChoiceItems(labels.map { it.name }.toTypedArray(), selectedTrueFalse) { _, which, isChecked ->
                        selectedTrueFalse[which] = isChecked
                    }.setPositiveButton("Ok"){ _, _ ->
                        labelList.clear()
                        for (i in labels.indices)
                            if (selectedTrueFalse[i])
                                labelList.add(labels[i])
                        binding.labelEditText.setText(labelsToString(labelList))
                    }.setNegativeButton("Cancel", null)
                    .setNeutralButton("New label"){ _, _ ->
                        val layout = layoutInflater.inflate(R.layout.add_label_layout, null)
                        val alert = AlertDialog.Builder(requireContext()).setView(layout).show()
                        layout.findViewById<Button>(R.id.button).setOnClickListener{
                            val text = layout.findViewById<EditText>(R.id.labelName).text.toString()
                            if (text.isEmpty()) {
                                Toast.makeText(requireContext(), "The label name cannot be empty", Toast.LENGTH_SHORT).show()
                            }else {
                                viewModel.insertLabel(text)
                                alert.dismiss()
                            }
                        }
                        layout.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                            alert.dismiss()
                        }
                        setButtonColor(layout.findViewById(R.id.button), requireActivity())
                        setButtonColor(layout.findViewById(R.id.cancel_button), requireActivity())
                    }.create().show()
            }
        }
        viewModel.mediator.observe(viewLifecycleOwner) {
            selectedTrueFalse = it
            if (viewModel.labelsOfCard.value != null) {
                labelList = viewModel.labelsOfCard.value!!.toMutableList()
                binding.labelEditText.setText(labelsToString(labelList))
            }
        }
        viewModel.card.observe(viewLifecycleOwner, {
            itemColor = it.color
            textColor = it.textColor
            binding.colorEditText.setTextColor(itemColor)
            binding.textColorEditText.setTextColor(textColor)
        })
        viewModel.labelsOfCard.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.labelEditText.setText("")
            }else {
                binding.labelEditText.setText(labelsToString(it))
                if (it.size == 1) {
                    binding.labelTextView.setText(R.string.one_label)
                } else binding.labelTextView.setText(R.string.more_labels)
            }
        }
        binding.colorEditText.setOnClickListener{
            val colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setColor(itemColor)
                .setPresets(preset_colors)
                .create()
            colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, color: Int) {
                    binding.colorEditText.setTextColor(color)
                    itemColor = color
                }

                override fun onDialogDismissed(dialogId: Int) {

                }
            })
            colorPickerDialog.show(childFragmentManager, "Choose a color")
        }
        binding.textColorEditText.setOnClickListener{
            val colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setColor(textColor)
                .setPresets(text_preset_colors)
                .create()
            colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, color: Int) {
                    binding.textColorEditText.setTextColor(color)
                    textColor = color
                }

                override fun onDialogDismissed(dialogId: Int) {

                }
            })
            colorPickerDialog.show(childFragmentManager, "Choose a color")
        }
        binding.subtasksButton.setOnClickListener {
            this.findNavController().navigate(EditCardFragmentDirections.actionEditCardFragmentToSubtaskFragment(LongArray(1){cardId}))
        }
        binding.notificationButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val c = Calendar.getInstance()
                    val timePickerDialog = TimePickerDialog(requireContext(), { _, hour, minute ->
                        val reminderDate = Calendar.getInstance()
                        reminderDate.set(year, month, day, hour, minute, 0)
                        if (reminderDate < Calendar.getInstance()) {
                            Toast.makeText(requireContext(), requireContext().getText(R.string.time_error), Toast.LENGTH_SHORT).show()
                            return@TimePickerDialog
                        }
                        var reminderId = viewModel.card.value?.reminderId
                        if (reminderId == null)
                            reminderId = NotificationIdManipulator.generateId(requireActivity())
                        val card = viewModel.card.value!!
                        scheduleNotification(requireActivity(), cardId, null, reminderId, reminderDate)
                        viewModel.editCard(card.timeBegin, card.timeEnd,
                            resources.getStringArray(R.array.weekdays)[card.weekday], card.place, card.name, card.info, card.color, card.textColor, labelList, reminderDate, reminderId)
                        Toast.makeText(requireContext(), requireContext().getText(R.string.notification_alert), Toast.LENGTH_SHORT).show()
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),true)
                    timePickerDialog.updateTime(0, 0)
                    timePickerDialog.show()
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
        binding.resetNotification.setOnClickListener {
            var reminderId = viewModel.card.value?.reminderId
            if (reminderId == null)
                reminderId = NotificationIdManipulator.generateId(requireActivity())
            val card = viewModel.card.value!!
            scheduleNotification(requireActivity(), cardId, null, reminderId!!, null)
            viewModel.editCard(card.timeBegin, card.timeEnd,
                resources.getStringArray(R.array.weekdays)[card.weekday], card.place, card.name, card.info, card.color, card.textColor, labelList, null, reminderId)
            Toast.makeText(requireContext(), requireContext().getText(R.string.reset_alert), Toast.LENGTH_SHORT).show()
        }
        setButtonColor(binding.subtasksButton, requireActivity())
        setButtonColor(binding.notificationButton, requireActivity())
        setButtonColor(binding.resetNotification, requireActivity())
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add -> {
                val start = binding.startHourEditText.text.toString()
                val finish = binding.endHourEditText.text.toString()
                val weekday = binding.weekdayEditText.text.toString()
                val place = binding.placeEditText.text.toString()
                val name = binding.nameEditText.text.toString()
                val info = binding.infoEditText.text.toString()
                val reminderId = NotificationIdManipulator.generateId(requireActivity())
                try {
                    viewModel.cloneCard(
                        start,
                        finish,
                        weekday,
                        place,
                        name,
                        info,
                        itemColor,
                        textColor,
                        labelList,
                        null,
                        reminderId
                    )
                    this.findNavController().navigateUp()
                    hideKeyboard(activity as MainActivity)
                } catch (e: Exception) {
                    Toast.makeText(context, e.localizedMessage!!.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                true
            }
            R.id.edit -> {
                val start = binding.startHourEditText.text.toString()
                val finish = binding.endHourEditText.text.toString()
                val weekday = binding.weekdayEditText.text.toString()
                val place = binding.placeEditText.text.toString()
                val name = binding.nameEditText.text.toString()
                val info = binding.infoEditText.text.toString()
                val reminderDate = viewModel.card.value?.reminderDate
                val reminderId = viewModel.card.value?.reminderId
                try {
                    viewModel.editCard(
                        start,
                        finish,
                        weekday,
                        place,
                        name,
                        info,
                        itemColor,
                        textColor,
                        labelList,
                        reminderDate,
                        reminderId
                    )
                    this.findNavController().navigateUp()
                    hideKeyboard(activity as MainActivity)
                } catch (e: Exception) {
                    Toast.makeText(context, e.localizedMessage!!.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                true
            }
            R.id.delete -> {
                viewModel.onDeleteButtonPressed()
                this.findNavController().navigateUp()
                hideKeyboard(activity as MainActivity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}