package com.example.mediaplayer.model
import com.google.gson.annotations.SerializedName

data class ItunesResponse (
    @SerializedName("resultCount")
    val resultCount : Int,

    @SerializedName("results")
    val results : List<Song>
)