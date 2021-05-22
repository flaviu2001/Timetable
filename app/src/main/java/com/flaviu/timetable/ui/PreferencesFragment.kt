package com.flaviu.timetable.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.flaviu.timetable.*
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.database.CardDatabaseDao
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class PreferencesFragment : PreferenceFragmentCompat() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    lateinit var database: CardDatabaseDao

    private fun clearData() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                clearDataFromDatabase(requireContext(), database)
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        database =
            CardDatabase.getInstance(requireNotNull(this.activity).application).cardDatabaseDao
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>("clear_data")!!.setOnPreferenceClickListener {
            AlertDialog.Builder(this.context)
                .setTitle("Are you sure you want to clear all data?")
                .setPositiveButton(
                    R.string.yes
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
            colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
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
            colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
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
        findPreference<Preference>("save_data")!!.setOnPreferenceClickListener {
            saveFile()
            true
        }
        findPreference<Preference>("load_data")!!.setOnPreferenceClickListener {
            loadFile()
            true
        }
    }

    private fun saveFile() {
        val now = Calendar.getInstance()
        val fileName = "Timetable-${
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ROOT).format(
                Date(now.timeInMillis)
            )
        }.txt"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        startActivityForResult(intent, 2)
    }

    private fun loadFile() {
        val path = File("/storage/emulated/0/Documents/Timetable Backups/")
        path.mkdirs()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, path.toURI())
        }
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (resultData?.data != null) {
                    AlertDialog.Builder(this.context)
                        .setTitle("Are you sure you want to load all the data?")
                        .setPositiveButton(
                            R.string.yes
                        ) { _, _ ->
                            uiScope.launch {
                                withContext(Dispatchers.IO) {
                                    if (loadDataFromFile(resultData.data!!, requireContext()))
                                        Snackbar.make(
                                            requireView(),
                                            "Data successfully loaded",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    else Snackbar.make(
                                        requireView(),
                                        "Error processing the file",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        .setNegativeButton(R.string.no, null).show()
                } else {
                    Snackbar.make(requireView(), "No file found", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(requireView(), "No file found", Snackbar.LENGTH_SHORT).show()
            }
        } else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                if (resultData?.data != null) {
                    uiScope.launch {
                        withContext(Dispatchers.IO) {
                            val contentResolver = requireContext().contentResolver
                            @Suppress("BlockingMethodInNonBlockingContext")
                            contentResolver.openOutputStream(resultData.data!!).use { stream ->
                                stream!!.write(getTextFromDB(CardDatabase.getInstance(requireContext()).cardDatabaseDao).toByteArray())
                            }
                            Snackbar.make(
                                requireView(),
                                "Successfully saved the data",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else Snackbar.make(
                    requireView(),
                    "Could not choose folder (ERROR: 2)",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else Snackbar.make(
                requireView(),
                "Could not choose folder (ERROR: 1)",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}
