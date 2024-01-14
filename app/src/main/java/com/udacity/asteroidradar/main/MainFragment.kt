package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import timber.log.Timber

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val asteroidAdapter =
        AsteroidListAdapter(AsteroidListAdapter.OnClickListener { viewModel.onAsteroidClicked(it) })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.navigateDetail.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.onNavigateDetailComplete()
                Timber.d("Navigate to Details")
            }
        })

        binding.asteroidRecycler.adapter = asteroidAdapter

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.listAsteroid.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroid ->
            asteroid.apply {
                asteroidAdapter.submitList(this)
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_saved_menu -> viewModel.updateFilter(AsteroidFilter.ALL)
            R.id.show_week_menu -> viewModel.updateFilter(AsteroidFilter.WEEK)
            R.id.show_today_menu -> viewModel.updateFilter(AsteroidFilter.TODAY)
        }
        return true
    }
}
