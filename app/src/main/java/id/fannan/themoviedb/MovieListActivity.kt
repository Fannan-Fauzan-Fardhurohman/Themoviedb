package id.fannan.themoviedb

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import id.fannan.themoviedb.databinding.ActivityMovieListBinding
import id.fannan.themoviedb.entity.Movie
import id.fannan.themoviedb.network.RequestState
import id.fannan.themoviedb.ui.adapter.MovieListAdapter
import id.fannan.themoviedb.ui.listeners.OnMovieClickListener
import id.fannan.themoviedb.ui.vm.MovieViewModel

class MovieListActivity : AppCompatActivity() {
    private var _binding: ActivityMovieListBinding? = null
    private val binding get() = _binding!!
    private var adapter: MovieListAdapter? = null
    private var layoutManager: LayoutManager? = null
    private val viewModel: MovieViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestThanObserveAnyChangeGenres()
        viewModel.getPopularMovie()
        ObserveAnyChangePopularMovie()
        setupRecyclerView()
        adapter?.onMovieClickListener(object : OnMovieClickListener {
            override fun onMovieClick(movies: Movie, genres: String) {
                val intent = Intent(this@MovieListActivity, MovieDetailActivity::class.java)
                intent.putExtra(MovieDetailActivity.movie, movies)
                intent.putExtra(MovieDetailActivity.genres, genres)
                startActivity(intent)
            }
        })
        binding.searchButton.setOnClickListener {
            val query = binding.search.text.toString()
            when {
                query.isEmpty() -> binding.search.error = "Please insert a keyword!"
                else -> {
                    val intent = Intent(this, SearchMovieActivity::class.java)
                    intent.putExtra(SearchMovieActivity.query, query)
                    startActivity(intent)
                }
            }
        }
    }

    fun ObserveAnyChangePopularMovie() {
        viewModel.popularResponse.observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> showLoading()
                    is RequestState.Success -> {
                        hideLoading()
                        it.data?.results?.let { data -> adapter?.differ?.submitList(data.toList()) }
                    }

                    is RequestState.Error -> {
                        hideLoading()
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun requestThanObserveAnyChangeGenres() {
        viewModel.getGenres().observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> {}
                    is RequestState.Success -> it.data.genres?.let { data -> adapter?.setGenres(data) }
                    is RequestState.Error -> Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }
    }

    private val scrolListener = object : OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                viewModel.getPopularMovie()
            }
        }
    }

    private fun showLoading() {
        binding.loading.show()
    }

    private fun hideLoading() {
        binding.loading.hide()
    }

    private fun setupRecyclerView() {
        adapter = MovieListAdapter()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.apply {
            movieList.adapter = adapter
            movieList.layoutManager = layoutManager
            movieList.addOnScrollListener(scrolListener)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}