package com.example.mediaplayer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.model.Song
import com.example.mediaplayer.network.RetrofitClient
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
}