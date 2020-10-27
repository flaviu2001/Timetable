package com.flaviu.timetable.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.flaviu.timetable.*
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.SettingsFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener


class SettingsFragment : Fragment() {
    private lateinit var binding: SettingsFragmentBinding
    private lateinit var viewModel: SettingsViewModel

    private fun setupViewModel() {
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val factory = SettingsViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun setupClickListeners() {
        binding.clearButton.setOnClickListener{ view: View ->
            AlertDialog.Builder(this.context)
                .setTitle("Are you sure you want to clear all data?")
                .setPositiveButton(R.string.yes
                ) { _, _ ->
                    viewModel.clearData()
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Cleared all entries",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    view.findNavController().navigateUp()
                }
                .setNegativeButton(R.string.no, null).show()
        }
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        var canEdit = sharedPref.getBoolean(getString(R.string.saved_edit_state), true)
        if (canEdit) {
            binding.toggleButton.text = getString(R.string.lock)
        }else binding.toggleButton.text = getString(R.string.unlock)
        binding.toggleButton.setOnClickListener{
            canEdit = sharedPref.getBoolean(getString(R.string.saved_edit_state), true)
            if (canEdit) {
                binding.toggleButton.text = getString(R.string.unlock)
            }else binding.toggleButton.text = getString(R.string.lock)
            with(sharedPref.edit()) {
                putBoolean(getString(R.string.saved_edit_state), !canEdit)
                apply()
            }
        }
        binding.setBackgroundButton.setOnClickListener{
            val colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setColor(getBackgroundColor(requireActivity()))
                .setPresets(preset_colors)
                .create()
            colorPickerDialog.setColorPickerDialogListener(object: ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, color: Int) {
                    updateBackgroundColor(requireActivity(), color)
                    setBackgroundColor(requireActivity())
                }
                override fun onDialogDismissed(dialogId: Int) {

                }
            })
            colorPickerDialog.show(childFragmentManager, "Choose a color")
        }
        binding.setAccentButton.setOnClickListener{
            val colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setColor(getAccentColor(requireActivity()))
                .setPresets(preset_colors)
                .create()
            colorPickerDialog.setColorPickerDialogListener(object: ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, color: Int) {
                    updateAccentColor(requireActivity(), color)
                    setAccentColor(requireActivity())
                    refreshButtons()
                }
                override fun onDialogDismissed(dialogId: Int) {

                }
            })
            colorPickerDialog.show(childFragmentManager, "Choose a color")
        }
    }

    fun refreshButtons() {
        listOf(binding.setAccentButton,
            binding.setBackgroundButton,
            binding.clearButton,
            binding.toggleButton).forEach{
            setButtonColor(it, requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)
        setupViewModel()
        setupClickListeners()
        refreshButtons()
        return binding.root
    }
}