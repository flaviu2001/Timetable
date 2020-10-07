package com.flaviu.timetable.addcard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
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
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {  //TODO Implement with liveData
            val start = binding.startHourEditText.text.toString()
            val finish = binding.endHourEditText.text.toString()
            val weekday = binding.weekdayEditText.text.toString()
            val place = binding.placeEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val info = binding.infoEditText.text.toString()
            val label = binding.labelEditText.text.toString()
            try{
                viewModel.addCard(start, finish, weekday, place, name, info, label)
                this.findNavController().navigateUp()
                hideKeyboard(activity as MainActivity)
            } catch (e: Exception) {
                Toast.makeText(context, e.localizedMessage!!.toString(), Toast.LENGTH_SHORT).show()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}