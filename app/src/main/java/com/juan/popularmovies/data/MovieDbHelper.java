package com.juan.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.juan.popularmovies.data.MovieContract.FavoriteMovieEntry;
import com.juan.popularmovies.model.Movie;

import java.util.ArrayList;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "popularmovies.db";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME;
    public static final String SQL_BASE_SELECT = "SELECT * FROM " + FavoriteMovieEntry.TABLE_NAME +
            " WHERE " + FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ";

    private static final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
            FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY, " +
            FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
            FavoriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
            FavoriteMovieEntry.COLUMN_POSTER_KEY + " TEXT NOT NULL, " +
            FavoriteMovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
            FavoriteMovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
            FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " INTEGER NOT NULL, " +
            " UNIQUE(" + FavoriteMovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

    private static final String[] FAVORITE_MOVIES_PROJECTION = new String[] {
            FavoriteMovieEntry.COLUMN_MOVIE_ID,
            FavoriteMovieEntry.COLUMN_MOVIE_TITLE,
            FavoriteMovieEntry.COLUMN_POSTER_KEY,
            FavoriteMovieEntry.COLUMN_MOVIE_SYNOPSIS,
            FavoriteMovieEntry.COLUMN_MOVIE_RATING,
            FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE
    };

    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_MOVIE_TITLE = 1;
    private static final int INDEX_POSTER_KEY = 2;
    private static final int INDEX_MOVIE_SYNOPSIS = 3;
    private static final int INDEX_MOVIE_RATING = 4;
    private static final int INDEX_MOVIE_RELEASE_DATE = 5;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long addMovieToFavorites(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(FavoriteMovieEntry.COLUMN_MOVIE_ID, movie.getId());
        values.put(FavoriteMovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        values.put(FavoriteMovieEntry.COLUMN_MOVIE_SYNOPSIS, movie.getSynopsis());
        values.put(FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        values.put(FavoriteMovieEntry.COLUMN_POSTER_KEY, movie.getPosterPath());
        values.put(FavoriteMovieEntry.COLUMN_MOVIE_RATING, movie.getUserRating());

        SQLiteDatabase db = getWritableDatabase();
        long val = db.insert(FavoriteMovieEntry.TABLE_NAME, null, values);
        db.close();

        return val;
    }

    public void removeMovieFromFavorites(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FavoriteMovieEntry.TABLE_NAME, FavoriteMovieEntry.COLUMN_MOVIE_ID + "=" + movie.getId(), null);
        db.close();
    }

    public ArrayList<Movie> getFavoriteMoviesList() {
        ArrayList<Movie> favorites = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(FavoriteMovieEntry.TABLE_NAME,
                FAVORITE_MOVIES_PROJECTION,
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                favorites.add(cursorToMovie(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return favorites;
    }

    private Movie cursorToMovie(Cursor cursor) {
        return new Movie(cursor.getLong(INDEX_MOVIE_ID),
                cursor.getString(INDEX_MOVIE_TITLE),
                "http://image.tmdb.org/t/p/w185" + cursor.getString(INDEX_POSTER_KEY),
                cursor.getString(INDEX_MOVIE_SYNOPSIS),
                cursor.getDouble(INDEX_MOVIE_RATING),
                cursor.getString(INDEX_MOVIE_RELEASE_DATE));
    }

    public boolean isMovieFavorited(Movie movie) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_BASE_SELECT + movie.getId(), null);

        boolean favorited = cursor.moveToFirst();
        cursor.close();
        db.close();

        return favorited;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
