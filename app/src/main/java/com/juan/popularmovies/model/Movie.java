package com.juan.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/** Object used to represent a movie. Implements Parcelable to be able to pass it through Intents
 *  and onSaveInstanceState*/
public class Movie implements Parcelable {

    private long id;
    private String title;
    private String posterPath;
    private String synopsis;
    private double userRating;
    private String releaseDate;

    public Movie(long id, String title, String posterPath, String synopsis, double userRating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public Movie(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.posterPath = in.readString();
        this.synopsis = in.readString();
        this.userRating = in.readDouble();
        this.releaseDate = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(synopsis);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
