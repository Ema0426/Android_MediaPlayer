package com.example.mediaplayer.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.databinding.FragmentLibraryBinding
import com.example.mediaplayer.ui.adapter.SongAdapter
import com.example.mediaplayer.viewmodel.MusicViewModel
import androidx.appcompat.widget.SearchView
import com.example.mediaplayer.R
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class LibraryFragment : Fragment() {
    private var _binding : FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MusicViewModel by activityViewModels()
    private lateinit var adapter : SongAdapter

    private var searchJob : Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observerViewModel()
        setupSearchInput()
    }

    private fun setupSearchInput(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val finalQuery = query?.trim() ?: ""
                searchJob?.cancel()

                if(finalQuery.isNotBlank()){
                    viewModel.searchMusic(finalQuery)
                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.trim() ?: ""
                searchJob?.cancel()

                if (query.length > 2) {
                    searchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(500)
                        viewModel.searchMusic(query)
                    }
                }
                return true
            }
        })
    }
    private fun setupRecyclerView(){
        adapter = SongAdapter(emptyList()){ selectedSong ->
            val currentList = viewModel.songs.value ?: emptyList()
            val index = currentList.indexOf(selectedSong)
            viewModel.playQueue(currentList, if(index != -1) index else 0)
            findNavController().navigate(R.id.libraryFragment_to_playerFragment)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }
    private fun observerViewModel(){
        viewModel.songs.observe(viewLifecycleOwner){ songsList ->
            adapter.updateSongs(songsList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner){ isLoading ->
            if(isLoading){
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}