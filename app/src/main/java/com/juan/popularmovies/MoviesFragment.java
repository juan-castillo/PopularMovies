package com.juan.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/** Fragment used to show a grid of movies sorted by either popularity or ratings */
public class MoviesFragment extends Fragment {

    private MovieArrayAdapter movieArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable options menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        // Initialize the MovieArrayAdapter
        movieArrayAdapter = new MovieArrayAdapter(getContext(), new ArrayList<Movie>());

        // Attach the MovieArrayAdapter to the GridView
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieArrayAdapter);
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Movie movie = movieArrayAdapter.getItem(position);

                        /* Could've easily just passed in string values to the intent but wanted
                         * to try implementing Parcelable since I've never used it before* */
                        Intent movieDetailIntent = new Intent(getActivity(), DetailActivity.class);
                        movieDetailIntent.putExtra("com.juan.popularmovies.Movie", movie);

                        startActivity(movieDetailIntent);
                    }
                }
        );

        return rootView;
    }

    /** Calls FetchMoviesTask to update the gridview with a list of movies based on sort order*/
    private void updateMovies() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPreferences.getString(getString(R.string.pref_sorting_key), getString(R.string.pref_sorting_default));
        new FetchMoviesTask().execute(sortOrder);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    /** AsynTask that fetches movies based on popularity or ratings  */
    public class FetchMoviesTask extends AsyncTask<String, Movie[], Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
        final String API_KEY_PARAM = "api_key";

        final String MOVIE_LIST = "results";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_POSTER = "poster_path";
        final String MOVIE_SYNOPSIS = "overview";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";

        private StringBuffer jsonStringBuffer = new StringBuffer();

        /** Parses the JSON String returned from TheMovieDatabase API and returns a Movie array*/
        private Movie[] getMoviesViaJson(String jsonString) throws JSONException{

            JSONObject moviesJson = new JSONObject(jsonString);
            JSONArray movieArray = moviesJson.getJSONArray(MOVIE_LIST);

            Movie[] movies = new Movie[movieArray.length()];

            // Loop through all movies in the JSONArray and add them to the Movie array
            for (int i = 0; i < movieArray.length(); i++) {
                // Get JSONObject representing a movie from the movieArray
                JSONObject movieObject = movieArray.getJSONObject(i);
                Movie movie = new Movie(
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

            // If no param is found then use a default sort order
            String sortOrder;
            if (params.length != 0) {
                sortOrder = params[0];
            } else {
                sortOrder = getString(R.string.pref_sorting_default);
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
                movieArrayAdapter.clear();
                movieArrayAdapter.addAll(movies);
            }
        }
    }
}
