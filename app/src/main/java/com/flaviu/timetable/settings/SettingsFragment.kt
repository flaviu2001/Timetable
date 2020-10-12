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
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.SettingsFragmentBinding
import com.google.android.material.snackbar.Snackbar


class SettingsFragment : Fragment() {
    private lateinit var binding: SettingsFragmentBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val factory = SettingsViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
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
        return binding.root
    }
}