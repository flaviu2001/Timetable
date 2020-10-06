package com.flaviu.timetable.editcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.flaviu.timetable.MainActivity
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.EditCardFragmentBinding
import com.flaviu.timetable.hideKeyboard
import java.lang.Exception

class EditCardFragment : Fragment() {

    private lateinit var viewModel: EditCardViewModel
    private lateinit var binding: EditCardFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_card_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val arguments = EditCardFragmentArgs.fromBundle(requireArguments())
        val editCardViewModelFactory = EditCardViewModelFactory(arguments.cardKey, dataSource)
        viewModel = ViewModelProvider(this, editCardViewModelFactory).get(EditCardViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.cancelButton.setOnClickListener{view: View ->
            view.findNavController().navigateUp()
            hideKeyboard(activity as MainActivity)
        }
        binding.deleteButton.setOnClickListener{view: View ->
            viewModel.onDeleteButtonPressed()
            view.findNavController().navigateUp()
            hideKeyboard(activity as MainActivity)
        }
        binding.editCardBButton.setOnClickListener{view: View ->
            val start = binding.startHourEditText.text.toString()
            val finish = binding.endHourEditText.text.toString()
            val weekday = binding.weekdayEditText.text.toString()
            val place = binding.placeEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val info = binding.infoEditText.text.toString()
            try{
                viewModel.editCard(start, finish, weekday, place, name, info)
                view.findNavController().navigateUp()
                hideKeyboard(activity as MainActivity)
            } catch (e: Exception) {
                Toast.makeText(context, e.localizedMessage!!.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

}