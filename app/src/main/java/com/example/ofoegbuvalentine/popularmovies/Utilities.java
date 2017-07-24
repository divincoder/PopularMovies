package com.example.ofoegbuvalentine.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Ofoegbu Valentine on 16/04/2017.
 */

public class Utilities {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static AlertDialog.Builder showDialog(Context context, int icon, int message) {
        return new AlertDialog.Builder(context)
                .setMessage(message)
                .setIcon(icon);
    }

    public static void showToast(Context context, String message, int length) {
        Toast.makeText(context, message, length).show();
    }
}
