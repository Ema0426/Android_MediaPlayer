package com.example.mediaplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentFavoritesBinding
import com.example.mediaplayer.ui.adapter.SongAdapter
import com.example.mediaplayer.viewmodel.MusicViewModel
import kotlin.getValue
import com.example.mediaplayer.service.PlaybackService
import android.content.Intent
import com.example.mediaplayer.setupLogoutMenu
import com.example.mediaplayer.showLogoutDialog
import kotlin.jvm.java


class FavoritesFragment : Fragment() {

    private var _binding : FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MusicViewModel by activityViewModels()
    private lateinit var adapter : SongAdapter
    private var currentQuery: String = ""




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observerViewModel()
        setupSearchView()
        setupLogoutMenu(viewModel)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(/* listener = */ object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                filterFavorites()
                return true
            }
        })
    }

    private fun filterFavorites() {
        val allFavorites = viewModel.favoriteSongs.value ?: emptyList()

        if (currentQuery.isEmpty()) {
            adapter.updateSongs(allFavorites)
        } else {
            val filteredList = allFavorites.filter { song ->
                song.title.contains(currentQuery, ignoreCase = true) ||
                        song.artist.contains(currentQuery, ignoreCase = true)
            }
            adapter.updateSongs(filteredList)
        }
    }

    private fun setupRecyclerView(){
        adapter = SongAdapter(emptyList()) { selectedSong ->
            val allFavorites = viewModel.favoriteSongs.value ?: emptyList()
            val listToPlay = if (currentQuery.isEmpty()) {
                allFavorites
            } else {
                allFavorites.filter {
                    it.title.contains(currentQuery, ignoreCase = true) ||
                            it.artist.contains(currentQuery, ignoreCase = true)
                }
            }

            val index = listToPlay.indexOf(selectedSong)
            viewModel.playQueue(listToPlay, if(index != -1) index else 0)

            findNavController().navigate(R.id.action_favoritesFragment_to_playerFragment)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }


    private fun observerViewModel(){
        viewModel.favoriteSongs.observe(viewLifecycleOwner) {
            filterFavorites()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }



}