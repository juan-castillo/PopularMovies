package com.juan.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/** Custom ArrayAdapter used to hold a list of movies and their poster images */
public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    private String posterURL;

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.list_item_movie, movies);

        // Create the base URL for movie posters
        posterURL = context.getString(R.string.base_image_url)
                .concat(context.getString(R.string.movie_poster_size));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the movie corresponding to the current position
        Movie movie = getItem(position);

        // Make sure the view isn't null
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        // Load the movie image into the ImageView using Glide
        Glide
            .with(getContext())
            .load(posterURL.concat(movie.getPosterPath()))
            .into((ImageView) convertView);

        return convertView;
    }
}
