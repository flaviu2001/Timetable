package com.flaviu.timetable.ui.home

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.flaviu.timetable.R
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.database.Label
import com.flaviu.timetable.databinding.HomeFragmentBinding
import com.flaviu.timetable.getAccentColor
import com.flaviu.timetable.ui.list.CardAdapter
import com.flaviu.timetable.ui.list.CardListener
import com.flaviu.timetable.ui.list.ListFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {

    private lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        val dataSource = CardDatabase.getInstance(application).cardDatabaseDao
        val homeViewModelFactory = HomeViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val singleLabel = sharedPref.getBoolean("singleLabelMode", false)
        if (singleLabel)
            setupSingleLabelView()
        else
            setupTabLayout()

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val isLocked = sharedPref.getBoolean(getString(R.string.saved_edit_state), false)
        menu[0].isVisible = !isLocked
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.subtaskFragment) {
            this.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToSubtaskFragment(LongArray(0)))
            return true
        }
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        )
                || super.onOptionsItemSelected(item)
    }

    private fun setupTabLayout() {
        binding.mainTabLayout.visibility = TabLayout.VISIBLE
        binding.pager.visibility = ViewPager2.VISIBLE
        binding.singleLabelView.visibility = RecyclerView.GONE
        viewModel.labels.observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                binding.helperTextView.visibility = TextView.VISIBLE
                binding.mainTabLayout.visibility = TabLayout.GONE
                binding.pager.visibility = ViewPager2.GONE
                return@observe
            } else {
                binding.helperTextView.visibility = TextView.GONE
                binding.mainTabLayout.visibility = TabLayout.VISIBLE
                binding.pager.visibility = ViewPager2.VISIBLE
            }
            binding.pager.adapter = PagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                it
            )
            TabLayoutMediator(binding.mainTabLayout, binding.pager) { tab, position ->
                if (viewModel.labels.value == null)
                    return@TabLayoutMediator
                tab.text = viewModel.labels.value!![position].name
            }.attach()
            binding.mainTabLayout.setSelectedTabIndicatorColor(getAccentColor(requireActivity()))
        }
    }

    private fun setupSingleLabelView() {
        binding.mainTabLayout.visibility = TabLayout.GONE
        binding.pager.visibility = ViewPager2.GONE
        binding.singleLabelView.visibility = RecyclerView.VISIBLE
        val adapter = CardAdapter(resources, CardListener { cardKey: Long ->
            viewModel.onCardClicked(cardKey)
        })
        binding.singleLabelView.adapter = adapter
        viewModel.cards.observe(viewLifecycleOwner, {
            if (it == null || it.isEmpty()) {
                binding.helperTextView.visibility = TextView.VISIBLE
                binding.singleLabelView.visibility = RecyclerView.GONE
                return@observe
            } else {
                binding.helperTextView.visibility = TextView.GONE
                binding.singleLabelView.visibility = RecyclerView.VISIBLE
            }
            adapter.addHeaderAndSubmitList(it)
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
    }

    class PagerAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
        private val items: List<Label>?
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            if (items == null)
                return 0
            return items.size
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = ListFragment()
            fragment.arguments = Bundle().apply {
                putString("label", items!![position].name)
            }
            return fragment
        }
    }
}
