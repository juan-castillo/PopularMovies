package com.juan.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.juan.popularmovies.asynctask.FetchReviewsTask;
import com.juan.popularmovies.asynctask.FetchTrailersTask;
import com.juan.popularmovies.data.MovieDbHelper;
import com.juan.popularmovies.model.Movie;
import com.juan.popularmovies.model.Review;
import com.juan.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

/** Fragment used to show details for a selected movie */
public class DetailFragment extends Fragment {

    private static final String BASE_TRAILER_URL = "http://www.youtube.com/watch?v=";

    private List<Trailer> trailers = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();
    private LinearLayout trailersList;
    private LinearLayout reviewsList;
    private String posterURL;
    private View rootView;
    private ViewGroup container;
    private Movie movie;
    private boolean dualPane;

    private MovieDbHelper movieDbHelper;
    OnFavoritesChangedListener listener;


    public interface OnFavoritesChangedListener {
        void onFavoritesChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().findViewById(R.id.movie_detail_container) != null) {
            dualPane = true;
        }

        movieDbHelper = new MovieDbHelper(getContext());

        // Create the posterURL
        posterURL = getString(R.string.base_image_url).concat(getString(R.string.movie_poster_size));

        // Enable options menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnFavoritesChangedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement onFavoritesChangedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle == null) {
            bundle = getArguments();
        }

        if (bundle != null) {
            movie = bundle.getParcelable("com.juan.popularmovies.model.Movie");
        }

        if (movie != null) {
            new FetchTrailersTask(this).execute(movie);
            new FetchReviewsTask(this).execute(movie);

            // Set the movie title in the titleView
            TextView titleView = (TextView) rootView.findViewById(R.id.move_title);
            titleView.setText(movie.getTitle());

            // Load the movie image into the ImageView using Glide
            ImageView posterView = (ImageView) rootView.findViewById(R.id.movie_poster);
            posterView.setContentDescription(movie.getTitle());
            Glide
                    .with(this)
                    .load(posterURL.concat(movie.getPosterPath()))
                    .into(posterView);

            // Set the release date in the releaseDateView
            TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
            releaseDateView.setText("Release date: \n  ".concat(movie.getReleaseDate()));

            // Set the ratings in the ratingsView
            TextView ratingsView = (TextView) rootView.findViewById(R.id.ratings_view);
            ratingsView.setText("Rating: \n  ".concat(String.valueOf(movie.getUserRating()))
                    .concat(" / 10"));

            // Setup the favorites button
            final Button button = (Button) rootView.findViewById(R.id.favorite_button);
            if (movieDbHelper.isMovieFavorited(movie)) {
                button.setText(getString(R.string.button_remove_from_favorites_text));
            } else {
                button.setText(getString(R.string.button_add_to_favorites_text));
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (movieDbHelper.isMovieFavorited(movie)) {
                        movieDbHelper.removeMovieFromFavorites(movie);
                        Toast.makeText(getActivity(), "Removed from Favorites!", Toast.LENGTH_SHORT).show();
                        button.setText(getString(R.string.button_add_to_favorites_text));
                        listener.onFavoritesChanged();

                    } else {
                        movieDbHelper.addMovieToFavorites(movie);
                        Toast.makeText(getActivity(), "Added to Favorites!", Toast.LENGTH_SHORT).show();
                        button.setText(getString(R.string.button_remove_from_favorites_text));
                        listener.onFavoritesChanged();
                    }
                }
            });

            // Set the synopsis in the synopsisView
            TextView synopsisView = (TextView) rootView.findViewById(R.id.synopsis);
            synopsisView.setText(movie.getSynopsis());

        }

        return rootView;
    }

    private void showTrailers() {
        // Add trailers to the trailers_list
        trailersList = (LinearLayout) rootView.findViewById(R.id.trailers_list);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        for (final Trailer trailer : trailers) {
            View trailerView = inflater.inflate(R.layout.list_item_trailer, container, false);

            ImageView playButton = (ImageView) trailerView.findViewById(R.id.play_button);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri youTubeUri = Uri.parse(BASE_TRAILER_URL.concat(trailer.getKey()));
                    Intent trailerIntent = new Intent(Intent.ACTION_VIEW, youTubeUri);
                    startActivity(trailerIntent);
                }
            });

            TextView trailerName = (TextView) trailerView.findViewById(R.id.trailer_name);
            trailerName.setText(trailer.getName());

            trailersList.addView(trailerView);
        }
    }

    private void showReviews() {
        // Add reviews to the reviews_list
        reviewsList = (LinearLayout) rootView.findViewById(R.id.reviews_list);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        for (Review review : reviews) {
            View reviewView = inflater.inflate(R.layout.list_item_review, container, false);

            TextView reviewAuthor = (TextView) reviewView.findViewById(R.id.review_author);
            reviewAuthor.setText(review.getAuthor());

            TextView reviewContent = (TextView) reviewView.findViewById(R.id.review_content);
            reviewContent.setText(review.getContent());

            reviewsList.addView(reviewView);
        }
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
        showTrailers();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        showReviews();
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
