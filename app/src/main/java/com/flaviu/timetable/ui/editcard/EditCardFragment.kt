package com.flaviu.timetable.ui.editcard

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
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
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val arguments = EditCardFragmentArgs.fromBundle(requireArguments())
        val editCardViewModelFactory = EditCardViewModelFactory(
            arguments.cardKey,
            dataSource,
            application
        )
        viewModel = ViewModelProvider(this, editCardViewModelFactory).get(EditCardViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        editTextTimeDialogInject(context, binding.startHourEditText)
        editTextTimeDialogInject(context, binding.endHourEditText)
        editTextWeekdayDialogInject(context, binding.weekdayEditText)
        binding.labelEditText.setOnClickListener{
            CardDatabase.getInstance(requireContext()).cardDatabaseDao.getAllLabels().observe(viewLifecycleOwner) {labels ->
                if (selectedTrueFalse.isEmpty())
                    selectedTrueFalse = BooleanArray(labels.size) {false}
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Choose your labels")
                    .setMultiChoiceItems(labels.map { it.name }.toTypedArray(), selectedTrueFalse) { _, which, isChecked ->
                        selectedTrueFalse[which] = isChecked
                    }.setPositiveButton("Ok"){ _, _ ->
                        labelList.clear()
                        for (i in labels.indices)
                            if (selectedTrueFalse[i])
                                labelList.add(labels[i])
                        binding.labelEditText.setText(labelsToString(labelList))
                    }.setNegativeButton("Cancel") { _, _ ->
                        return@setNegativeButton
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
                        labelList
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
                        labelList
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