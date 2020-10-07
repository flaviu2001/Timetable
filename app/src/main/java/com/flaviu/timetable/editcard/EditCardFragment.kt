package com.flaviu.timetable.editcard

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add -> {
                val start = binding.startHourEditText.text.toString()
                val finish = binding.endHourEditText.text.toString()
                val weekday = binding.weekdayEditText.text.toString()
                val place = binding.placeEditText.text.toString()
                val name = binding.nameEditText.text.toString()
                val info = binding.infoEditText.text.toString()
                val label = binding.labelEditText.text.toString()
                try {
                    viewModel.cloneCard(start, finish, weekday, place, name, info, label)
                    this.findNavController().navigateUp()
                    hideKeyboard(activity as MainActivity)
                } catch (e: Exception) {
                    Toast.makeText(context, e.localizedMessage!!.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                true
            }
            R.id.edit -> {
                val start = binding.startHourEditText.text.toString()
                val finish = binding.endHourEditText.text.toString()
                val weekday = binding.weekdayEditText.text.toString()
                val place = binding.placeEditText.text.toString()
                val name = binding.nameEditText.text.toString()
                val info = binding.infoEditText.text.toString()
                val label = binding.labelEditText.text.toString()
                try{
                    viewModel.editCard(start, finish, weekday, place, name, info, label)
                    this.findNavController().navigateUp()
                    hideKeyboard(activity as MainActivity)
                } catch (e: Exception) {
                    Toast.makeText(context, e.localizedMessage!!.toString(), Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.delete -> {
                viewModel.onDeleteButtonPressed()
                this.findNavController().navigateUp()
                hideKeyboard(activity as MainActivity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}