package id.fannan.themoviedb.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import id.fannan.themoviedb.entity.GenreResponse
import id.fannan.themoviedb.entity.MovieResponse
import id.fannan.themoviedb.network.RequestState
import id.fannan.themoviedb.repository.MovieIRepository
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response

class MovieViewModel : ViewModel() {
    private val repository: MovieIRepository = MovieIRepository()
    private var popularPage = 1
    private var searchPage = 1
    private var popularMovieResponse: MovieResponse? = null
    private var _popularResponse = MutableLiveData<RequestState<MovieResponse?>>()

    private var searchMovieResponse: MovieResponse? = null
    private var _searchResponse = MutableLiveData<RequestState<MovieResponse?>>()

    var popularResponse: LiveData<RequestState<MovieResponse?>> = _popularResponse
    var searchResponse: LiveData<RequestState<MovieResponse?>> = _searchResponse
    fun getPopularMovie() {
        viewModelScope.launch {
            _popularResponse.postValue(RequestState.Loading)
            val response = repository.getPopularMovie(popularPage)
            _popularResponse.postValue(HandlePopularMovieResponse(response))
        }
    }

    fun searchMovie(query: String) {
        viewModelScope.launch {
            _searchResponse.postValue(RequestState.Loading)
            val response = repository.searchMovie(query, searchPage)
            _searchResponse.postValue(HandleSearchMovieResponse(response))
        }
    }

    private fun HandleSearchMovieResponse(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                searchPage++
                if (searchMovieResponse == null) searchMovieResponse = it else {
                    val oldMovies = searchMovieResponse?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }
            RequestState.Success(searchMovieResponse ?: response.body())
        } else RequestState.Error(
            try {
                response.errorBody()?.string().let {
                    JSONObject(it).get("status_message")
                }
            } catch (e: JSONException) {
                e.localizedMessage
            } as String
        )
    }


    private fun HandlePopularMovieResponse(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                popularPage++
                if (popularMovieResponse == null) popularMovieResponse = it else {
                    val oldMovies = popularMovieResponse?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }
            RequestState.Success(popularMovieResponse ?: response.body())
        } else RequestState.Error(
            try {
                response.errorBody()?.string().let {
                    JSONObject(it).get("status_message")
                }
            } catch (e: JSONException) {
                e.localizedMessage
            } as String
        )
    }

    fun getGenres(): LiveData<RequestState<GenreResponse>> = liveData {
        try {
            val response = repository.getGenres()
            emit(RequestState.Success(response))
        } catch (e: HttpException) {
            emit(RequestState.Error(e.response()?.errorBody().toString()))
        }
    }
}