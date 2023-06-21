package id.fannan.themoviedb.ui.listeners

import id.fannan.themoviedb.entity.Movie

interface OnMovieClickListener {
    fun onMovieClick(movies: Movie, genres:String)
}