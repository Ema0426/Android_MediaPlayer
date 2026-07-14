package com.example.mediaplayer.model

import com.google.gson.annotations.SerializedName
data class Song (
    @SerializedName("trackId")
    val id : Long = 0L,

    @SerializedName("trackName")
    val title : String = "",

    @SerializedName("artistName")
    val artist : String = "",


    @SerializedName("previewUrl")
    val previewUrl : String = "",

    @SerializedName("artworkUrl100")
    val coverUrl : String? = null
    )