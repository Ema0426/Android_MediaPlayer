package com.example.mediaplayer.model

import com.google.firebase.firestore.DocumentId

data class Playlist(
    @DocumentId val id: String = "",
    val name: String = "",
    val ownerUid: String = "",
    val songIds: List<String> = emptyList()
)