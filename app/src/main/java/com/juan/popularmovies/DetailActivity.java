package com.juan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.juan.popularmovies.DetailFragment.OnFavoritesChangedListener;

public class DetailActivity extends AppCompatActivity implements OnFavoritesChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            // Add DetailFragment to the ContentView
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public void onFavoritesChanged() {}
}
