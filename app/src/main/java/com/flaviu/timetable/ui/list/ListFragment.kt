package com.flaviu.timetable.ui.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
        val factory = ListViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, factory).get(ListViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adapter = CardAdapter(arguments.label, resources, CardListener {cardKey: Long ->
            viewModel.onCardClicked(cardKey)
        })
        binding.cardList.adapter = adapter
        viewModel.cards.observe(viewLifecycleOwner, {
            it?.let{
                adapter.addHeaderAndSubmitList(it)
            }
        })
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        viewModel.navigateToEditCard.observe(viewLifecycleOwner, {cardKey ->
            cardKey?.let{
                val canEdit = sharedPref.getBoolean(getString(R.string.saved_edit_state), true)
                if (canEdit) {
                    this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditCardFragment(cardKey))
                    viewModel.onEditCardNavigated()
                }
            }
        })
        return binding.root
    }
}