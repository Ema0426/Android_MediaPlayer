package com.example.mediaplayer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.model.Song
import com.example.mediaplayer.network.RetrofitClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

/*
* ci consente di mantenere i nostri dati anche se il fragment o activity vengono distrutti.
* all'interno dello scope "viewModelScope"
*
*
*/
class MusicViewModel : ViewModel() {
    private val _songs = MutableLiveData<List<Song>>()
    val songs : LiveData<List<Song>> get() =  _songs
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading
    private val _currentSong = MutableLiveData<Song>()

    val currentSong : LiveData<Song> get() = _currentSong

    private val _isFavorite = MutableLiveData<Boolean>()

    val isFavorite : LiveData<Boolean> get() = _isFavorite

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _favoriteSongs = MutableLiveData<List<Song>>()
    val favoriteSongs: LiveData<List<Song>> get() = _favoriteSongs


    private var currentQueue: List<Song> = emptyList()
    private var currentIndex: Int = 0


    init {
        listenToFavorites()
    }


    fun playQueue(queue: List<Song>, startIndex: Int) {
        if (queue.isEmpty() || startIndex < 0 || startIndex >= queue.size) return
        currentQueue = queue
        currentIndex = startIndex
        _currentSong.value = currentQueue[currentIndex]
    }

    fun nextSong() {
        if (currentQueue.isEmpty()) return

        currentIndex = if (currentIndex < currentQueue.size - 1) {
            currentIndex + 1
        } else {
            0
        }
        _currentSong.value = currentQueue[currentIndex]
    }

    fun previousSong() {
        if (currentQueue.isEmpty()) return

        currentIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            currentQueue.size - 1
        }
        _currentSong.value = currentQueue[currentIndex]
    }

    fun searchMusic(query : String){
        if(query.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = RetrofitClient.apiService.searchSongs(term = query)
                _songs.value = response.results
            }catch (e : Exception){
                Log.e("MusicViewModel" , "Errore durante il caricamento da iTunes", e)
                _songs.value = emptyList()
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun checkIfFavorite(songId : Long){
        val uid = auth.currentUser?.uid ?: return

        db.collection("Users")
            .document(uid)
            .collection("Favorites")
            .document(songId.toString())
            .get()
            .addOnSuccessListener { document ->
                _isFavorite.value = document.exists()
            }
            .addOnFailureListener { e ->
                Log.e("MusicViewModel", "Errore durante la verifica dei preferiti", e)
                _isFavorite.value = false
            }
    }

    fun addToFavorites(song : Song){
        val uid = auth.currentUser?.uid ?: return

        db.collection("Users")
            .document(uid)
            .collection("Favorites")
            .document(song.id.toString())
            .set(song)
            .addOnSuccessListener {
                Log.d("MusicViewModel", "Traccia ${song.title} aggiunta hai preferiti")
            }
            .addOnFailureListener {
                Log.d("MusicViewModel", "Errore durante il salvataggio nei preferiti")
            }
    }

    fun removeToFavorites(song : Song){
        val uid = auth.currentUser?.uid ?: return

        db.collection("Users")
            .document(uid)
            .collection("Favorites")
            .document(song.id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("MusicViewModel", "Traccia ${song.title} rimossa dai preferiti")
            }
            .addOnFailureListener {
                Log.d("MusicViewModel", "Errore durante la rimozione dai preferiti")
            }
    }

    private fun listenToFavorites() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("Users")
            .document(uid)
            .collection("Favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d("MusicViewModel", "Errore ascolto preferiti", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val songs = snapshot.documents.mapNotNull { it.toObject(Song::class.java) }
                    _favoriteSongs.value = songs
                }
            }
    }

}