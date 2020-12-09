package com.flaviu.timetable.editcard

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.flaviu.timetable.*
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.EditCardFragmentBinding
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener

class EditCardFragment : Fragment() {

    private lateinit var viewModel: EditCardViewModel
    private lateinit var binding: EditCardFragmentBinding
    private var itemColor = 0
    private var textColor = 0

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
        viewModel.card.observe(viewLifecycleOwner, {
            itemColor = it.color
            textColor = it.textColor
            binding.colorEditText.setTextColor(itemColor)
            binding.textColorEditText.setTextColor(textColor)
        })
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
                val label = binding.labelEditText.text.toString()
                try {
                    viewModel.cloneCard(
                        start,
                        finish,
                        weekday,
                        place,
                        name,
                        info,
                        label,
                        itemColor,
                        textColor
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
                val label = binding.labelEditText.text.toString()
                try {
                    viewModel.editCard(
                        start,
                        finish,
                        weekday,
                        place,
                        name,
                        info,
                        label,
                        itemColor,
                        textColor
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