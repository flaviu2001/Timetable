package com.flaviu.timetable.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    private lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val homeViewModelFactory = HomeViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        val adapter = CardAdapter(CardListener {cardKey: Long ->
            viewModel.onCardClicked(cardKey)
        })
        binding.cardList.adapter = adapter
        viewModel.cards.observe(viewLifecycleOwner, {
            it?.let{
                adapter.addHeaderAndSubmitList(it)
            }
        })
        binding.addCardButton.setOnClickListener{view: View ->
            view.findNavController().navigate(R.id.action_homeFragment_to_addCardFragment)
        }
        viewModel.navigateToEditCard.observe(viewLifecycleOwner, {cardKey ->
            cardKey?.let{
                this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditCardFragment(cardKey))
                viewModel.onEditCardNavigated()
            }
        })
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}