package com.juan.popularmovies.asynctask;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.juan.popularmovies.BuildConfig;
import com.juan.popularmovies.MovieListFragment;
import com.juan.popularmovies.R;
import com.juan.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** AsynTask that fetches movies based on popularity or ratings  */
public class FetchMovieListTask extends AsyncTask<String, Movie[], Movie[]> {

    private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();

    final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
    final String API_KEY_PARAM = "api_key";

    final String MOVIE_ID = "id";
    final String MOVIE_LIST = "results";
    final String MOVIE_TITLE = "original_title";
    final String MOVIE_POSTER = "poster_path";
    final String MOVIE_SYNOPSIS = "overview";
    final String MOVIE_RATING = "vote_average";
    final String MOVIE_RELEASE_DATE = "release_date";

    private StringBuffer jsonStringBuffer = new StringBuffer();
    private MovieListFragment movieListFragment;

    public FetchMovieListTask(MovieListFragment movieListFragment) {
        this.movieListFragment = movieListFragment;
    }

    /** Parses the JSON String returned from TheMovieDatabase API and returns a Movie array*/
    private Movie[] getMoviesViaJson(String jsonString) throws JSONException {

        JSONObject moviesJson = new JSONObject(jsonString);
        JSONArray movieArray = moviesJson.getJSONArray(MOVIE_LIST);

        Movie[] movies = new Movie[movieArray.length()];

        // Loop through all movies in the JSONArray and add them to the Movie array
        for (int i = 0; i < movieArray.length(); i++) {
            // Get JSONObject representing a movie from the movieArray
            JSONObject movieObject = movieArray.getJSONObject(i);
            Movie movie = new Movie(
                    movieObject.getLong(MOVIE_ID),
                    movieObject.getString(MOVIE_TITLE),
                    movieObject.getString(MOVIE_POSTER),
                    movieObject.getString(MOVIE_SYNOPSIS),
                    movieObject.getDouble(MOVIE_RATING),
                    movieObject.getString(MOVIE_RELEASE_DATE));
            movies[i] = movie;
        }

        return movies;
    }

    @Override
    protected Movie[] doInBackground(String... params) {

        // If for whatever reason no param is found then use a default sort order
        String sortOrder;
        if (params.length != 0) {
            sortOrder = params[0];
        } else {
            sortOrder = movieListFragment.getString(R.string.pref_sorting_default);
        }

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try {
            // Build URL for TheMovieDatabase API
            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendPath(sortOrder)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            // Open connection to TheMovieDatabase
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            // Read inputStream and store it in a StringBuffer
            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonStringBuffer.append(line);
            }

        } catch(Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing bufferedReader", e);
                }
            }
        }

        if (jsonStringBuffer.length() != 0) {
            try {
                return getMoviesViaJson(jsonStringBuffer.toString());
            } catch(Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }

        // Should only get here if something went wrong
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        if (movies != null) {
            movieListFragment.replaceMovieList(movies);
        }
    }
}
