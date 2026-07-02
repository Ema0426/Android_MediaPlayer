package com.example.mediaplayer.network
import com.example.mediaplayer.model.ItunesResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/*
*   cosa stiamo facendo in questo pezzo di codice:
*   stiamo dicharando un interfaccia che definisce
*   come interagire con le API.
*
*   sotto creaimo un oggetto singleton [ TAP insegna ;) ]
*
*
*/

interface ItunesApiService{
    @GET("search")
    suspend fun searchSongs(
       @Query("term") term: String,
       @Query("media") media: String = "music"
    ): ItunesResponse
}

object RetrofitClient{
    private const val BASE_URL = "https://itunes.apple.com/"

    val apiService : ItunesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }
}