package com.flaviu.timetable.home

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.flaviu.timetable.R
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.databinding.HomeFragmentBinding
import com.flaviu.timetable.list.ListFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {

    private lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val homeViewModelFactory = HomeViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.cards.observe(viewLifecycleOwner, Observer{
            if (viewModel.cards.value == null || viewModel.cards.value!!.isEmpty()) {
                binding.helperTextView.visibility = TextView.VISIBLE
                binding.mainTabLayout.visibility = TabLayout.GONE
                binding.pager.visibility = ViewPager2.GONE
            }else{
                binding.helperTextView.visibility = TextView.GONE
                binding.mainTabLayout.visibility = TabLayout.VISIBLE
                binding.pager.visibility = ViewPager2.VISIBLE
            }
        })
        setupTabLayout()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        )
                || super.onOptionsItemSelected(item)
    }

    private fun setupTabLayout() {
        val tabs: LiveData<List<String>> = Transformations.switchMap(viewModel.cards) { cards: List<Card> ->
            val newList = MutableLiveData<List<String>>()
            newList.value = cards.map {
                it.label
            }.toSortedSet().toList()
            newList
        }
        tabs.observe(viewLifecycleOwner, Observer {
            if (viewModel.cards.value == null)
                return@Observer
            binding.pager.adapter = PagerAdapter(
                requireActivity().supportFragmentManager,
                viewLifecycleOwner.lifecycle,
                tabs
            )
            TabLayoutMediator(binding.mainTabLayout, binding.pager) { tab, position ->
                if (viewModel.cards.value == null)
                    return@TabLayoutMediator
                tab.text = tabs.value!![position]
            }.attach()
        })
    }

    class PagerAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
        private val items: LiveData<List<String>>
    ): FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            if (items.value == null)
                return 0
            return items.value!!.size
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = ListFragment()
            fragment.arguments = Bundle().apply {
                putString("label", items.value!![position])
            }
            return fragment
        }
    }
}
