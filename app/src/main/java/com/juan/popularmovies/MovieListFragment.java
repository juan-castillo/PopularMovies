package com.juan.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.juan.popularmovies.adapter.MovieListAdapter;
import com.juan.popularmovies.adapter.MovieListAdapter.OnMovieSelectedListener;
import com.juan.popularmovies.asynctask.FetchMovieListTask;
import com.juan.popularmovies.model.Movie;
import com.juan.popularmovies.util.Utils;
import com.juan.popularmovies.view.MovieRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

/** Fragment used to show a grid of movies sorted by either popularity or ratings */
public class MovieListFragment extends Fragment {

    private static final String KEY_MOVIE_LIST = "MovieList";
    private static final String KEY_SORT_ORDER = "SortOrder";

    private OnMovieSelectedListener listener;
    private MovieRecyclerView recyclerView;
    private MovieListAdapter movieListAdapter;
    private ArrayList<Movie> movieList;

    private String sortOrder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    /** Replaces the movies arraylist and notifies the RecyclerView Adapter that the dataset
     * has changed. Also moves the scroll position to 0 */
    public void replaceMovieList(Movie[] movies) {
        if (movies != null) {
            movieList.clear();
            movieList.addAll(Arrays.asList(movies));

            movieListAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate rootView
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        if (savedInstanceState != null) {
            // Restore movieList and sortOrder
            movieList = savedInstanceState.getParcelableArrayList(KEY_MOVIE_LIST);
            sortOrder = savedInstanceState.getString(KEY_SORT_ORDER);
        }

        // Initialize the RecyclerView
        recyclerView = (MovieRecyclerView) rootView.findViewById(R.id.movies_grid);
        recyclerView.setHasFixedSize(true);
        movieListAdapter = new MovieListAdapter(getContext(), movieList, listener);
        recyclerView.setAdapter(movieListAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the state of the LayoutManager, the list of movies and the current sort order
        savedInstanceState.putParcelableArrayList(KEY_MOVIE_LIST, movieList);
        savedInstanceState.putString(KEY_SORT_ORDER, sortOrder);

        super.onSaveInstanceState(savedInstanceState);
    }

    /** Returns true if the sortOrder is null or has changed*/
    private boolean sortOrderChanged() {
        if (sortOrder != null) {
            String newSortOrder = Utils.getSortingPreference(getContext());
            if (sortOrder.equals(newSortOrder)) {
                return false;
            }
        }

        return true;
    }

    /** Calls FetchMoviesTask to attempt to update the view with a list of movies based on sort order*/
    private void updateMovies() {
        // Only update the movie list if the sortOrder has changed to avoid
        // unnecessary network calls/http requests
        if (sortOrderChanged()) {
            if (Utils.hasConnectivity(getContext())) {
                sortOrder = Utils.getSortingPreference(getContext());
                new FetchMovieListTask(this).execute(sortOrder);

            } else {
                CharSequence text = "No internet connectivity, please try again later";
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
