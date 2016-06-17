package com.juan.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/** Fragment used to show details for a selected movie */
public class DetailFragment extends Fragment {

    private String posterURL;
    private String movieKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the posterURL
        posterURL = getString(R.string.base_image_url).concat(getString(R.string.movie_poster_size));

        // Get the key for the parcelable Movie object
        movieKey = getString(R.string.parcelable_movie_key);

        // Enable options menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get the Movie object passed in to from the MoviesFragment through an Intent
        Movie movie = getActivity().getIntent().getExtras().getParcelable(movieKey);

        // Set the movie title in the titleView
        TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
        titleView.setText(movie.getTitle());

        // Load the movie image into the ImageView using Glide
        ImageView posterView = (ImageView) rootView.findViewById(R.id.posterView);
        Glide
                .with(this)
                .load(posterURL.concat(movie.getPosterPath()))
                .into(posterView);

        // Set the release date in the releaseDateView
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.releaseDateView);
        releaseDateView.setText("Release date: \n".concat(movie.getReleaseDate()));

        // Set the ratings in the ratingsView
        TextView ratingsView = (TextView) rootView.findViewById(R.id.ratingsView);
        ratingsView.setText("Rating: \n".concat(String.valueOf(movie.getUserRating()))
                                      .concat(" / 10"));

        // Set the synopsis in the ratingsView
        TextView synopsisView = (TextView) rootView.findViewById(R.id.synopsisView);
        synopsisView.setText(movie.getSynopsis());

        return rootView;
    }
}
