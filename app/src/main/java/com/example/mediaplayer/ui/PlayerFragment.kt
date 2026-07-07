package com.example.mediaplayer.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentPlayerBinding
import com.example.mediaplayer.service.PlaybackService
import com.example.mediaplayer.viewmodel.MusicViewModel




class PlayerFragment : Fragment() {
    private var _biding : FragmentPlayerBinding? = null
    private val biding get() = _biding!!

    private val viewModel : MusicViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _biding = FragmentPlayerBinding.inflate(inflater, container, false)
        return biding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerViewModel()
    }

    private fun observerViewModel(){
        viewModel.currentSong.observe(viewLifecycleOwner){ song ->
            if(song == null) return@observe

            biding.textViewTitle.text = song.title
            biding.textViewArtist.text = song.artist
            biding.imageViewCover.load(song.coverUrl){
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_foreground)
            }
            val intent = Intent(requireContext(), PlaybackService::class.java).apply {
                putExtra(PlaybackService.EXTRA_AUDIO_URL, song.previewUrl)
            }
            ContextCompat.startForegroundService(requireContext(), intent)
        }
    }

    override fun onDestroyView() {
        _biding = null
        super.onDestroyView()
    }
}