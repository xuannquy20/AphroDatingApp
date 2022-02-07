package com.projectd.aphroapp.dao;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;

public class InternetDAO {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

