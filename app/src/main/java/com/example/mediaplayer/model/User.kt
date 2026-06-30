package com.example.mediaplayer.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val uid: String = "",
    val displayName: String = "",
    val email: String = ""
)