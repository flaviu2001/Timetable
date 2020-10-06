package com.flaviu.timetable.addcard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.flaviu.timetable.MainActivity
import com.flaviu.timetable.R
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.AddCardFragmentBinding
import com.flaviu.timetable.hideKeyboard
import java.lang.Exception

class AddCardFragment : Fragment() {
    private lateinit var binding: AddCardFragmentBinding
    private lateinit var viewModel: AddCardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_card_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val factory = AddCardViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, factory).get(AddCardViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.cancelButton.setOnClickListener{view: View ->
            view.findNavController().navigateUp()
            hideKeyboard(activity as MainActivity)
        }
        binding.addCardBButton.setOnClickListener{view: View -> //TODO: Implement using LiveData
            val start = binding.startHourEditText.text.toString()
            val finish = binding.endHourEditText.text.toString()
            val weekday = binding.weekdayEditText.text.toString()
            val place = binding.placeEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val info = binding.placeEditText.text.toString()
            try{
                viewModel.addCard(start, finish, weekday, place, name, info)
                view.findNavController().navigateUp()
                hideKeyboard(activity as MainActivity)
            } catch (e: Exception) {
                Toast.makeText(context, e.localizedMessage!!.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
}