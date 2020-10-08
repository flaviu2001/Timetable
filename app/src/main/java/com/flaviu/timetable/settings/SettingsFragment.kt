package com.flaviu.timetable.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.flaviu.timetable.MainActivity
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.SettingsFragmentBinding
import com.flaviu.timetable.hideKeyboard
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
        binding.clearButton.setOnClickListener{view: View ->
            viewModel.clearData()
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                "Cleared all entries",
                Snackbar.LENGTH_SHORT // How long to display the message.
            ).show()
            view.findNavController().navigateUp()
        }
        return binding.root
    }
}