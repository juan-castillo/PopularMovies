package com.juan.popularmovies.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.juan.popularmovies.R;

public class Utils {

    /** Returns the preferred movie sorting order*/
    public static String getSortingPreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prefSortKey = context.getString(R.string.pref_sorting_key);
        String prefSortDefault = context.getString(R.string.pref_sorting_default);

        return prefs.getString(prefSortKey, prefSortDefault);
    }

    /** Checks if there is an active internet connection*/
    public static boolean hasConnectivity(Context context) {
        ConnectivityManager mngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mngr.getActiveNetworkInfo();

        return (info != null && info.getState() == NetworkInfo.State.CONNECTED);
    }
}
