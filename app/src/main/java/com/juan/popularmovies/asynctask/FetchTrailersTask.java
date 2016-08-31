package com.juan.popularmovies.asynctask;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.juan.popularmovies.BuildConfig;
import com.juan.popularmovies.DetailFragment;
import com.juan.popularmovies.model.Movie;
import com.juan.popularmovies.model.Trailer;

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
import java.util.List;

/** AsynTask that fetches movie trailers  */
public class FetchTrailersTask extends AsyncTask<Movie, Void, List<Trailer>> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

    final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
    final String MOVIES_VIDEOS_PATH = "videos";
    final String API_KEY_PARAM = "api_key";

    final String MOVIE_TRAILER_LIST = "results";
    final String MOVIE_TRAILER_NAME = "name";
    final String MOVIE_TRAILER_KEY= "key";
    final String MOVIE_TRAILER_SITE= "site";
    final String MOVIE_TRAILER_TYPE= "type";
    final String MOVIE_TRAILER_FILTER_SITE = "YouTube";
    final String MOVIE_TRAILER_FILTER_TYPE = "Trailer";

    private StringBuffer jsonStringBuffer = new StringBuffer();
    private DetailFragment detailFragment;

    public FetchTrailersTask(DetailFragment detailFragment) {
        this.detailFragment = detailFragment;
    }

    private boolean isYouTubeSource(String src) {
        return src.equals(MOVIE_TRAILER_FILTER_SITE);
    }

    private boolean isTrailer(String type) {
        return type.equals(MOVIE_TRAILER_FILTER_TYPE);
    }

    @Override
    protected List<Trailer> doInBackground(Movie... params) {

        // Make sure a movie is passed in
        if (params.length == 0) {
            return null;
        }

        Movie movie = params[0];
        try {
            return getMovieTrailersViaJson(movie);
        } catch(Exception e) {
            Log.e(LOG_TAG, "Error getting movie details", e);
        }

        // Should only get here if something went wrong
        return null;
    }

    /** Sends an HTTP Request to TheMovieDatabase API and returns an array of Trailers */
    private List<Trailer> getMovieTrailersViaJson(Movie movie) throws JSONException {

        // Lookup movie trailers
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try {
            // Build URL for TheMovieDatabase API
            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendPath(String.valueOf(movie.getId()))
                    .appendPath(MOVIES_VIDEOS_PATH)
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
            Log.e(LOG_TAG, "Error getting movie trailers", e);
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

        // Parse the JSON response
        JSONObject trailersJson = new JSONObject(jsonStringBuffer.toString());
        JSONArray trailersArray = trailersJson.getJSONArray(MOVIE_TRAILER_LIST);

        List<Trailer> trailers = new ArrayList<>();

        // Loop through all trailers in the JSONArray and add them to the Trailer array
        for (int i = 0; i < trailersArray.length(); i++) {

            // Get JSONObject representing a movie trailer from the trailersArray
            JSONObject trailerObject = trailersArray.getJSONObject(i);
            boolean isYouTubeSource = isYouTubeSource(trailerObject.getString(MOVIE_TRAILER_SITE));
            boolean isTrailer = isTrailer(trailerObject.getString(MOVIE_TRAILER_TYPE));

            if (isYouTubeSource && isTrailer) {
                // Only want to add YouTube trailers currently
                Trailer trailer = new Trailer(
                        trailerObject.getString(MOVIE_TRAILER_NAME),
                        trailerObject.getString(MOVIE_TRAILER_KEY));

                trailers.add(trailer);
            }
        }

        return trailers;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        if (trailers != null && trailers.size() > 0) {
            detailFragment.setTrailers(trailers);
        }
    }

}
