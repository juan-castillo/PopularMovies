package com.juan.popularmovies.data;

import android.provider.BaseColumns;

public class MovieContract {

    public MovieContract(){}

    public static abstract class FavoriteMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_KEY = "poster";
        public static final String COLUMN_MOVIE_RATING = "rating";
    }
}
