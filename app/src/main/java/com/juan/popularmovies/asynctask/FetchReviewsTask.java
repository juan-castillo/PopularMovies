package com.juan.popularmovies.asynctask;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.juan.popularmovies.BuildConfig;
import com.juan.popularmovies.DetailFragment;
import com.juan.popularmovies.model.Movie;
import com.juan.popularmovies.model.Review;

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

/** AsynTask that fetches movie reviews  */
public class FetchReviewsTask extends AsyncTask<Movie, Void, List<Review>> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
    final String MOVIES_REVIEWS_PATH = "reviews";
    final String API_KEY_PARAM = "api_key";

    final String MOVIE_REVIEW_LIST = "results";
    final String MOVIE_REVIEW_AUTHOR = "author";
    final String MOVIE_REVIEW_CONTENT= "content";

    private StringBuffer jsonStringBuffer = new StringBuffer();
    private DetailFragment detailFragment;

    public FetchReviewsTask(DetailFragment detailFragment) {
        this.detailFragment = detailFragment;
    }

    @Override
    protected List<Review> doInBackground(Movie... params) {

        // Make sure a movie is passed in
        if (params.length == 0) {
            return null;
        }

        Movie movie = params[0];
        try {
            return getMovieReviewsViaJson(movie);
        } catch(Exception e) {
            Log.e(LOG_TAG, "Error getting movie details", e);
        }

        // Should only get here if something went wrong
        return null;
    }

    /** Sends an HTTP Request to TheMovieDatabase API and returns an array of Reviews */
    private List<Review> getMovieReviewsViaJson(Movie movie) throws JSONException {

        // Lookup movie trailers
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try {
            // Build URL for TheMovieDatabase API
            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendPath(String.valueOf(movie.getId()))
                    .appendPath(MOVIES_REVIEWS_PATH)
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
            Log.e(LOG_TAG, "Error getting movie reviews", e);
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
        JSONObject reviewsJson = new JSONObject(jsonStringBuffer.toString());
        JSONArray reviewsArray = reviewsJson.getJSONArray(MOVIE_REVIEW_LIST);

        List<Review> reviews = new ArrayList<>();

        // Loop through all reviews in the JSONArray and add them to the Review array
        for (int i = 0; i < reviewsArray.length(); i++) {

            // Get JSONObject representing a movie trailer from the trailersArray
            JSONObject reviewObject = reviewsArray.getJSONObject(i);
            Review review = new Review(
                    reviewObject.getString(MOVIE_REVIEW_AUTHOR),
                    reviewObject.getString(MOVIE_REVIEW_CONTENT));

            reviews.add(review);
        }

        return reviews;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        if (reviews != null && reviews.size() > 0) {
            detailFragment.setReviews(reviews);
        }
    }
}
