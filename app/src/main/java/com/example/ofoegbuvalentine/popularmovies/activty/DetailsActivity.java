package com.example.ofoegbuvalentine.popularmovies.activty;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ofoegbuvalentine.popularmovies.Utilities;
import com.example.ofoegbuvalentine.popularmovies.R;
import com.example.ofoegbuvalentine.popularmovies.adapter.MovieReviewsAdapter;
import com.example.ofoegbuvalentine.popularmovies.adapter.MovieTrailerAdapter;
import com.example.ofoegbuvalentine.popularmovies.api.Client;
import com.example.ofoegbuvalentine.popularmovies.api.Service;
import com.example.ofoegbuvalentine.popularmovies.data.DatabaseUtils;
import com.example.ofoegbuvalentine.popularmovies.data.FavoritesContract;
import com.example.ofoegbuvalentine.popularmovies.data.Review;
import com.example.ofoegbuvalentine.popularmovies.data.Trailer;
import com.example.ofoegbuvalentine.popularmovies.data.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ofoegbuvalentine.popularmovies.adapter.MovieAdapter.MOVIE;

public class DetailsActivity extends AppCompatActivity {

    private static final Service API_INTERFACE = Client.getClient().create(Service.class);
    @BindView(R.id.tv_rating)
    TextView mMovieRating;
    @BindView(R.id.tv_release_date)
    TextView mMovieReleaseDate;
    @BindView(R.id.tv_overview)
    TextView mMovieOverview;
    @BindView(R.id.im_movie_poster)
    ImageView mMoviePoster;
    @BindView(R.id.rv_trailers)
    RecyclerView mTrailerRecycler;
    @BindView(R.id.rv_reviews)
    RecyclerView mReviewRecycler;
    @BindView(R.id.tv_reviews)
    TextView mReview;
    @BindView(R.id.tv_trailers)
    TextView mTrailer;
    @BindView(R.id.collapse_toolBar)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;
    private ArrayList<Trailer> mTrailersList = new ArrayList<>();
    private ArrayList<Review> mReviewsList = new ArrayList<>();
    private MovieTrailerAdapter mTrailerAdapter;
    private MovieReviewsAdapter mReviewsAdapter;
    private Movie selectedMovie;
    private final String SCROLL_POSITION = "scroll-position";
    private int[] mPosition = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        if (getIntent() != null) {

            selectedMovie = getIntent().getParcelableExtra(MOVIE);
            toolbarLayout.setTitle(selectedMovie.getTitle());
            mMovieRating.append("User Ratings \n" + getString(R.string.vote_average, selectedMovie.getVoteAverage()));
            mMovieReleaseDate.append("Release Date \n" + selectedMovie.getReleaseDate());
            mMovieOverview.setText(selectedMovie.getOverview());
            Glide.with(this)
                    .load(selectedMovie.getPosterUrl())
                    .error(R.drawable.ic_error_black_24dp)
                    .into(mMoviePoster);

            getMovieTrailers();
            getMovieReviews();
        } else {
            Utilities.showToast(this, getString(R.string.error_toast), Toast.LENGTH_LONG);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(SCROLL_POSITION, new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPosition = savedInstanceState.getIntArray(SCROLL_POSITION);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mPosition != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(mPosition[0], mPosition[1]);
                }
            }, 1000);
        }
    }

    private void getMovieTrailers() {
        Call<JsonObject> call = API_INTERFACE.getMovieTrailers(selectedMovie.getMovieId(), getString(R.string.api_key));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Type trailerType = new TypeToken<List<Trailer>>() {
                }.getType();
                mTrailersList = new Gson().fromJson(response.body().getAsJsonArray("results"), trailerType);
                if (mTrailersList.isEmpty()) mTrailer.setText(getString(R.string.no_trailers));
                mTrailerAdapter = new MovieTrailerAdapter(DetailsActivity.this, mTrailersList);
                mTrailerRecycler.setAdapter(mTrailerAdapter);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Utilities.showToast(DetailsActivity.this, getString(R.string.error_toast), Toast.LENGTH_LONG);
            }
        });
    }

    private void getMovieReviews() {
        Call<JsonObject> call = API_INTERFACE.getMovieReviews(selectedMovie.getMovieId(), getString(R.string.api_key));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Type reviewType = new TypeToken<List<Review>>() {
                }.getType();
                mReviewsList = new Gson().fromJson(response.body().getAsJsonArray("results"), reviewType);
                if (mReviewsList.isEmpty()) mReview.setText(getString(R.string.no_reviews));
                mReviewsAdapter = new MovieReviewsAdapter(DetailsActivity.this, mReviewsList);
                mReviewRecycler.setAdapter(mReviewsAdapter);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Utilities.showToast(DetailsActivity.this, getString(R.string.error_toast), Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_favorite:
                if (isFavorite()) {
                    removeMovieFromFavorites();
                    item.setIcon(R.drawable.ic_favorite_normal);

                } else {
                    addMovieToFavorites();
                    item.setIcon(R.drawable.ic_favorite_added);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeMovieFromFavorites() {
        String selection = FavoritesContract.MoviesEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(selectedMovie.getMovieId())};
        getContentResolver().delete(FavoritesContract.buildMovieUriWithId(selectedMovie.getMovieId()),
                selection, selectionArgs);
        getContentResolver().delete(FavoritesContract.buildTrailerUriWithId(selectedMovie.getMovieId()),
                selection, selectionArgs);
        getContentResolver().delete(FavoritesContract.buildReviewUriWithId(selectedMovie.getMovieId()),
                selection, selectionArgs);
    }

    synchronized private void addMovieToFavorites() {
        getContentResolver().insert(FavoritesContract.buildMovieUriWithId(selectedMovie.getMovieId()),
                DatabaseUtils.getMovieDetails(this, selectedMovie));
        if (!mTrailersList.isEmpty())
            getContentResolver().insert(FavoritesContract.buildTrailerUriWithId(selectedMovie.getMovieId()),
                    DatabaseUtils.getTrailerDetails(selectedMovie, mTrailersList));
        if (!mReviewsList.isEmpty())
            getContentResolver().insert(FavoritesContract.buildReviewUriWithId(selectedMovie.getMovieId()),
                    DatabaseUtils.getReviewDetails(selectedMovie, mReviewsList));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_favorite);
        if (isFavorite()) {
            menuItem.setIcon(R.drawable.ic_favorite_added);
        } else {
            menuItem.setIcon(R.drawable.ic_favorite_normal);
        }
        return true;
    }

    private boolean isFavorite() {
        String[] projection = {FavoritesContract.MoviesEntry.COLUMN_ID};
        String selection = FavoritesContract.MoviesEntry.COLUMN_ID + " = " + selectedMovie.getMovieId();
        Cursor cursor = getContentResolver().query(FavoritesContract.buildMovieUriWithId(selectedMovie.getMovieId()),
                projection,
                selection,
                null,
                null,
                null);
        return (cursor != null ? cursor.getCount() : 0) > 0;
    }
}
