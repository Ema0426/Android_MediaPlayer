package com.example.mediaplayer.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.databinding.FragmentLibraryBinding
import com.example.mediaplayer.ui.adapter.SongAdapter
import com.example.mediaplayer.viewmodel.MusicViewModel

class LibraryFragment : Fragment() {
    private var _biding : FragmentLibraryBinding? = null
    private val biding get() = _biding!!

    private val viewModel : MusicViewModel by viewModels()
    private lateinit var adapter : SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _biding = FragmentLibraryBinding.inflate(inflater, container, false)
        return biding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observerViewModel()
        viewModel.searchMusic("Queen")

    }

    private fun setupRecyclerView(){
        adapter = SongAdapter(emptyList()){ selectedSong ->
            Toast.makeText(requireContext(), "In riproduzione : ${selectedSong.title}", Toast.LENGTH_SHORT).show()
        }

        biding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        biding.recyclerView.adapter = adapter
    }

    private fun observerViewModel(){
        viewModel.songs.observe(viewLifecycleOwner){ songsList ->
            adapter.updateSongs(songsList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner){ isLoading ->
            if(isLoading){
                biding.progressBar.visibility = View.VISIBLE
                biding.recyclerView.visibility = View.GONE
            }else{
                biding.progressBar.visibility = View.GONE
                biding.recyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        _biding = null
        super.onDestroyView()
    }
}