package id.fannan.themoviedb.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fannan.themoviedb.BuildConfig
import id.fannan.themoviedb.databinding.MovieListBinding
import id.fannan.themoviedb.entity.Genres
import id.fannan.themoviedb.entity.Movie
import id.fannan.themoviedb.ui.listeners.OnMovieClickListener

class MovieListAdapter : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {
    private lateinit var onMovieClickListener: OnMovieClickListener
    private val genresList = ArrayList<Genres>()

    fun onMovieClickListener(onMovieClickListener: OnMovieClickListener) {
        this.onMovieClickListener = onMovieClickListener
    }

    fun setGenres(list: List<Genres>) {
        this.genresList.clear()
        this.genresList.addAll(list)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem


        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)


    inner class ViewHolder(val binding: MovieListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MovieListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(differ.currentList[position]) {
                binding.apply {
                    title.text = originalTitle
                    lang.text = originalLanguage
                    releaseDate.text = differ.currentList[position].releaseDate
                    ratingText.text = voteAverage.toString()
                    ratingBar.rating = voteAverage?.div(2) ?: 0f

                    val uriPoster = "${BuildConfig.PHOTO_BASE_URL}$posterPath"
                    Glide.with(itemView).load(uriPoster).into(poster)

                    val map = genresList.associate { it.id to it.name }
                    val genres = StringBuilder()


                    val genresId = ArrayList<Int>()
                    if (genreIds != null) {
                        genresId.addAll(genreIds)
                        for (data in genresId) {
                            genres.append("${map[data]}, ")
                        }
                    }
                    genre.text = genres.dropLast(2)
                    itemView.setOnClickListener {
                        onMovieClickListener.onMovieClick(this@with, genres.toString())
                    }
                }
            }
        }
    }
}