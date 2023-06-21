package id.fannan.themoviedb.repository

import id.fannan.themoviedb.BuildConfig
import id.fannan.themoviedb.network.ApiConfig

class MovieIRepository {

    private val client = ApiConfig.getApiServices()
    suspend fun getPopularMovie(page: Int) = client.getPopularMovie(BuildConfig.API_KEY, page)
    suspend fun searchMovie(query:String, page:Int) = client.sarchMovie(BuildConfig.API_KEY,query,page)
    suspend fun getGenres() = client.getGenres(BuildConfig.API_KEY)

}