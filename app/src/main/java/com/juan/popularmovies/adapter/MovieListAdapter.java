package com.juan.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.juan.popularmovies.R;
import com.juan.popularmovies.model.Movie;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private ArrayList<Movie> movies;
    private String posterURL;
    private Context context;
    private final OnMovieSelectedListener listener;


    public interface OnMovieSelectedListener {
        void onMovieSelected(Movie movie);
    }

    public MovieListAdapter(Context context, ArrayList<Movie> movies, OnMovieSelectedListener listener) {
        this.movies = movies;
        this.context = context;
        this.listener = listener;

        // Create the base URL for movie posters
        posterURL = context.getString(R.string.base_image_url)
                .concat(context.getString(R.string.movie_poster_size));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.list_item_movie, viewGroup, false);

        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Movie movie = movies.get(position);

        // Load the movie image into the ImageView using Glide
        ImageView imageView = viewHolder.posterView;
        imageView.setContentDescription(movie.getTitle());
        Glide
                .with(context)
                .load(posterURL.concat(movie.getPosterPath()))
                .fitCenter()
                .into(imageView);

        // Setup OnClickListener
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMovieSelected(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView posterView;

        public ViewHolder(View view) {
            super(view);
            posterView = (ImageView) view.findViewById(R.id.movie_poster);
        }
    }

}
