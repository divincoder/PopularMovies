package com.example.ofoegbuvalentine.popularmovies.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ofoegbu Valentine on 16/04/2017.
 */

public interface Service {

    @GET("movie/popular")
    Call<JsonObject> getPopularMovies(@Query(value = "api_key") String API_KEY, @Query("page") int page);

    @GET("movie/top_rated")
    Call<JsonObject> getTopRatedMovies(@Query(value = "api_key") String API_KEY, @Query("page") int page);
}
