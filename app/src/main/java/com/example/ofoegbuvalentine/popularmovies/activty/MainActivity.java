package com.example.ofoegbuvalentine.popularmovies.activty;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ofoegbuvalentine.popularmovies.adapter.MovieAdapter;
import com.example.ofoegbuvalentine.popularmovies.Utilities;
import com.example.ofoegbuvalentine.popularmovies.R;
import com.example.ofoegbuvalentine.popularmovies.api.Client;
import com.example.ofoegbuvalentine.popularmovies.api.Service;
import com.example.ofoegbuvalentine.popularmovies.data.DatabaseUtils;
import com.example.ofoegbuvalentine.popularmovies.data.FavoritesContract;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BUNDLE_RECYCLER_LAYOUT = "MainActivity.recycler.layout";
    private static final String SORT_STATE = "sort_state";
    private static final String SORT_TITLE = "sort_title";
    private final static String SORT_TOP = "Top Rated";
    private final static String SORT_POPULAR = "Popular";
    private final static String SORT_FAVORITE = "Favorite";
    private static final Service API_INTERFACE = Client.getClient().create(Service.class);
    private static final Type TYPE = new TypeToken<List<Movie>>() {
    }.getType();
    private boolean isTopRated = false;
    private String currentSort = SORT_POPULAR;
    @BindView(R.id.recycler_view)
    RecyclerView mMoviesRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    private ArrayList<Movie> mMoviesList;
    private MovieAdapter mMoviesAdapter;
    private GridLayoutManager layoutManager;
    private Parcelable mListState;
    private static Bundle mBundleRecyclerViewState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mMoviesRecyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {

            mMoviesList = savedInstanceState.getParcelableArrayList(MOVIE);
            currentSort = savedInstanceState.getString(SORT_TITLE);

            switch (currentSort) {
                case SORT_POPULAR:
                    toolbarLayout.setTitle(getString(R.string.title, SORT_POPULAR));
                    break;
                case SORT_TOP:
                    toolbarLayout.setTitle(getString(R.string.title, SORT_TOP));
                    break;
                case SORT_FAVORITE:
                    toolbarLayout.setTitle(getString(R.string.title, SORT_FAVORITE));
                    break;
                default:
                    toolbarLayout.setTitle(getString(R.string.title, SORT_POPULAR));
            }
            loadData();

        } else {
            getMoviesBySortOrder(isTopRated);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSavedInstanceState called");
        outState.putParcelableArrayList(MOVIE, mMoviesList);
        outState.putString(SORT_TITLE, currentSort);
        outState.putBoolean(SORT_STATE, isTopRated);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        updateFavorites();
        ActivityCompat.invalidateOptionsMenu(this);
    }

    private void updateFavorites() {
        if (currentSort.equals(SORT_FAVORITE)) {
            getFavoriteMovies();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
        mBundleRecyclerViewState = new Bundle();
        mListState = mMoviesRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mListState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mBundleRecyclerViewState != null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
                    mMoviesRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                }
            }, 50);
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutManager.setSpanCount(3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager.setSpanCount(2);
        }
        mMoviesRecyclerView.setLayoutManager(layoutManager);
    }

    //Method to show no network connectivity dialog
    private void showNoConnectionDialog() {

        Utilities.showDialog(this, android.R.drawable.ic_dialog_alert, R.string.internet)
                .setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .show();
    }

    /**
     * Method to load data in views
     */
    private void loadData() {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMoviesAdapter = new MovieAdapter(MainActivity.this, mMoviesList);
        mMoviesAdapter.notifyDataSetChanged();
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);
    }

    /**
     * helper method to get popular movies
     */
    private void getPopularMovies() {
        Call<JsonObject> call = API_INTERFACE.getPopularMovies(getString(R.string.api_key), 1);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mMoviesList = new Gson().fromJson(response.body().getAsJsonArray("results"), TYPE);
                loadData();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                t.printStackTrace();
                Utilities.showToast(MainActivity.this, getString(R.string.toast_error), Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * method to get top rated movies
     */
    private void getTopRatedMovies() {
        Call<JsonObject> call = API_INTERFACE.getTopRatedMovies(getString(R.string.api_key), 1);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mMoviesList = new Gson().fromJson(response.body().getAsJsonArray("results"), TYPE);
                loadData();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                t.printStackTrace();
                Utilities.showToast(MainActivity.this, getString(R.string.toast_error), Toast.LENGTH_LONG);
            }
        });
    }

    private void getFavoriteMovies() {
        ArrayList<Movie> movieList = DatabaseUtils.getFavoriteMovies(this);
        mMoviesList = movieList;
        toolbarLayout.setTitle(getString(R.string.title, getString(R.string.favorite)));
        loadData();

        if (movieList.size() == 0) {
            Utilities.showToast(this, "You haven't specified any Favorite movies", Toast.LENGTH_LONG);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().equals(SORT_TOP)) {
            getMoviesBySortOrder(true);
            isTopRated = true;
            currentSort = SORT_TOP;
            item.setTitle(SORT_POPULAR);
        } else if (item.getTitle().equals(SORT_POPULAR)) {
            getMoviesBySortOrder(false);
            isTopRated = false;
            currentSort = SORT_POPULAR;
            item.setTitle(SORT_TOP);
        } else if (item.getTitle().equals(SORT_FAVORITE)) {
            getFavoriteMovies();
            currentSort = SORT_FAVORITE;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.sort_order);
        if (isTopRated) {
            menuItem.setTitle(SORT_POPULAR);
        } else {
            menuItem.setTitle(SORT_TOP);
        }

        if (isFavoritesAvailable()) {
            menu.findItem(R.id.sort_favorite).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * method to re-query movie db API and update views based on user sort selection
     *
     * @param sortChoice selected sort choice
     */
    private void getMoviesBySortOrder(boolean sortChoice) {
        if (sortChoice) {
            toolbarLayout.setTitle(getString(R.string.title, SORT_TOP));
            if (Utilities.isNetworkConnected(this)) {
                getTopRatedMovies();
            } else {
                showNoConnectionDialog();
            }
        } else {
            toolbarLayout.setTitle(getString(R.string.title, SORT_POPULAR));
            getPopularMovies();
        }
    }

    private boolean isFavoritesAvailable() {
        String[] projection = {FavoritesContract.MoviesEntry._ID};
        String selection = FavoritesContract.MoviesEntry._ID + " IS NOT NULL";
        Cursor cursor = getContentResolver().query(FavoritesContract.MOVIES_CONTENT_URI,
                projection,
                selection,
                null,
                null,
                null);
        return (cursor != null ? cursor.getCount() : 0) > 0;
    }
}