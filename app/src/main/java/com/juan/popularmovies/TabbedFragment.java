package com.juan.popularmovies;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juan.popularmovies.adapter.TabAdapter;

public class TabbedFragment extends Fragment {

    private TabAdapter tabAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabAdapter = new TabAdapter(getChildFragmentManager());
        ((MainActivity) getActivity()).setTabbedFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);

        // Setup tabs
        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(tabAdapter);
        tabs.setupWithViewPager(pager);

        return rootView;
    }

    public TabAdapter getTabAdapter() {
        return tabAdapter;
    }
}
