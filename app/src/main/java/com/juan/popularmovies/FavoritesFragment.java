package com.juan.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juan.popularmovies.adapter.MovieListAdapter;
import com.juan.popularmovies.adapter.MovieListAdapter.OnMovieSelectedListener;
import com.juan.popularmovies.data.MovieDbHelper;
import com.juan.popularmovies.model.Movie;
import com.juan.popularmovies.view.MovieRecyclerView;

import java.util.ArrayList;

/** Fragment used to show a grid of favorited movies */
public class FavoritesFragment extends Fragment {

    private OnMovieSelectedListener listener;
    private MovieRecyclerView recyclerView;
    private MovieListAdapter movieListAdapter;
    private ArrayList<Movie> movieList;
    private MovieDbHelper movieDbHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieDbHelper = new MovieDbHelper(getContext());
        movieList = new ArrayList<>();

        // Enable options menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented this listener
        try {
            listener = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovieSelectedListener");
        }
    }

    /* Updates the list of favorited movies */
    public void updateFavorites() {
        movieList.clear();
        movieList.addAll(movieDbHelper.getFavoriteMoviesList());
        movieListAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate rootView
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Initialize RecyclerView
        recyclerView = (MovieRecyclerView) rootView.findViewById(R.id.favorites_grid);
        recyclerView.setHasFixedSize(true);
        movieListAdapter = new MovieListAdapter(getContext(), movieList, listener);
        recyclerView.setAdapter(movieListAdapter);

        updateFavorites();

        return rootView;
    }
}
