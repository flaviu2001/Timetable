package com.flaviu.timetable.ui.subtask

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.SubtaskFragmentBinding

class SubtaskFragment : Fragment() {
    private lateinit var binding: SubtaskFragmentBinding
    private lateinit var viewModel: SubtaskViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = SubtaskFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val database = CardDatabase.getInstance(requireContext()).cardDatabaseDao
        val cardIdArray = SubtaskFragmentArgs.fromBundle(requireArguments()).cardId
        val cardId = if (cardIdArray.isEmpty()) null else cardIdArray[0]
        viewModel = ViewModelProvider(this, SubtaskViewModelFactory(cardId, database)).get(SubtaskViewModel::class.java)
        binding.viewModel = viewModel
        val adapter = SubtaskAdapter(viewLifecycleOwner, database)
        binding.subtaskList.adapter = adapter
        binding.subtaskList.layoutManager = GridLayoutManager(context, 2)
        viewModel.subtasks.observe(viewLifecycleOwner) {
            adapter.data = it
        }
        return binding.root
    }
}