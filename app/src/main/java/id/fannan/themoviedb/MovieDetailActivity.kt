package id.fannan.themoviedb

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fannan.themoviedb.databinding.ActivityMovieDetailBinding
import id.fannan.themoviedb.databinding.ActivityMovieListBinding
import id.fannan.themoviedb.entity.Movie
import id.fannan.themoviedb.ui.adapter.MovieListAdapter
import id.fannan.themoviedb.ui.vm.MovieViewModel

class MovieDetailActivity : AppCompatActivity() {
    private var _binding: ActivityMovieDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        intent.parcelable<Movie>(movie)?.let {
            intent.getStringExtra(genres)
                ?.let { it1 -> setupData(it, it1) }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setupData(movie: Movie, genres: String) {
        with(movie) {
            binding.apply {
                val uriPoster = "${BuildConfig.PHOTO_BASE_URL}${posterPath}"
                Glide.with(this@MovieDetailActivity).load(uriPoster).into(posterDetail)
                titleDetail.text = title
                releaseDateDetail.text = releaseDate
                ratingText.text = voteAverage.toString()
                ratingBar.rating = voteAverage?.div(2) ?: 0f
                genreDetail.text = genres.dropLast(2)
                overview.text = movie.overview
            }
        }

    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    companion object {
        const val movie = "movie"
        const val genres = "genres"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}