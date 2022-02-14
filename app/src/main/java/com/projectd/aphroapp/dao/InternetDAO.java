package com.projectd.aphroapp.dao;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.firebase.database.FirebaseDatabase;

import java.net.InetAddress;

public class InternetDAO {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

