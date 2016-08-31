package com.juan.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.juan.popularmovies.DetailFragment.OnFavoritesChangedListener;
import com.juan.popularmovies.adapter.MovieListAdapter.OnMovieSelectedListener;
import com.juan.popularmovies.adapter.TabAdapter;
import com.juan.popularmovies.model.Movie;

public class MainActivity extends AppCompatActivity implements OnMovieSelectedListener, OnFavoritesChangedListener {

    private boolean dualPane;
    private TabbedFragment tabbedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            dualPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment())
                        .commit();
            }
        }

        if (savedInstanceState == null) {
            // Add DetailFragment to the ContentView
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TabbedFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onFavoritesChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu for this activity
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Start the SettingsActivity if the Settings option was selected
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable("com.juan.popularmovies.model.Movie", movie);

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);
        detailFragment.setMovie(movie);

        if (dualPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment)
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtras(args);
            startActivity(intent);
        }
    }

    @Override
    public void onFavoritesChanged() {
        TabAdapter tabAdapter = tabbedFragment.getTabAdapter();
        FavoritesFragment favoritesFragment = (FavoritesFragment) tabAdapter.getFavoritesFragment();
        if (favoritesFragment != null) {
            favoritesFragment.updateFavorites();
        }
    }

    public void setTabbedFragment(TabbedFragment tabbedFragment) {
        this.tabbedFragment = tabbedFragment;
    }
}
