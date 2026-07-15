package com.example.mediaplayer.ui.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.ItemSongBinding
import com.example.mediaplayer.loadCover
import com.example.mediaplayer.model.Song

/*
*   in sostanza stiamo collegando la classe Song,
*   con la RecyclerView.
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

            if (song.playCount > 0) {
                binding.textViewPlayCount.visibility = android.view.View.VISIBLE
                binding.textViewPlayCount.text = if (song.playCount == 1) "1 ascolto" else "${song.playCount} ascolti"
            } else {
                binding.textViewPlayCount.visibility = android.view.View.GONE
            }

            binding.imageViewCover.loadCover(song.coverUrl)

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