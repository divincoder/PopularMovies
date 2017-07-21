package com.example.ofoegbuvalentine.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ofoegbuvalentine.popularmovies.R;
import com.example.ofoegbuvalentine.popularmovies.activty.DetailsActivity;
import com.example.ofoegbuvalentine.popularmovies.data.Movie;

import java.util.List;

/**
 * Created by Ofoegbu Valentine on 16/04/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public static final String MOVIE = "movie";
    private Context mContext;
    private List<Movie> mMoviesList;

    public MovieAdapter(final Context context, List<Movie> moviesList) {
        mContext = context;
        mMoviesList = moviesList;
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {
        holder.setMovieThumbnail(mContext, mMoviesList.get(position).getPosterUrl());
    }

    @Override
    public int getItemCount() {
        if (mMoviesList == null) {
            return 0;
        }
        return mMoviesList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mMovieThumbnail;

        public MovieViewHolder(final View itemView) {
            super(itemView);
            mMovieThumbnail = (ImageView) itemView.findViewById(R.id.movie_image);
            itemView.setOnClickListener(this);
        }


        /**
         * A convenience method to set movie thumbnail in ImageView
         *
         * @param context  Context of the activity where the view is displayed
         * @param imageUrl String URL of the image to be displayed
         */
        void setMovieThumbnail(final Context context, final String imageUrl) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(mMovieThumbnail);
        }

        /**
         * Called whenever a user clicks a view
         *
         * @param v The view that was clicked
         */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailsActivity.class);
            intent.putExtra(MOVIE, mMoviesList.get(getAdapterPosition()));
            mContext.startActivity(intent);
        }
    }
}
