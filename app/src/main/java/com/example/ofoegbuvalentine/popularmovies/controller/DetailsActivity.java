package com.example.ofoegbuvalentine.popularmovies.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ofoegbuvalentine.popularmovies.R;
import com.example.ofoegbuvalentine.popularmovies.model.Movie;

import static com.example.ofoegbuvalentine.popularmovies.MovieAdapter.MOVIE;

public class DetailsActivity extends AppCompatActivity {

    private TextView mMovieTitle, mMovieRating, mMovieReleaseDate, mMovieOverview;
    private ImageView mMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        mMovieRating = (TextView) findViewById(R.id.tv_rating);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mMovieOverview = (TextView) findViewById(R.id.tv_overview);
        mMoviePoster = (ImageView) findViewById(R.id.im_movie_poster);

        Movie selectedMovie = getIntent().getParcelableExtra(MOVIE);

        mMovieTitle.setText(selectedMovie.getTitle());
        mMovieRating.append("User Ratings \n" + getString(R.string.vote_average, selectedMovie.getVoteAverage()));
        mMovieReleaseDate.append("Release Date \n" + selectedMovie.getReleaseDate());
        mMovieOverview.setText(selectedMovie.getOverview());
        Glide.with(this)
                .load(selectedMovie.getPosterUrl())
                .into(mMoviePoster);
    }
}
