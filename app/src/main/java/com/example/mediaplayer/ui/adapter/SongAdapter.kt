package com.example.mediaplayer.ui.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.ItemSongBinding
import com.example.mediaplayer.model.Song

/*
*   in sostanza stiamo collegando la classe Song,
*   con la RecyclerView. quando deve far apparire le canzoni
*   ne allocherà solo il numero necessario visualizzabile a schermo.
*   quando scorri la lista, se ne occuperà il GC a deallocare tutto quanto
*
*/
class SongAdapter (
    private var songList : List<Song>,
    private val onSongClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>(){

    inner class SongViewHolder(private val binding : ItemSongBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (song : Song){
            binding.textViewArtist.text = song.artist
            binding.textViewTitle.text = song.title

            binding.imageViewCover.load(song.coverUrl){
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_foreground)
            }

            binding.root.setOnClickListener {
                onSongClick(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int = songList.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songList[position])
    }

    fun updateSongs(newList: List<Song>){
        this.songList = newList
        notifyDataSetChanged()
    }
}