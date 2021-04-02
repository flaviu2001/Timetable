package com.flaviu.timetable.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.ListFragmentBinding
import com.flaviu.timetable.ui.home.HomeFragmentDirections

class ListFragment : Fragment() {
    private lateinit var binding: ListFragmentBinding
    private lateinit var viewModel: ListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.list_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val arguments = ListFragmentArgs.fromBundle(requireArguments())
        val factory = ListViewModelFactory(arguments.label, dataSource)
        viewModel = ViewModelProvider(this, factory).get(ListViewModel::class.java)
        val adapter = CardAdapter(resources, CardListener { cardKey: Long ->
            viewModel.onCardClicked(cardKey)
        })
        binding.cardList.adapter = adapter
        viewModel.cards.observe(viewLifecycleOwner, {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        viewModel.navigateToEditCard.observe(viewLifecycleOwner, { cardKey ->
            cardKey?.let {
                val isLocked = sharedPref.getBoolean(getString(R.string.saved_edit_state), false)
                if (!isLocked) {
                    this.findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToEditCardFragment(cardKey)
                    )
                    viewModel.onEditCardNavigated()
                }
            }
        })
        return binding.root
    }
}