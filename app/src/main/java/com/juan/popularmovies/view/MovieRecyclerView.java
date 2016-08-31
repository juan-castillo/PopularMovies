package com.juan.popularmovies.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.juan.popularmovies.R;

/** RecyclerView class that automatically detects the number of columns to span.
 *  Uses a GridLayoutManager as its LayoutManager */
public class MovieRecyclerView extends RecyclerView {

    private GridLayoutManager gridLayoutManager;
    private float columnWidth;

    public MovieRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupGridLayoutManager();
    }

    private void setupGridLayoutManager() {
        // Get column width in pixels
        columnWidth = getContext().getResources().getInteger(R.integer.column_width);
        columnWidth = columnWidth * Resources.getSystem().getDisplayMetrics().density;

        // Calculate columns/span
        int spanCount = (int) Math.max(1, getMeasuredWidth() / columnWidth);
        gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int spanCount = (int) Math.max(1, getMeasuredWidth() / columnWidth);
        gridLayoutManager.setSpanCount(spanCount);
    }

}
