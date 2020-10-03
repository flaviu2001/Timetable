package com.flaviu.timetable.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val homeViewModelFactory = HomeViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        val adapter = CardAdapter(CardListener {
            Log.i("pula", "O bagat baiatu meu click listener sa moara titanicul")
        })
        binding.cardList.adapter = adapter
        viewModel.cards.observe(viewLifecycleOwner, {
            it?.let{
                adapter.addHeaderAndSubmitList(it)
            }
        })
        return binding.root
    }
}