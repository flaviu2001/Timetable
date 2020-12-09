package com.flaviu.timetable.ui.subtask

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.SubtaskFragmentBinding
import com.flaviu.timetable.ui.list.CardAdapter
import com.flaviu.timetable.ui.list.CardListener

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
        viewModel.subtasks.observe(viewLifecycleOwner) {
            adapter.data = it
//            Log.i("SubtaskFragment", it.size.toString())
        }
        return binding.root
    }
}