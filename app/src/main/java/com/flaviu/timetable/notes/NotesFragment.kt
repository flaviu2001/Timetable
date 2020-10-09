package com.flaviu.timetable.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.NotesFragmentBinding

class NotesFragment : Fragment() {

    private lateinit var binding: NotesFragmentBinding
    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val factory = NotesViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, factory).get(NotesViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adapter = NotesAdapter(application.resources, NotesCardListener { cardKey: Long ->
            viewModel.onCardClicked(cardKey)
        })
        binding.cardList.adapter = adapter
        viewModel.cards.observe(viewLifecycleOwner, {
            it?.let{
                if (it.isEmpty()) {
                    binding.helperTextView.visibility = TextView.VISIBLE
                    binding.cardList.visibility = RecyclerView.GONE
                }else {
                    binding.helperTextView.visibility = TextView.GONE
                    binding.cardList.visibility = RecyclerView.VISIBLE
                }
                adapter.addHeaderAndSubmitList(it)
            }
        })
        viewModel.navigateToEditCard.observe(viewLifecycleOwner, {cardKey ->
            cardKey?.let{
                this.findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToEditCardFragment(cardKey))
                viewModel.onEditCardNavigated()
            }
        })
        return binding.root
    }

}