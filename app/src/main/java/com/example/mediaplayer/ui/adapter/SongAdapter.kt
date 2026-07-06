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

    inner class SongViewHolder(private val biding : ItemSongBinding) : RecyclerView.ViewHolder(biding.root){
        fun bind (song : Song){
            biding.textViewArtist.text = song.artist
            biding.textViewTitle.text = song.title

            biding.imageViewCover.load(song.coverUrl){
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_foreground)
            }

            biding.root.setOnClickListener {
                onSongClick(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val biding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(biding)
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