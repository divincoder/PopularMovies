package com.example.ofoegbuvalentine.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ofoegbu Valentine on 16/04/2017.
 */

public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("original_title")
    private String title;
    @SerializedName("vote_average")
    private Double voteAverage;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("backdrop_path")
    private String backdropPath;
    private String backdropUrl = "https://image.tmdb.org/t/p/w500";
    private String posterUrl = "https://image.tmdb.org/t/p/w185";

    public Movie() {
    }

    protected Movie(Parcel in) {
        overview = in.readString();
        releaseDate = in.readString();
        title = in.readString();
        voteAverage = in.readDouble();
        posterPath = in.readString();
        posterUrl = in.readString();
        backdropPath = in.readString();
        backdropUrl = in.readString();

    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    private String getPosterPath() {
        return posterPath;
    }

    public String getPosterUrl() {
        return posterUrl + getPosterPath();
    }

    private String getBackdropPath() {
        return backdropPath;
    }

    public String getBackdropUrl() {
        return backdropUrl + getBackdropPath();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(title);
        dest.writeDouble(voteAverage);
        dest.writeString(posterPath);
        dest.writeString(posterUrl);
        dest.writeString(backdropPath);
        dest.writeString(backdropUrl);
    }
}
