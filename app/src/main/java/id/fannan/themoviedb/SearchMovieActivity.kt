package id.fannan.themoviedb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.fannan.themoviedb.databinding.ActivityMovieListBinding
import id.fannan.themoviedb.databinding.ActivitySearchMovieBinding
import id.fannan.themoviedb.entity.Movie
import id.fannan.themoviedb.network.RequestState
import id.fannan.themoviedb.ui.adapter.MovieListAdapter
import id.fannan.themoviedb.ui.listeners.OnMovieClickListener
import id.fannan.themoviedb.ui.vm.MovieViewModel

class SearchMovieActivity : AppCompatActivity() {
    private var _binding: ActivitySearchMovieBinding? = null
    private val binding get() = _binding!!
    private var adapter: MovieListAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private val viewModel: MovieViewModel by viewModels()
    private var isSearchAgain = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestThanObserveAnyChangeGenres()

        binding.search.setText(intent.getStringExtra(query))
        if (!isSearchAgain) viewModel.searchMovie(binding.search.text.toString())

        binding.searchButton.setOnClickListener {
            val query = binding.search.text.toString()
            when {
                query.isEmpty() -> binding.search.error = "Please insert a keyword!"
                else -> {
                    isSearchAgain = true
                    viewModel.searchMovie(query)
                }
            }
        }

        observeAnyChangeSearchMovie()
        setupRecyclerView()

        adapter?.onMovieClickListener(object : OnMovieClickListener {
            override fun onMovieClick(movies: Movie, genres: String) {
                val intent = Intent(this@SearchMovieActivity, MovieDetailActivity::class.java)
                intent.putExtra(MovieDetailActivity.movie, movies)
                intent.putExtra(MovieDetailActivity.genres, genres)
                startActivity(intent)
            }
        })

    }

    fun observeAnyChangeSearchMovie() {
        viewModel.searchResponse.observe(this) {
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

    private val scrolListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                viewModel.searchMovie(binding.search.text.toString())
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

    companion object {
        const val query = "query"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}