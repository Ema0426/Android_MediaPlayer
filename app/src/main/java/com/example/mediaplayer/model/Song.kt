package com.example.mediaplayer.model

import com.google.firebase.firestore.DocumentId
data class Song (
    @DocumentId val id: String = "",
    val title: String = "",
    val artist: String = ""
)