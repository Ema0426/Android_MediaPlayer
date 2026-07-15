package com.example.mediaplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer


/*
* creiamo l'istanza di exoplayer, che sarebbe il nostro oggetto per i flussi audio
* https://dev.to/theplebdev/lets-talk-about-services-in-android-with-kotlin-1fij
* https://developer.android.com/media/media3/exoplayer
* un po di documentazione utile
*
* per le notifiche guardare l'esercitazione
*
* dopo c'è stato bisogno di fare una bound service,
* https://developer.android.com/develop/background-work/services/bound-services
* qui c'è un po di documentazione, c'è anche il laboratorio da visionare per capire il funzionamento
*
*/

class PlaybackService : Service() {
    private var player: ExoPlayer? = null

    private val binder = LocalBinder()
    companion object {
        const val EXTRA_AUDIO_URL = "extra_audio_url"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "media_playback_channel"
    }

    inner class LocalBinder : Binder(){
        fun getService() : PlaybackService = this@PlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audioUrl = intent?.getStringExtra(EXTRA_AUDIO_URL)

        startForegroundService()

        if(audioUrl != null){
            val currentPlayingUrl = player?.currentMediaItem?.localConfiguration?.uri?.toString()

            if (audioUrl != currentPlayingUrl) {
                val mediaItem = MediaItem.fromUri(audioUrl)
                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.play()
            }
        }
        return START_NOT_STICKY
    }

    fun getExoPlayer() : ExoPlayer? = player

    private fun startForegroundService(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Canale dedicato ai controlli di riproduzione audio"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notifcation = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Riproduzione in corso")
            .setContentText("Il tuo brano è in esecuzione")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notifcation,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notifcation)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        player?.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}