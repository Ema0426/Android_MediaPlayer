package com.example.mediaplayer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

/*
* creiamo l'istanza di exoplayer, che sarebbe il nostro oggetto per i flussi audio
* https://dev.to/theplebdev/lets-talk-about-services-in-android-with-kotlin-1fij
* https://developer.android.com/media/media3/exoplayer
* un po di documentazione utile
*
*/

class PlaybackService : Service() {
    private var player: ExoPlayer? = null

    companion object {
        const val EXTRA_AUDIO_URL = "extra_audio_url"
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audioUrl = intent?.getStringExtra(EXTRA_AUDIO_URL)

        if(audioUrl != null){
            val mediaItem = MediaItem.fromUri(audioUrl)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}