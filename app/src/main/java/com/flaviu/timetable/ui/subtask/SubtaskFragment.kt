package com.flaviu.timetable.ui.subtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.SubtaskFragmentBinding
import com.flaviu.timetable.hideKeyboard
import com.flaviu.timetable.setButtonColor

class SubtaskFragment : Fragment() {
    private lateinit var binding: SubtaskFragmentBinding
    private lateinit var viewModel: SubtaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SubtaskFragmentBinding.inflate(inflater)
        val database = CardDatabase.getInstance(requireContext()).cardDatabaseDao
        val cardIdArray = SubtaskFragmentArgs.fromBundle(requireArguments()).cardId
        val cardId = if (cardIdArray.isEmpty()) null else cardIdArray[0]
        if (cardId != null) {
            binding.addSubtaskButton.visibility = Button.VISIBLE
            setButtonColor(binding.addSubtaskButton, requireActivity())
            binding.addSubtaskButton.setOnClickListener {
                this.findNavController().navigate(
                    SubtaskFragmentDirections.actionSubtaskFragmentToAddSubtaskFragment(cardId)
                )
                hideKeyboard(requireActivity())
            }
        }
        viewModel = ViewModelProvider(
            this,
            SubtaskViewModelFactory(cardId, database)
        ).get(SubtaskViewModel::class.java)
        val adapter = SubtaskAdapter(viewLifecycleOwner, database, SubtaskListener {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val isLocked = sharedPref.getBoolean(getString(R.string.saved_edit_state), false)
            if (!isLocked) {
                database.getCardOfSubtask(it).observe(viewLifecycleOwner) { card ->
                    this.findNavController().navigate(
                        SubtaskFragmentDirections.actionSubtaskFragmentToEditSubtaskFragment(
                            it,
                            card.cardId
                        )
                    )
                    hideKeyboard(requireActivity())
                }
            }
        })
        binding.subtaskList.adapter = adapter
        binding.subtaskList.layoutManager = GridLayoutManager(context, 2)
        viewModel.subtasks.observe(viewLifecycleOwner) {
            adapter.data = it
        }
        return binding.root
    }
}