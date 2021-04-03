package com.flaviu.timetable

import android.app.AlertDialog
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.database.CardDatabaseDao
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.coroutines.*

class PreferencesFragment : PreferenceFragmentCompat() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    lateinit var database: CardDatabaseDao

    private fun clearData() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.clearCards()
                database.clearLabels()
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        database = CardDatabase.getInstance(requireNotNull(this.activity).application).cardDatabaseDao
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>("clear_data")!!.setOnPreferenceClickListener {
            AlertDialog.Builder(this.context)
                .setTitle("Are you sure you want to clear all data?")
                .setPositiveButton(R.string.yes
                ) { _, _ ->
                    clearData()
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Cleared all entries",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    requireView().findNavController().navigateUp()
                }
                .setNegativeButton(R.string.no, null).show()
            true
        }
        findPreference<Preference>("bgd_color")!!.setOnPreferenceClickListener {
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
            true
        }
        findPreference<Preference>("accent_color")!!.setOnPreferenceClickListener {
            val colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setColor(getAccentColor(requireActivity()))
                .setPresets(preset_colors)
                .create()
            colorPickerDialog.setColorPickerDialogListener(object: ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, color: Int) {
                    updateAccentColor(requireActivity(), color)
                    setAccentColor(requireActivity())
                }
                override fun onDialogDismissed(dialogId: Int) {

                }
            })
            colorPickerDialog.show(childFragmentManager, "Choose a color")
            true
        }
    }
}