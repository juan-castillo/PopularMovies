package com.juan.popularmovies.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.juan.popularmovies.FavoritesFragment;
import com.juan.popularmovies.MovieListFragment;

public class TabAdapter extends FragmentPagerAdapter {

    private static final String ALL_MOVIES = "All";
    private static final String FAVORITE_MOVIES = "Favorites";

    private MovieListFragment movieListFragment;
    private FavoritesFragment favoritesFragment;


    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getMovieListFragment() {
        return movieListFragment;
    }

    public Fragment getFavoritesFragment() {
        return favoritesFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MovieListFragment();
            case 1:
                return new FavoritesFragment();
            default:
                return null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        switch (position) {
            case 0:
                movieListFragment = (MovieListFragment) fragment;
                break;
            case 1:
                favoritesFragment = (FavoritesFragment) fragment;
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return ALL_MOVIES;
            case 1:
                return FAVORITE_MOVIES;
            default:
                return null;
        }
    }
}
