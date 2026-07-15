package com.example.mediaplayer.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.load
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentPlayerBinding
import com.example.mediaplayer.formatAsTime
import com.example.mediaplayer.loadCover
import com.example.mediaplayer.service.PlaybackService
import com.example.mediaplayer.viewmodel.MusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class PlayerFragment : Fragment() {
    private var _binding : FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MusicViewModel by activityViewModels()

    private var playBackService : PlaybackService? = null
    private var exoPlayer : ExoPlayer? = null
    private var isBound = false

    private val playerListener = object : Player.Listener{
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayPauseButtonIcon(isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if(playbackState == Player.STATE_READY){
                exoPlayer?.let{ player ->
                    binding.seekBar.max = player.duration.toInt()
                    binding.textViewTotalTime.text = player.duration.formatAsTime()
                }
            }
            else if (playbackState == Player.STATE_ENDED) {
                viewModel.nextSong()
            }
        }
    }

    private val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var binder = service as PlaybackService.LocalBinder
            playBackService = binder.getService()
            exoPlayer = playBackService?.getExoPlayer()
            isBound = true

            exoPlayer?.addListener(playerListener)

            // se il player sta già suonando bisogna settare il player correttamente
            exoPlayer?.let { player ->
                updatePlayPauseButtonIcon(player.isPlaying)
                if (player.playbackState == Player.STATE_READY) {
                    binding.seekBar.max = player.duration.toInt()
                    binding.textViewTotalTime.text = player.duration.formatAsTime()
                    binding.seekBar.progress = player.currentPosition.toInt()
                    binding.textViewCurrentTime.text = player.currentPosition.formatAsTime()
                }
            }


            startProgressLoop()
            setupUIControls()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            exoPlayer?.removeListener(playerListener)
            playBackService = null
            exoPlayer = null
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observerViewModel()
    }

    private fun observerViewModel(){
        viewModel.currentSong.observe(viewLifecycleOwner){ song ->
            if(song == null) return@observe
            binding.imageViewCover.loadCover(song.coverUrl, isHighRes = true)
            viewModel.checkIfFavorite(song.id)

            viewModel.incrementPlayCount(song.id)

            val intent = Intent(requireContext(), PlaybackService::class.java).apply {
                putExtra(PlaybackService.EXTRA_AUDIO_URL, song.previewUrl)
            }
            androidx.core.content.ContextCompat.startForegroundService(requireContext(), intent)
            if(!isBound) {
                requireContext().bindService(intent, serviceConnection, android.content.Context.BIND_AUTO_CREATE)
            }
        }

    }

    private fun setupUIControls(){
        binding.fabPlayPause.setOnClickListener {
            exoPlayer?.let{ player ->
                if(player.isPlaying){
                    player.pause()
                }else{
                    player.play()
                }
            }
        }


        binding.btnNext.setOnClickListener {
            viewModel.nextSong()
        }

        binding.btnPrevious.setOnClickListener {
            viewModel.previousSong()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.textViewCurrentTime.text = progress.toLong().formatAsTime()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let{exoPlayer?.seekTo(it.progress.toLong())}
            }
        })

        binding.imageViewFavorite.setOnClickListener {
            val song = viewModel.currentSong.value ?: return@setOnClickListener
            val isCurrentlyFavorite = viewModel.isFavorite.value ?: false

            if(isCurrentlyFavorite){
                viewModel.removeToFavorites(song)
            }else{
                viewModel.addToFavorites(song)
            }
        }
    }

    private fun startProgressLoop(){
        viewLifecycleOwner.lifecycleScope.launch {
            while(isActive){
                exoPlayer?.let{ player ->
                    if(player.isPlaying){
                        binding.seekBar.progress = player.currentPosition.toInt()
                        binding.textViewCurrentTime.text = player.currentPosition.formatAsTime()
                    }
                }
                delay(250)
            }
        }
    }

    private fun updatePlayPauseButtonIcon(isPLaying: Boolean){
        val iconRes = if(isPLaying){
            android.R.drawable.ic_media_pause
        }else{
            android.R.drawable.ic_media_play
        }
        binding.fabPlayPause.setImageResource(iconRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(isBound){
            exoPlayer?.removeListener(playerListener)
            requireContext().unbindService(serviceConnection)
            isBound = false
        }
        _binding = null
    }
}