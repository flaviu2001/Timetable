package com.flaviu.timetable.ui.addcard

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
import com.flaviu.timetable.databinding.AddCardFragmentBinding
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import java.util.*

class AddCardFragment : Fragment() {
    private lateinit var binding: AddCardFragmentBinding
    private lateinit var viewModel: AddCardViewModel
    private var itemColor = 0
    private var textColor = 0xFFFFFFFF.toInt()
    private var labelList = mutableListOf<Label>()
    private var selectedTrueFalse = BooleanArray(0)
    private var expirationDate: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_card_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val database = CardDatabase.getInstance(application).cardDatabaseDao
        val factory = AddCardViewModelFactory(database, application)
        viewModel = ViewModelProvider(this, factory).get(AddCardViewModel::class.java)
        editTextTimeDialogInject(context, binding.startHourEditText)
        editTextTimeDialogInject(context, binding.endHourEditText)
        editTextWeekdayDialogInject(context, binding.weekdayEditText)
        setButtonColor(binding.resetExpiration, requireActivity())
        binding.expirationPicker.setOnClickListener{
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val c = Calendar.getInstance()
                    val timePickerDialog = TimePickerDialog(requireContext(), { _, hour, minute ->
                        val expirationDate = Calendar.getInstance()
                        expirationDate.set(year, month, day, hour, minute, 0)
                        if (expirationDate < Calendar.getInstance()) {
                            Toast.makeText(
                                requireContext(),
                                requireContext().getText(R.string.time_error),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TimePickerDialog
                        }
                        binding.resetExpiration.visibility = Button.VISIBLE
                        binding.expirationPicker.setText(prettyTimeString(expirationDate))
                        this.expirationDate = expirationDate
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
                    timePickerDialog.updateTime(0, 0)
                    timePickerDialog.show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
        binding.resetExpiration.setOnClickListener{
            this.expirationDate = null
            binding.resetExpiration.visibility = Button.GONE
            binding.expirationPicker.setText("")
        }
        viewModel.allLabels.observe(viewLifecycleOwner) { labels ->
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
                    }.show()
            }
        }
        itemColor = getAccentColor(requireActivity())
        binding.colorEditText.setTextColor(itemColor)
        binding.colorEditText.setOnClickListener{
            val colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setColor(itemColor)
                .setPresets(preset_colors)
                .create()
            colorPickerDialog.setColorPickerDialogListener(object: ColorPickerDialogListener{
                override fun onColorSelected(dialogId: Int, color: Int) {
                    binding.colorEditText.setTextColor(color)
                    itemColor = color
                }
                override fun onDialogDismissed(dialogId: Int) {

                }
            })
            colorPickerDialog.show(childFragmentManager, "Choose a color")
        }
        binding.textColorEditText.setTextColor(textColor)
        binding.textColorEditText.setOnClickListener{
            val colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setColor(textColor)
                .setPresets(text_preset_colors)
                .create()
            colorPickerDialog.setColorPickerDialogListener(object: ColorPickerDialogListener{
                override fun onColorSelected(dialogId: Int, color: Int) {
                    binding.textColorEditText.setTextColor(color)
                    textColor = color
                }
                override fun onDialogDismissed(dialogId: Int) {

                }
            })
            colorPickerDialog.show(childFragmentManager, "Choose a color")
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            val start = binding.startHourEditText.text.toString()
            val finish = binding.endHourEditText.text.toString()
            val weekday = binding.weekdayEditText.text.toString()
            val place = binding.placeEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val info = binding.infoEditText.text.toString()
            try{
                viewModel.addCard(start, finish, weekday, place, name, info, itemColor, textColor, labelList, null, expirationDate)
                this.findNavController().navigateUp()
                hideKeyboard(activity as MainActivity)
            } catch (e: Exception) {
                Toast.makeText(context, e.localizedMessage!!.toString(), Toast.LENGTH_SHORT).show()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}