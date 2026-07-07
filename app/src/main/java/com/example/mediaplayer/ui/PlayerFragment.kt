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
import com.example.mediaplayer.service.PlaybackService
import com.example.mediaplayer.viewmodel.MusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class PlayerFragment : Fragment() {
    private var _biding : FragmentPlayerBinding? = null
    private val biding get() = _biding!!

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
                    biding.seekBar.max = player.duration.toInt()
                }
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
            exoPlayer?.let { updatePlayPauseButtonIcon(it.isPlaying) }
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
            requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        }
    }

    private fun setupUIControls(){
        biding.fabPlayPause.setOnClickListener {
            exoPlayer?.let{ player ->
                if(player.isPlaying){
                    player.pause()
                }else{
                    player.play()
                }
            }
        }

        biding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let{exoPlayer?.seekTo(it.progress.toLong())}
            }
        })
    }

    private fun startProgressLoop(){
        viewLifecycleOwner.lifecycleScope.launch {
            while(isActive){
                exoPlayer?.let{ player ->
                    if(player.isPlaying){
                        biding.seekBar.progress = player.currentPosition.toInt()
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
        biding.fabPlayPause.setImageResource(iconRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(isBound){
            exoPlayer?.removeListener(playerListener)
            requireContext().unbindService(serviceConnection)
            isBound = false
        }
        _biding = null
    }
}